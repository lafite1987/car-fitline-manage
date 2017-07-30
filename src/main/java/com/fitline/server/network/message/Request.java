package com.fitline.server.network.message;

import com.fitline.server.network.Context;

public interface Request {

	/**
	 * 包大小
	 * @return
	 */
	public int length();
	/**
	 * 版本号
	 * @return
	 */
	public int version();
	/**
	 * 请求的cmd
	 * @return
	 */
	public int cmd();
	/**
	 * 请求上下文
	 * @return
	 */
	public Context getContext();
	
	public <T> T readJSON(Class<T> cls);
	
	public static class RequestFactory {
		
		public static Request createRequest(int length, int version, int cmd, byte[] data) {
			return new AbstractRequest(data) {
				
				@Override
				public int version() {
					return version;
				}
				
				@Override
				public Context getContext() {
					return null;
				}
				
				@Override
				public int length() {
					return length;
				}
				
				@Override
				public int cmd() {
					return cmd;
				}
				
				@Override
				public String toString() {
					return "Request[length=" + length + ", version=" + version + ", cmd=" + cmd  + "]";
				}
				
			};
		}
	}
	
}
