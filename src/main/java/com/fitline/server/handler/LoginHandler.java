package com.fitline.server.handler;

import org.springframework.stereotype.Component;

import com.fitline.server.context.Cmd;
import com.fitline.server.context.CmdType;
import com.fitline.server.context.Handler;
import com.fitline.server.model.Device;
import com.fitline.server.model.DeviceAuthReq;
import com.fitline.server.model.DeviceAuthResp;
import com.fitline.server.network.Context;
import com.fitline.server.network.message.Request;
import com.fitline.server.network.message.Response;

@Component
@Cmd(value = CmdType.LOGIN)
public class LoginHandler implements Handler {

	@Override
	public void handle(Request req, Response resp) {
		DeviceAuthReq auth = req.readJSON(DeviceAuthReq.class);
		Context context = req.getContext();
		//Login Success Set Device to Context
		Device device = new Device(auth.getNo(), resp.channel());
		context.setDevice(device);
		DeviceAuthResp authResp = new DeviceAuthResp();
		authResp.setNo(device.getNo());
		authResp.setAuth("0");
		resp.write(authResp);
	}

}
