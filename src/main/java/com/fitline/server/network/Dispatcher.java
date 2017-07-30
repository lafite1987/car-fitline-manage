package com.fitline.server.network;

import com.fitline.server.LogUtil;
import com.fitline.server.Processor;
import com.fitline.server.Processor.HandlerInfo;
import com.fitline.server.context.Handler;
import com.fitline.server.network.message.Request;
import com.fitline.server.network.message.Response;

import io.netty.channel.Channel;

public class Dispatcher {

	public static void process(Request request, Channel channel) {
		int cmd = request.cmd();
		int version = request.version();
		HandlerInfo handlerInfo = Processor.getHandler(cmd, version);
		if(handlerInfo == null) {
			LogUtil.LOG.warn("Handler {} Not Found", cmd);
		}
		if(handlerInfo != null) {
			try {
				Handler handler = handlerInfo.getHandler();
				handler.handle(request, new Response(handlerInfo.getCmd().value(), 
						handlerInfo.getCmd().version(), channel));
			} catch (Throwable e) {
				LogUtil.LOG.error("Dispatcher Process {} Exception", request, e);
			}
		}
	}
}
