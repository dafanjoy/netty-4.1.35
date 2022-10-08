package io.netty.example.echo.my;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
* @author 作者
* @version 创建时间：2020年1月4日 上午10:36:28
* 类说明
*/
public class ServerApp {
	public static void main(String[] args) {
		EventLoopGroup boss = new NioEventLoopGroup();
		EventLoopGroup work = new NioEventLoopGroup(2);
		try {
			ServerBootstrap bootstrap = new ServerBootstrap();
			bootstrap.childOption(ChannelOption.SO_SNDBUF,2);
			bootstrap.group(boss, work).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ChannelPipeline p = ch.pipeline();
							// p.addLast(new LoggingHandler(LogLevel.INFO));
							// 向ChannelPipeline中添加自定义channelHandler
							p.addLast(new OutHandlerA());
							p.addLast(new ServerHandlerA());
							p.addLast(new ServerHandlerB());
							p.addLast(new ServerHandlerC());
							p.addLast(new OutHandlerB());
							p.addLast(new OutHandlerC());
							p.addLast(new ExceptionHandler());
						
						}
					});
			bootstrap.bind(8050).sync();

		} catch (Exception e) {
			// TODO: handle exception
		}

	}

}
