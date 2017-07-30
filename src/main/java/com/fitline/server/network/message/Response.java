package com.fitline.server.network.message;

import io.netty.channel.Channel;

public class Response {

	private int cmd;
	
	private int version;
	
	private int errorCode;
	
	private Channel channel;
	
	public Response(int cmd, int version, Channel channel) {
		this.channel = channel;
		this.cmd = cmd;
		this.version = version;
	}
	
	private Packet packet;

	public int getCmd() {
		return cmd;
	}

	public int getVersion() {
		return version;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public Packet getPacket() {
		return packet;
	}
	
	public Channel channel() {
		return channel;
	}
	public void write(Packet packet) {
		this.packet = packet;
		channel.writeAndFlush(this);
	}
	
	
}
