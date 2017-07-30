package cn.lfy.base.web.filter;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.lfy.base.Constants;
import cn.lfy.base.model.LoginUser;
import cn.lfy.common.framework.exception.ApplicationException;
import cn.lfy.common.framework.exception.ErrorCode;

public class PermissionInterceptor extends HandlerInterceptorAdapter {

	private Logger log = LoggerFactory.getLogger(PermissionInterceptor.class);

	/** 忽略拦截请求的Url **/
	public static Set<String> ignoreUrl = new HashSet<String>();

	static {

		/*
		 * 放置忽略拦截请求的action名称
		 */
		ignoreUrl.add("/manage/login"); /* 登录验证 */
		ignoreUrl.add("/manage/dologin"); /* 登录验证 */
		ignoreUrl.add("/manage/code"); /* 登录验证 */
		ignoreUrl.add("/manage/index"); /* 登录验证 */
		ignoreUrl.add("/manage/login_default.do");
		ignoreUrl.add("/manage/admin/personal");
		ignoreUrl.add("/manage/admin/update2");
		ignoreUrl.add("/manage/logout");

	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		String requestUrl = request.getRequestURI();

		request.setAttribute("reqMill", System.currentTimeMillis());
		if (!(ignoreUrl.contains(requestUrl))) {

			if (!permissionVerify(request)) {
				throw ApplicationException.newInstance(ErrorCode.PERMISSION_DENIED);
			}
		}
		return super.preHandle(request, response, handler);
	}

	private boolean permissionVerify(HttpServletRequest request) {

		HttpSession session = request.getSession();
		LoginUser user = (LoginUser) session.getAttribute(Constants.SESSION_LOGIN_USER);

		String uri = request.getRequestURI();
		Set<String> uriSet = user.getUriSet();
		if ("/manage/index".equals(uri)) {
			return true;
		}
		if(uriSet.contains(uri)) {
			return true;
		}
		
		return false;
	}
	
	public void afterCompletion(
			HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		String requestUrl = request.getRequestURI();
		long respMill = System.currentTimeMillis();
		long reqMill = (Long)request.getAttribute("reqMill");
		log.info("权限拦截，请求url：" + requestUrl + "，cost " + (respMill - reqMill));
	}

}
