package io.netty.example.echo.test;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
* @author 作者
* @version 创建时间：2020年6月18日 下午4:17:30
* 类说明
*/
public class CodecHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
    	System.out.println("CodecHandler收到数据："+ByteUtils.toHexString((byte[])msg));
    	byte[] sendBytes = new byte[] {0x7E,0x01,0x02,0x7e};
        ctx.write(sendBytes);
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
