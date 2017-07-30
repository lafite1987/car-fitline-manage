package com.fitline.server.network.message;

import com.fitline.server.network.Context;

public class RequestWrapper implements Request {

	private Request request;
	
	private Context context;
	
	public RequestWrapper(Request request, Context context) {
		this.request = request;
		this.context = context;
	}

	@Override
	public int length() {
		return request.length();
	}

	@Override
	public int version() {
		return request.version();
	}

	@Override
	public int cmd() {
		return request.cmd();
	}

	public Context getContext() {
		return this.context;
	}
	@Override
	public <T> T readJSON(Class<T> cls) {
		return request.readJSON(cls);
	}
}
