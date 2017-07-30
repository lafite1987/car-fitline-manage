package com.fitline.server;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fitline.server.disruptor.DisruptorEvent;
import com.fitline.server.model.Device;
import com.fitline.server.network.ServerChannelHandler;
import com.fitline.server.network.ServerDecoderHandler;
import com.fitline.server.network.ServerEncoderHandler;
import com.google.common.collect.Maps;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.DefaultThreadFactory;
/**
 * TCP Server
 * @author leo.liao
 *
 */
public class TcpServer {

	private int bossThread;
	private int ioThread;
	private int port;
	static ServerBootstrap serverBootstrap;

	EventLoopGroup bossGroup;
	EventLoopGroup workerGroup;
	
	private static ChannelGroup allChannels = new DefaultChannelGroup(new DefaultEventExecutorGroup(1).next());
	private static Logger logger = LoggerFactory.getLogger(TcpServer.class);
	
	private static Map<String, Device> ONLINE_DEVICE_MAP = Maps.newConcurrentMap();
	private static Map<String, Device> CHANNEL_DEVICE_MAP = Maps.newConcurrentMap();
	
	public void start() {
		port = Integer.parseInt(System.getProperty("tcp.server.port", "9000"));
		logger.info("Get TCP Port[{}], Start TCPServer...", port);
		bossGroup = new NioEventLoopGroup(bossThread, new DefaultThreadFactory("Boss_Thread"));
		workerGroup = new NioEventLoopGroup(ioThread, new DefaultThreadFactory("Io_Thread"));
		
		try {
			serverBootstrap = new ServerBootstrap();
			serverBootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.SO_RCVBUF, 1048576)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline()
							.addLast("idleStateHandler", new IdleStateHandler(300, 300, 0))
							.addLast(new ServerDecoderHandler(TcpServer.this))
							.addLast("encoder", new ServerEncoderHandler())
							.addLast(new ServerChannelHandler(TcpServer.this))
							;
					}
				});
			allChannels.add(serverBootstrap.bind(port).channel());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		initBusinessThread();
		logger.info("TCPServer start Finish, Listen Port[{}].", port);
	}
	
	public void shutdown() {
		logger.info("Stop TCPServer...");
		try {
			ChannelGroupFuture future = allChannels.close();
			future.awaitUninterruptibly();
			for (int i = 0; i < 4; i++) {
				businessThread[i].shutdown();
			}
			logger.info("Stop TCPServer Finish.");
		} catch(Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	private DisruptorEvent[] businessThread;
	
	private void initBusinessThread() {
		businessThread = new DisruptorEvent[4];
		for (int i = 0; i < 4; i++) {
			businessThread[i] = new DisruptorEvent("Business_Thread_" + i, 1);
		}
	}
	public void setPort(int port) {
		this.port = port;
	}
	public void setBossThread(int bossThread) {
		this.bossThread = bossThread;
	}
	public void setIoThread(int ioThread) {
		this.ioThread = ioThread;
	}
	public DisruptorEvent getBusinessThread(String no) {
		int code = Math.abs(no.hashCode());
		return businessThread[code & 3];
	}
	
	/**
	 * Add Online Device
	 * @param no
	 * @param channel
	 */
	public void regist(String no, Device device) {
		Channel channel = device.getChannel();
		ONLINE_DEVICE_MAP.put(no, device);
		CHANNEL_DEVICE_MAP.put(channel.id().asLongText(), device);
	}
	/**
	 * By No Get Device
	 * @param no
	 * @return
	 */
	public Device getDevice(String no) {
		return ONLINE_DEVICE_MAP.get(no);
	}
	/**
	 * By channelId Get Device
	 * @param channelId
	 * @return
	 */
	public Device getDeviceByChannelId(String channelId) {
		return CHANNEL_DEVICE_MAP.get(channelId);
	}
	
}
