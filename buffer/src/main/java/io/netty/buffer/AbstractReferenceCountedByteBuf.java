/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.netty.buffer;

import io.netty.util.IllegalReferenceCountException;
import io.netty.util.internal.PlatformDependent;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

import static io.netty.util.internal.ObjectUtil.checkPositive;

/**
 * Abstract base class for {@link ByteBuf} implementations that count references.
 */
public abstract class AbstractReferenceCountedByteBuf extends AbstractByteBuf {
    private static final long REFCNT_FIELD_OFFSET;
    //采用 AtomicIntegerFieldUpdater 对象，CAS方式更新refCnt
    private static final AtomicIntegerFieldUpdater<AbstractReferenceCountedByteBuf> refCntUpdater =
            AtomicIntegerFieldUpdater.newUpdater(AbstractReferenceCountedByteBuf.class, "refCnt");

    //refCnt 实际值为偶数，采用位移操作提高效率
    // even => "real" refcount is (refCnt >>> 1); odd => "real" refcount is 0
    @SuppressWarnings("unused")
    private volatile int refCnt = 2;

    static {
        long refCntFieldOffset = -1;
        try {
            if (PlatformDependent.hasUnsafe()) {
                refCntFieldOffset = PlatformDependent.objectFieldOffset(
                        AbstractReferenceCountedByteBuf.class.getDeclaredField("refCnt"));
            }
        } catch (Throwable ignore) {
            refCntFieldOffset = -1;
        }

        REFCNT_FIELD_OFFSET = refCntFieldOffset;
    }

    private static int realRefCnt(int rawCnt) {
        return (rawCnt & 1) != 0 ? 0 : rawCnt >>> 1;
    }

    protected AbstractReferenceCountedByteBuf(int maxCapacity) {
        super(maxCapacity);
    }

    private int nonVolatileRawCnt() {
        // TODO: Once we compile against later versions of Java we can replace the Unsafe usage here by varhandles.
        return REFCNT_FIELD_OFFSET != -1 ? PlatformDependent.getInt(this, REFCNT_FIELD_OFFSET)
                : refCntUpdater.get(this);
    }

    @Override
    boolean isAccessible() {
        // Try to do non-volatile read for performance as the ensureAccessible() is racy anyway and only provide
        // a best-effort guard.

        // This is copied explicitly from the nonVolatileRawCnt() method above to reduce call stack depth,
        // to avoid hitting the default limit for inlining (9)
        final int rawCnt = REFCNT_FIELD_OFFSET != -1 ? PlatformDependent.getInt(this, REFCNT_FIELD_OFFSET)
                : refCntUpdater.get(this);

        // The "real" ref count is > 0 if the rawCnt is even.
        // (x & y) appears to be surprisingly expensive relative to (x == y). Thus the expression below provides
        // a fast path for most common cases where the ref count is 1, 2, 3 or 4.
        return rawCnt == 2 || rawCnt == 4 || rawCnt == 6 || rawCnt == 8 || (rawCnt & 1) == 0;
    }

    @Override
    public int refCnt() {
        return realRefCnt(refCntUpdater.get(this));
    }

    /**
     * An unsafe operation intended for use by a subclass that sets the reference count of the buffer directly
     */
    protected final void setRefCnt(int newRefCnt) {
        refCntUpdater.set(this, newRefCnt << 1); // overflow OK here
    }

    @Override
    public ByteBuf retain() {
        return retain0(1);
    }

    @Override
    public ByteBuf retain(int increment) {
        return retain0(checkPositive(increment, "increment"));
    }

    //计数器增值操作
    private ByteBuf retain0(final int increment) {
        // all changes to the raw count are 2x the "real" change
        int adjustedIncrement = increment << 1; // overflow OK here  真正的计数都是2倍递增
        int oldRef = refCntUpdater.getAndAdd(this, adjustedIncrement); //通过CAS方式递增并获取原值
        if ((oldRef & 1) != 0) {//判断奇偶，正常情况这里应该都是偶数
            throw new IllegalReferenceCountException(0, increment);
        }
        // don't pass 0!  如果计数小于等于0，以及整型范围越界（0x7fffffff+1）抛出异常
        if ((oldRef <= 0 && oldRef + adjustedIncrement >= 0)
                || (oldRef >= 0 && oldRef + adjustedIncrement < oldRef)) {
            // overflow case
            refCntUpdater.getAndAdd(this, -adjustedIncrement);
            throw new IllegalReferenceCountException(realRefCnt(oldRef), increment);
        }
        return this;
    }

    @Override
    public ByteBuf touch() {
        return this;
    }

    @Override
    public ByteBuf touch(Object hint) {
        return this;
    }

    @Override
    public boolean release() {
        return release0(1);
    }

    @Override
    public boolean release(int decrement) {
        return release0(checkPositive(decrement, "decrement"));
    }

    //计数器减值操作
    private boolean release0(int decrement) {
        int rawCnt = nonVolatileRawCnt(), realCnt = toLiveRealCnt(rawCnt, decrement); //对计数器进行除以2操作,也就是引用次数
        /**
         * /这里如注意 你传入的减值参数decrement  = realCnt 时 等同于 引用次数=释放次数，直接进行释放操作
         */
        if (decrement == realCnt) {
            if (refCntUpdater.compareAndSet(this, rawCnt, 1)) { //CAS方式置为1
                deallocate();//内存释放或放回内存池
                return true;
            }
            return retryRelease0(decrement);//进入具体操作
        }
        return releaseNonFinal0(decrement, rawCnt, realCnt);
    }

    private boolean releaseNonFinal0(int decrement, int rawCnt, int realCnt) {
        //如果decrement 小于 realCnt，通过CAS方式减去decrement*2
        if (decrement < realCnt
                // all changes to the raw count are 2x the "real" change
                && refCntUpdater.compareAndSet(this, rawCnt, rawCnt - (decrement << 1))) {
            return false;
        }
        return retryRelease0(decrement);
    }

    private boolean retryRelease0(int decrement) {
        for (;;) {
            int rawCnt = refCntUpdater.get(this), realCnt = toLiveRealCnt(rawCnt, decrement);
            if (decrement == realCnt) {
                if (refCntUpdater.compareAndSet(this, rawCnt, 1)) {
                    deallocate();
                    return true;
                }
            } else if (decrement < realCnt) {//如果decrement 小于 realCnt，通过CAS方式减去decrement*2
                // all changes to the raw count are 2x the "real" change
                if (refCntUpdater.compareAndSet(this, rawCnt, rawCnt - (decrement << 1))) {
                    return false;
                }
            } else {
                throw new IllegalReferenceCountException(realCnt, -decrement);
            }
            Thread.yield(); // this benefits throughput under high contention
        }
    }

    /**
     * Like {@link #realRefCnt(int)} but throws if refCnt == 0
     */
    private static int toLiveRealCnt(int rawCnt, int decrement) {
        if ((rawCnt & 1) == 0) {
            return rawCnt >>> 1;
        }
        // odd rawCnt => already deallocated
        throw new IllegalReferenceCountException(0, -decrement);
    }

    /**
     * Called once {@link #refCnt()} is equals 0.
     */
    protected abstract void deallocate();
}
