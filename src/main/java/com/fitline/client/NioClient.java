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
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.fitline.server.model.DeviceAuthReq;

public class NioClient {
	
	private static final Logger logger = LoggerFactory.getLogger(NioClient.class);

	private Selector selector;
	private SocketChannel channel;

	public void connect(String host, int port) throws IOException {
		channel = SocketChannel.open();
		channel.configureBlocking(false);
		channel.connect(new InetSocketAddress(host, port));

		selector = Selector.open();
		channel.register(selector, SelectionKey.OP_CONNECT);
	}

	public void process() throws IOException {
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
					break;
				} else if (key.isReadable()) {
					SocketChannel channel = (SocketChannel) key.channel();
					this.read(channel);
				}
			}
		}
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
		byte[] buf = _read(channel, 4);
//		int length = ((buf[0] & 0xFF) << 8) + (buf[1] & 0xFF);
		ByteBuffer lenBuf = ByteBuffer.wrap(buf);
		int length = lenBuf.getInt();
		byte[] recvData = _read(channel, length - 4);
		ByteBuffer buffer = ByteBuffer.wrap(recvData);
		int version = buffer.getShort();
		int cmd = buffer.getShort();
		int errCode = buffer.getShort();
		logger.info("Response[version=" + version + ",length=" + length + ",cmd=" + cmd + ",errCode=" + errCode + "]");
		if(cmd != 1000) {
			byte[] dst = new byte[length - 10];
			buffer.get(dst);
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
	static NioClient client = null;
	public static void main(String[] args) {
		try {
			ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();
			
			scheduled.schedule(new Runnable() {
				
				@Override
				public void run() {
					//登录
					DeviceAuthReq req = new DeviceAuthReq();
					req.setNo("123456");
					req.setToken("1234567890");
					String content = JSON.toJSONString(req);
					byte[] bytes;
					try {
						bytes = content.getBytes("UTF-8");
						ByteBuffer body = ByteBuffer.allocate(8 + bytes.length);
						body.putInt(8 + bytes.length);
						body.putShort((short)0);
						body.putShort((short)1001);
						body.put(bytes);
						body.flip();
						logger.info("Request[{}]", req);
						client.write(body);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 3, TimeUnit.SECONDS);
			scheduled.scheduleWithFixedDelay(new Runnable() {
				
				@Override
				public void run() {
					try {
						ByteBuffer cmd = ByteBuffer.allocate(8);
						cmd.putInt(8);
						cmd.putShort((short)0);
						cmd.putShort((short)1000);
						cmd.flip();
						logger.info("Request[length={}, version={}, cmd={}]", new Object[]{8, 0, 1000});
						client.write(cmd);
					} catch(Exception e) {
						System.err.println(e);
					}
				}
			}, 5, 5, TimeUnit.SECONDS);
			client = new NioClient();
			client.connect("127.0.0.1", 9000);
			client.process();
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public static int sum(byte[] bytes) {
		int sum = 0;
		for(byte b : bytes) {
			sum +=b;
		}
		sum = sum & 0XFF;
		return sum;
	}
}