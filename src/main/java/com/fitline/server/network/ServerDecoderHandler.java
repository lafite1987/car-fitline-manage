package com.fitline.server.network;

import com.fitline.server.LogUtil;
import com.fitline.server.TcpServer;
import com.fitline.server.network.message.Request;
import com.fitline.server.network.message.Request.RequestFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * <pre>
 * <br>客户端直接连接到服务器
 * 	+-------+-------+---+------+
 *	|  size |version|cmd| data |
 *	+---------------+---+------+
 *	|   4   |   2   | 2 |byte[]|
 *	+-------+-------+---+------+
 *  <br>size = 4 + 2 + 2 + data.length;
 *	</pre>
 * @author Leo.liao
 *
 */
public class ServerDecoderHandler extends LengthFieldBasedFrameDecoder {
	
    public ServerDecoderHandler(TcpServer tcpServer) {
        super(4096, 0, 4, -4, 0);
    }

    @Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
    	ByteBuf buffer = (ByteBuf)super.decode(ctx, in);
    	if(buffer != null) {
        	int length = buffer.readInt();
        	int version = buffer.readShort();
        	int cmd = buffer.readShort();
        	byte[] data = new byte[length - 8];
        	if(data.length > 0) {
        		buffer.readBytes(data);
        	}
    		Request request = RequestFactory.createRequest(length, version, cmd, data);
    		LogUtil.LOG.info("{}", request);
    		return request;
    	}
		return null;
	}

}
