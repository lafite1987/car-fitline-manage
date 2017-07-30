package com.fitline.server;

import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.fitline.server.context.Cmd;
import com.fitline.server.context.Handler;
import com.google.common.collect.Maps;

@Component
public class Processor implements ApplicationContextAware {

	private static Map<Integer, Map<Integer, HandlerInfo>> MAP_CMD = Maps.newConcurrentMap();
	
	private static ApplicationContext applicationContext;
	
	public static class HandlerInfo {
		
		private Cmd cmd;
		
		private Handler handler;
		
		public HandlerInfo(Cmd cmd, Handler handler) {
			this.cmd = cmd;
			this.handler = handler;
		}
		
		public Cmd getCmd() {
			return cmd;
		}

		public Handler getHandler() {
			return handler;
		}
		
	}
	
	/**
	 * 获取指定指令版本号的处理器
	 * @param cmd
	 * @param version
	 * @return
	 */
	public static HandlerInfo getHandler(int cmd, int version) {
		Map<Integer, HandlerInfo> versionTable = MAP_CMD.get(cmd);
		if(versionTable != null) {
			return versionTable.get(version);
		}
		return null;
	}
	
	@PostConstruct
	protected void load() {
		Map<String, Handler> handlerMap = applicationContext.getBeansOfType(Handler.class);
		for(Entry<String, Handler> entry : handlerMap.entrySet()) {
			Handler handler = entry.getValue();
			Cmd cmd = handler.getClass().getAnnotation(Cmd.class);
			if(cmd == null) continue;
			register(new HandlerInfo(cmd, handler));
		}
	}
	
	private void register(HandlerInfo handler) {
		Map<Integer, HandlerInfo> version = MAP_CMD.get(handler.cmd.value());
		if(version == null) {
			version = Maps.newConcurrentMap();
			MAP_CMD.put(handler.cmd.value(), version);
		}
		version.put(handler.cmd.version(), handler);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		Processor.applicationContext = applicationContext;
	}
	
}
