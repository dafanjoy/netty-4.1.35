package io.netty.example.echo.内存分配;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.example.echo.util.BytesUtils;

/**
* @author 作者
* @version 创建时间：2020年6月18日 下午4:17:30
* 类说明
*/
public class NBytebufHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	
       	ByteBuf recvBuffer = (ByteBuf) msg;// 申请ByteBuf 需要主动释放
        if(recvBuffer.isDirect()){
            System.err.println(true);
        }
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(true);
        ByteBuf sendBuffer = allocator.buffer();//申请池化直接内存
        System.err.println("sendBuffer的引用计数:"+sendBuffer.refCnt());
        sendBuffer.retain();
        System.err.println("sendBuffer的引用计数:"+sendBuffer.refCnt());
        sendBuffer.release();
        System.err.println("sendBuffer的引用计数:"+sendBuffer.refCnt());


    	try {
            byte[] bytesReady = new byte[recvBuffer.readableBytes()];
            recvBuffer.readBytes(bytesReady);
        	System.out.println("channelRead收到数据："+ BytesUtils.toHexString(bytesReady));
        	byte[] sendBytes = new byte[] {0x7E,0x01,0x02,0x7e};
            sendBuffer.writeBytes(sendBytes);
            ctx.writeAndFlush(sendBuffer);
            System.err.println("sendBuffer的引用计数:"+sendBuffer.refCnt());
    	}catch (Exception e) {
			// TODO: handle exception
            System.err.println(e.getMessage());
		}finally {
            System.err.println("recvBuffer的引用计数:"+recvBuffer.refCnt());
            recvBuffer.release(); //此处需要释放
            System.err.println("recvBuffer的引用计数:"+recvBuffer.refCnt());
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
