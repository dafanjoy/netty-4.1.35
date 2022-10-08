package io.netty.example.echo.内存分配;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
* @author 作者
* @version 创建时间：2020年6月18日 下午4:17:30
* 类说明
*/
public class BytebufHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	
       	ByteBuf recvmg = (ByteBuf) msg;// 申请ByteBuf 需要主动释放
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(true);
        ByteBuf nBuffer = allocator.buffer();
        System.err.println(nBuffer.refCnt());
        nBuffer.retain();
        System.err.println(nBuffer.refCnt());
        nBuffer.retain();
        System.err.println(nBuffer.refCnt());
        nBuffer.release();
        System.err.println(nBuffer.refCnt());
        System.err.println(nBuffer.refCnt());
        ByteBuf buf = Unpooled.buffer();
        buf.retain();
        // System.err.println(buf.refCnt());
        ByteBuf dicbuf = Unpooled.directBuffer();
        //System.err.println(dicbuf.refCnt());
        dicbuf.retain();
        //System.err.println(dicbuf.refCnt());

    	try {
        	if(recvmg.isDirect()) {
        		System.out.println("recvmg  true");
        	}

        	if(buf.isDirect()){
                System.out.println(" buf  true");
            }

            if(dicbuf.isDirect()){
                System.out.println(" dicbuf  true");
            }

       

        	//System.out.println("CodecHandler收到数据："+ByteUtils.toHexString((byte[])msg));
        	byte[] sendBytes = new byte[] {0x7E,0x01,0x02,0x7e};
            nBuffer.writeBytes(sendBytes);
            ctx.writeAndFlush(nBuffer);
    	}catch (Exception e) {
			// TODO: handle exception
		}finally {
			recvmg.release(); //此处释放一次，由Tail节点最后再释放一次
		}
 
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
