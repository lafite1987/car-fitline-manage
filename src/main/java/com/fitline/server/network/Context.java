package com.fitline.server.network;

import com.fitline.server.disruptor.DisruptorEvent;
import com.fitline.server.model.Device;

public class Context {

	public Context(DisruptorEvent worker) {
		this.worker = worker;
	}
	
	private volatile Device device;
	
	private DisruptorEvent worker;

	public Device getDevice() {
		return device;
	}
	
	public void setDevice(Device device) {
		this.device = device;
	}
	public DisruptorEvent getDisruptorEvent() {
		return worker;
	}

}
