package com.fitline.client;

import java.util.Map;

import com.google.common.collect.Maps;

public class ClientManager {

	private static final Map<String, NioClient2> map = Maps.newConcurrentMap();
	
	public static void add(String no, NioClient2 client) {
		map.put(no, client);
	}
	
	public static NioClient2 getClient(String no) {
		return map.get(no);
	}
	
	public static void remove(String no) {
		map.remove(no);
	}
}
