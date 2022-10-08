package io.netty.example.echo.my;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author 作者
 * @version 创建时间：2020年1月4日 上午10:38:22 类说明
 */
public class ServerHandlerC extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object object) {
		System.out.println(this.getClass().getName() + "--"+object.toString());
		ctx.fireChannelRead(object);
	}
	
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
    	System.err.println(this.getClass().getName()+"---"+cause.getMessage());
        ctx.fireExceptionCaught(cause);
    }
}
