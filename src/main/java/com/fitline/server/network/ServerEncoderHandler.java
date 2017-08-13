package com.fitline.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fitline.server.network.message.Packet;
import com.fitline.server.network.message.Response;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <pre>
 * <br>服务器编码器，编码输出给客户端
 * 	+-------+-------+---+-------+------+
 *	|  size |version|cmd|errCode| data |
 *	+---------------+---+-------+------+
 *	|   4   |   2   | 2 |   2   |byte[]|
 *	+-------+-------+---+-------+------+
 *  <br>size = 4 + 2 + 2 + 2 + data.length;
 *	</pre>
 * @author Leo.liao
 *
 */
public class ServerEncoderHandler extends MessageToByteEncoder<Response> {

	private static final Logger LOG = LoggerFactory.getLogger(ServerEncoderHandler.class.getName());
	
	private static final int LENGTH = 10;
	@Override
	protected void encode(ChannelHandlerContext ctx, Response response, ByteBuf out)
			throws Exception {
		Packet packet = response.getPacket();
		int length = LENGTH;
		int cmd = response.getCmd();
		int version = response.getVersion();
		int errorCode = response.getErrorCode();
		byte[] data = null;
		String content = null;
		if(packet != null) {
			content = packet.toJSON();
			data = content.getBytes("UTF-8");
			length += data.length;
		}
		
		out.writeInt(length);
		out.writeShort(version);
		out.writeShort(cmd);
		out.writeShort(errorCode);
		LOG.info("Response[length={}, version={} cmd={}, errorCode={}]", new Object[]{length, version, cmd, errorCode});
		if(data != null && data.length > 0) {
			out.writeBytes(data);
		}
	}

}
