package com.fitline.server.model;


public class DeviceAuthReq {

	private String no;
	
	private String token;

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public String toString() {
		return "NodeAuthReq [no=" + no + ", token=" + token + "]";
	}
	
	
	
}
