package cn.lfy.base.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.lfy.base.Constants;
import cn.lfy.base.model.LoginUser;
import cn.lfy.base.model.Menu;
import cn.lfy.base.model.Role;
import cn.lfy.base.model.User;
import cn.lfy.base.model.type.StateType;
import cn.lfy.base.service.RoleMenuService;
import cn.lfy.base.service.UserRoleService;
import cn.lfy.base.service.UserService;
import cn.lfy.common.framework.exception.ApplicationException;
import cn.lfy.common.framework.exception.ErrorCode;
import cn.lfy.common.model.Message;
import cn.lfy.common.utils.MessageDigestUtil;
import cn.lfy.common.utils.Strings;

@Controller
public class LoginController {

    private static final String ADMIN_LOGIN = "/system/login";

    private static final String INDEX = "/system/common/index";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;
    
    @Autowired
	private RoleMenuService roleMenuService;
    
    @RequestMapping("/manage/index")
    public String index(HttpServletRequest request, LoginUser currentUser) throws ApplicationException {
    	List<Menu> menus = roleMenuService.selectMenuListByRoleIds(Lists.newArrayList(currentUser.getRoleIds()));
        List<Menu> menuList = Lists.newArrayList();
        Set<String> uriSet = Sets.newTreeSet();
        for(Menu menu : menus) {
        	uriSet.add(menu.getUrl());
        	if(menu.getParentId() == -1) {
        		continue;
        	}
        	else if(menu.getParentId() == 1) {
        		menuList.add(menu);
        	} else if(menu.getOnMenu() == 1) {
        		Menu parent = null;
        		for(Menu m : menuList) {
        			if(m.getId() == menu.getParentId()) {
        				parent = m;
        				break;
        			}
        		}
        		if(parent != null && parent.getChildList() == null) {
        			 List<Menu> childList = new ArrayList<Menu>();
        			 parent.setChildList(childList);
        		}
        		if(parent != null) {
        			parent.getChildList().add(menu);
        		}
        	}
        }
        currentUser.setUriSet(uriSet);
        request.setAttribute("menuList", menuList);
        request.setAttribute("realName", currentUser.getUser().getNickname());
        return INDEX;
    }
    
    @RequestMapping("/manage/home")
    public String home() throws ApplicationException {
        return "/system/common/home";
    }
    
    @RequestMapping("/")
    public String manager() throws ApplicationException {
        return ADMIN_LOGIN;
    }
    
    @RequestMapping("/login")
    public String login() throws ApplicationException {
        return ADMIN_LOGIN;
    }

    @RequestMapping("/dologin")
    @ResponseBody
    public Object login(HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
        try {
            doLogin(request, response);
        } catch (ApplicationException ex) {
            throw ex;
        }
        return Message.newBuilder().build();
    }

    private void doLogin(HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (null == username || username.trim().length() == 0
                || null == password || password.length() == 0) {
            throw new ApplicationException(ErrorCode.PARAM_ILLEGAL, "", new String[]{"用户名或密码"});
        }
        
        LoginUser account = getLoginUser(username);
        User user = account.getUser();
        if (user == null) {
            throw ApplicationException.newInstance(ErrorCode.ERROR);
        }
        if (user.getState().equals(StateType.INACTIVE)) {
        	throw ApplicationException.newInstance(ErrorCode.ERROR);
        }
        
        password = MessageDigestUtil.getSHA256(password + user.getSalt());
        
        if (!Strings.slowEquals(password, user.getPassword())) {
        	throw ApplicationException.newInstance(ErrorCode.ERROR, "用户名或密码错误");
        }
        account.setId(user.getId());
        request.getSession().setAttribute(Constants.SESSION_LOGIN_USER, account);
    }
    
    private LoginUser getLoginUser(String username){
        User dbUser = userService.findByUsername(username);
        if(dbUser == null) {
        	throw ApplicationException.newInstance(ErrorCode.ERROR, "用户名或密码错误");
        }
        List<Role> roleList = userRoleService.getRoleListByUserId(dbUser.getId());
        LoginUser account = new LoginUser();
        Set<Role> roleSet = new TreeSet<Role>();
        roleSet.addAll(roleList);
        account.setRoles(roleSet);
        account.setUser(dbUser);

        return account;
    }

    @RequestMapping("/manage/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.removeAttribute(Constants.SESSION_LOGIN_USER);
        session.invalidate();
        return "redirect:/login";
    }
    

}
