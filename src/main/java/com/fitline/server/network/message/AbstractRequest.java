package com.fitline.server.network.message;

import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSON;

public abstract class AbstractRequest implements Request {

	private byte[] data = new byte[0];
	
	protected AbstractRequest(byte[] data) {
		this.data = data;
	}
	
	public <T> T readJSON(Class<T> cls) {
		try {
			String str = new String(data, "UTF-8");
			return JSON.parseObject(str, cls);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
