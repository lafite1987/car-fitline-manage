package com.fitline.server.handler;

import org.springframework.stereotype.Component;

import com.fitline.server.context.Cmd;
import com.fitline.server.context.CmdType;
import com.fitline.server.context.Handler;
import com.fitline.server.network.message.Request;
import com.fitline.server.network.message.Response;

@Component
@Cmd(value = CmdType.PING)
public class PingHandler implements Handler {

	@Override
	public void handle(Request req, Response resp) {
		resp.write(null);
	}

}
