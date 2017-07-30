package com.fitline.server.model;

import com.fitline.server.network.message.AbstractPacket;

public class DeviceAuthResp extends AbstractPacket {

	private String no;
	
	private String auth;

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}
	
	
}
