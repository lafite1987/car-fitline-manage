package cn.lfy.base.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import cn.lfy.base.model.LoginUser;

public class LoginFilter extends OncePerRequestFilter {

	private Logger log = LoggerFactory.getLogger(LoginFilter.class);
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		long start = System.currentTimeMillis();
		String requestUri = request.getRequestURI();
		
		if(!LoginVerify(request)) {
			String httpAjax=request.getHeader("X-Requested-With");
			if(httpAjax != null && "XMLHttpRequest".equals(httpAjax)) {
				String scheme = request.getScheme();
				String host = request.getServerName();
				int port =request.getServerPort();
				String loginUrl = scheme + "://" + host + ":" + port + "/login";
				response.setStatus(302);
				response.getWriter().write("{\"ret\":\"3000\", \"msg\":\"验证会话失败，请先登录\", \"redirectUrl\":\"" + loginUrl + "\"}");
				response.getWriter().flush();
				response.getWriter().close();
			} else {
				response.sendRedirect("/login");
			}
			return;
		}
		filterChain.doFilter(request, response);
		log.info("登录会话拦截，请求URL："+requestUri + " cost " + (System.currentTimeMillis() - start) + "ms");
	}

	
	private boolean LoginVerify(HttpServletRequest request){
		
		HttpSession session = request.getSession();
		LoginUser user = (LoginUser) session.getAttribute("SESSION_USER");
		if(null == user || user.getId() <= 0){
			return false;
		}
		return true;
	}
	
	
	
}
