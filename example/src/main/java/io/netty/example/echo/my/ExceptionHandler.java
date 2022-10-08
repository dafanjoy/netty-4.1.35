package io.netty.example.echo.my;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
* @author 作者
* @version 创建时间：2020年4月26日 下午7:37:00
* 类说明
*/
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println(this.getClass().getName() + "---" + cause.getMessage());
	}
}
