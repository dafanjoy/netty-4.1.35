package io.netty.example.echo.my;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

/**
 * @author 作者
 * @version 创建时间：2020年1月4日 上午10:37:50 类说明
 */
public class ServerHandlerA extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object object) {
//    	ByteBuf byteBuf = (ByteBuf) object;
//    	ByteBuf byteBuf1 = (ByteBuf) byteBuf;
//    	
//    	user(byteBuf1);
//    	byteBuf1.retain();
//    	ByteBuf outBuffer = ByteBufAllocator.DEFAULT.buffer();
//    	try {
//    	 	
//        	System.out.println(this.getClass().getName() + "--"+byteBuf.toString());
//        	System.out.println(this.getClass().getName() + "--"+outBuffer.toString());
//        	System.out.println(this.getClass().getName() + "--"+byteBuf1.toString());
//    	}finally {
//    		ReferenceCountUtil.release(byteBuf);
//		}

//        ByteBuf byteBuf = PooledByteBufAllocator.DEFAULT.buffer();
//        byteBuf.writeByte(2);
//        byteBuf.writeByte(1);
		// sctx.channel().write((ByteBuf)object);

//		ctx.write((ByteBuf) object);
		ctx.fireExceptionCaught(new Throwable("出现异常"));
		ctx.pipeline().fireExceptionCaught(new Throwable("出现异常"));
		// ctx.fireChannelRead(object);

	}

	void user(ByteBuf buf) {
		System.out.println(this.getClass().getName() + "--" + buf.toString());
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
//    	 ctx.channel().pipeline().fireChannelRead("hello word");
//    	 ctx.fireChannelRead("hello word");
		// ctx.write("123");
		// ctx.channel().write("123");
	}
//
//	@Override
//	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
//	 	System.out.println(this.getClass().getName() + "--"+msg);
//    	///ctx.fireChannelRead(object);
//		
//	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println(this.getClass().getName() + "---" + cause.getMessage());
		ctx.fireExceptionCaught(cause);
	}

}
