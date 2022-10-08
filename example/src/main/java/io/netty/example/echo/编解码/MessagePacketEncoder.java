package io.netty.example.echo.编解码;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.example.echo.BytesUtils;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;

/**
 * <pre>
 * 原始协议消息编码器
 * </pre>
 * 
 * @author jianshaofan
 * @version 创建时间：2019年4月17日 上午10:15:41 类说明
 */
public class MessagePacketEncoder extends MessageToByteEncoder<byte[]> {

	@Override
	protected void encode(ChannelHandlerContext ctx, byte[] bytes, ByteBuf out) throws Exception {
		//进行具体的编码处理 这里对字节数组进行打印
		System.out.println("编码器收到数据："+BytesUtils.toHexString(bytes));
		//写入并传送数据
		out.writeBytes(bytes);
	}
}
