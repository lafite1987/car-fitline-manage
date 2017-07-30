package cn.lfy.base.web;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lfy.common.framework.exception.ApplicationException;
import cn.lfy.common.model.Message;

@Controller
public class TestCtrl {

	@RequestMapping(value = "/test")
    @ResponseBody
    public Object test(HttpServletRequest request, String name, HttpServletResponse response) throws ApplicationException {
		Message.Builder builder = Message.newBuilder();
		builder.put("name", name);
		String start = request.getHeader("x-start-time");
		response.addHeader("x-start-time", start);
		try {
			response.flushBuffer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return builder.build();
	}
}
