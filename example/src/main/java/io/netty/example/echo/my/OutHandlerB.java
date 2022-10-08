package io.netty.example.echo.my;

import java.io.WriteAbortedException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
* @author 作者
* @version 创建时间：2020年3月7日 上午11:16:47
* 类说明
*/
public class OutHandlerB extends ChannelOutboundHandlerAdapter {
	@Override
	public void write(ChannelHandlerContext ctx,Object msg,ChannelPromise promise) {
		System.out.println(this.getClass().getName()+"--"+msg);	
		ctx.write((ByteBuf)msg);
	}
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	System.err.println(this.getClass().getName()+"---"+cause.getMessage());
        ctx.fireExceptionCaught(cause);
    }
}
