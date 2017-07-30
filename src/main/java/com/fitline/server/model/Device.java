package com.fitline.server.model;

import com.fitline.server.network.message.Packet;
import com.fitline.server.network.message.Response;

import io.netty.channel.Channel;

public class Device {

	private String no;
	
	private Channel channel;

	public Device(String no, Channel channel) {
		super();
		this.no = no;
		this.channel = channel;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}
	
	public void write(int cmd, int version, Packet packet) {
		if(channel != null && channel.isActive()) {
			new Response(cmd, version, channel).write(packet);
		}
	}

	@Override
	public String toString() {
		return "Device [no=" + no + "]";
	}
	
	
}
