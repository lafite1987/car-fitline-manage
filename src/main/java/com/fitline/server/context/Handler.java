package com.fitline.server.context;

import com.fitline.server.network.message.Request;
import com.fitline.server.network.message.Response;

public interface Handler {

	void handle(Request req, Response resp);
}
