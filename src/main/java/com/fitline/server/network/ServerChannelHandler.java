package com.fitline.server.network;

import com.fitline.server.TcpServer;
import com.fitline.server.disruptor.DisruptorEvent;
import com.fitline.server.network.message.Request;
import com.fitline.server.network.message.RequestWrapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ServerChannelHandler extends SimpleChannelInboundHandler<Request> {

	private TcpServer tcpServer;
	private Context context;
	
	public ServerChannelHandler(TcpServer tcpServer) {
		this.tcpServer = tcpServer;
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		DisruptorEvent worker = null;
		if(context == null) {
			worker = tcpServer.getBusinessThread(ctx.channel().id().asLongText());
			this.context = new Context(worker);
		}
		super.channelActive(ctx);
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Request request)
			throws Exception {
		DisruptorEvent worker = this.context.getDisruptorEvent();
		worker.publish(new Runnable() {
			
			@Override
			public void run() {
				Dispatcher.process(new RequestWrapper(request, context), ctx.channel());
			}
		});
	}

}
