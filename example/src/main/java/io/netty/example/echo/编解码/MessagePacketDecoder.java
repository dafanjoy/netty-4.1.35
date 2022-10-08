package io.netty.example.echo.编解码;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * <pre>
 * 原始协议消息解码器
 * </pre>
 *
 * @author jianshaofan
 * @version 创建时间：2019年4月17日 上午10:15:41 类说明
 */
public class MessagePacketDecoder extends ByteToMessageDecoder {
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out){
		try {
			if (buffer.readableBytes() > 0) {
				// 待处理的消息包
				byte[] bytesReady = new byte[buffer.readableBytes()];
				buffer.readBytes(bytesReady);
				//进行具体的解码处理
				System.out.println("解码器收到数据："+ByteUtils.toHexString(bytesReady));
				//这里不做过多处理直接把收到的消息放入链表中，并向后传递
				out.add(bytesReady);
			
			}
		}catch(Exception ex) {
			
		}

	}

}
