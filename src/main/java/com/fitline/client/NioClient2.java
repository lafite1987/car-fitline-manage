package com.fitline.client;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.fitline.server.model.DeviceAuthReq;
import com.fitline.server.model.DeviceAuthResp;

public class NioClient2 extends Thread {

	private final static Logger LOG = LoggerFactory.getLogger(NioClient2.class);
	
	public static interface ConnectFuture {
		void finish(NioClient2 client);
	}
	private Selector selector;
	private SocketChannel channel;
	private String no;

	private ScheduledFuture<?> scheduledFuture;
	
	public NioClient2(String no) {
		this.no = no;
		setDaemon(true);
	}
	public void connect(String host, int port, ConnectFuture future) throws IOException {
		channel = SocketChannel.open();
		channel.configureBlocking(false);
		channel.connect(new InetSocketAddress(host, port));

		selector = Selector.open();
		channel.register(selector, SelectionKey.OP_CONNECT);
		process(future);
	}

	public void process(final ConnectFuture future) throws IOException {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					while (true) {
						int keyCount = selector.select();
						if (keyCount <= 0) {
							continue;
						}
						Set<SelectionKey> readyKeys = selector.selectedKeys();
						Iterator<SelectionKey> keyIt = readyKeys.iterator();
						while (keyIt.hasNext()) {
							SelectionKey key = keyIt.next();
							keyIt.remove();
							if (key.isConnectable()) {
								SocketChannel channel = (SocketChannel) key.channel();
								if (channel.isConnectionPending()) {
									channel.finishConnect();
								}
								channel.configureBlocking(false);
								future.finish(NioClient2.this);
								break;
							} else if (key.isReadable()) {
								SocketChannel channel = (SocketChannel) key.channel();
								NioClient2.this.read(channel);
							}
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		thread.setName("Thread-" + NioClient2.this.no);
		thread.start();
	}

	public void heartBeat(SocketChannel channel) {

	}

	private byte[] _read(SocketChannel channel, int length) throws IOException {
		int nrecvd = 0;
		byte[] data = new byte[length];
		ByteBuffer buffer = ByteBuffer.wrap(data);
		try {
			while (nrecvd < length) {
				long n = channel.read(buffer);
				if (n < 0)
					throw new EOFException();
				nrecvd += (int) n;
			}
		} finally {

		}
		return data;
	}

	public void read(SocketChannel channel) throws IOException {
		byte[] buf = _read(channel, 2);
		int length = ((buf[0] & 0xFF) << 8) + (buf[1] & 0xFF);
		byte[] recvData = _read(channel, length);
		ByteBuffer buffer = ByteBuffer.wrap(recvData);
		int sum = buffer.get();
		int cmd = buffer.getShort();
		LOG.info("msg[sum=" + sum + ",length=" + length + ",cmd=" + cmd + "]");
		if(cmd != 1022) {
			byte[] dst = new byte[length - 3];
			buffer.get(dst);
			String xml = new String(dst, "UTF-8");
			LOG.info("Gateway response XML=" + xml);
			switch (cmd) {
			case 1002:
				loginReponse(xml);
				break;
			case 1004:
				break;
			default:
				break;
			}
		}
	}

	public void write(byte[] bytes) throws IOException {
		ByteBuffer requestBuffer = ByteBuffer.wrap(bytes);
		while (requestBuffer.hasRemaining()) {
			channel.write(requestBuffer);
		}
		channel.register(selector, SelectionKey.OP_READ);

	}
	
	public void write(ByteBuffer buf) throws IOException {
		while (buf.hasRemaining()) {
			channel.write(buf);
		}
		channel.register(selector, SelectionKey.OP_READ);

	}
	static final ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
	
	public static void login(final String no, final String key) throws Exception {
		NioClient2 client = new NioClient2(no);
		client.connect("127.0.0.1", 9000, new ConnectFuture() {
			
			@Override
			public void finish(final NioClient2 client) {
				//登录
				DeviceAuthReq req = new DeviceAuthReq();
				req.setNo(no);
				req.setToken(key);
				String content = JSON.toJSONString(req);
				byte[] bytes;
				try {
					bytes = content.getBytes("UTF-8");
					ByteBuffer body = ByteBuffer.allocate(2 + bytes.length);
					body.putShort((short)1001);
					body.put(bytes);
					body.flip();
					byte[] bodyBytes = body.array();
					int sum = sum(bodyBytes);
					
					ByteBuffer buf = ByteBuffer.allocate(3 + bodyBytes.length);
					
					buf.putShort((short)(bodyBytes.length + 1));
					buf.put((byte)sum);
					buf.put(bodyBytes);
					buf.flip();
					client.write(buf);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	private void loginReponse(String xml) {
		DeviceAuthResp nodeAuthResp = JSON.parseObject(xml, DeviceAuthResp.class);
		if("0".equals(nodeAuthResp.getAuth())) {
			ClientManager.add(nodeAuthResp.getNo(), NioClient2.this);
			scheduledFuture = scheduled.scheduleWithFixedDelay(new Runnable() {
				
				@Override
				public void run() {
					try {
						ByteBuffer body = ByteBuffer.allocate(2);
						body.putShort((short)1021);
						body.flip();
						byte[] bodyBytes = body.array();
						int sum = sum(bodyBytes);
						
						ByteBuffer buf = ByteBuffer.allocate(3 + bodyBytes.length);
						
						buf.putShort((short)(bodyBytes.length + 1));
						buf.put((byte)sum);
						buf.put(bodyBytes);
						buf.flip();
						NioClient2.this.write(buf);
					} catch(Exception e) {
						System.err.println(e);
						ClientManager.remove(no);
						close();
					}
				}
			}, 120, 120, TimeUnit.SECONDS);
		}
	}
	
	public void send(int cmd, Object obj) {
		String content = JSON.toJSONString(obj);
		byte[] bytes;
		try {
			bytes = content.getBytes("UTF-8");
			ByteBuffer body = ByteBuffer.allocate(2 + bytes.length);
			body.putShort((short)cmd);
			body.put(bytes);
			body.flip();
			byte[] bodyBytes = body.array();
			int sum = sum(bodyBytes);
			
			ByteBuffer buf = ByteBuffer.allocate(3 + bodyBytes.length);
			
			buf.putShort((short)(bodyBytes.length + 1));
			buf.put((byte)sum);
			buf.put(bodyBytes);
			buf.flip();
			write(buf);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			scheduledFuture.cancel(true);
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ClientManager.remove(no);
	}
	
	public static int sum(byte[] bytes) {
		int sum = 0;
		for(byte b : bytes) {
			sum +=b;
		}
		sum = sum & 0XFF;
		return sum;
	}
	
	public static void shutdown() {
		scheduled.shutdownNow();
	}
	public static void main(String[] args) {
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
