package cn.lfy.base.web.core;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebArgumentResolver;
import org.springframework.web.context.request.NativeWebRequest;

import cn.lfy.base.Constants;
import cn.lfy.base.model.LoginUser;

public class LoginUserArgumentResolver implements WebArgumentResolver {

	@Override
	public Object resolveArgument(MethodParameter methodParameter, NativeWebRequest webRequest) throws Exception {
		 if(methodParameter.getParameterType() != null
	                && methodParameter.getParameterType().equals(LoginUser.class)){
			 	// 判断controller方法参数有没有写当前用户，如果有，这里返回即可，通常我们从session里面取出来
            HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
            Object currentUser = request.getSession().getAttribute(Constants.SESSION_LOGIN_USER);
            return currentUser;
	    }
	    return UNRESOLVED;
	}
}
