package com.fitline.server.network.message;

import com.alibaba.fastjson.JSON;

public abstract class AbstractPacket implements Packet {

	public String toJSON() {
		return JSON.toJSONString(this);
	}
}
