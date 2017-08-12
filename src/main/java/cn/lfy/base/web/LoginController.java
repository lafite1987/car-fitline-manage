package cn.lfy.base.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiOperation;

import cn.lfy.base.Constants;
import cn.lfy.base.model.LoginUser;
import cn.lfy.base.model.Menu;
import cn.lfy.base.model.Role;
import cn.lfy.base.model.User;
import cn.lfy.base.model.type.StateType;
import cn.lfy.base.service.RoleMenuService;
import cn.lfy.base.service.UserRoleService;
import cn.lfy.base.service.UserService;
import cn.lfy.base.vo.UserMenuVO;
import cn.lfy.common.framework.exception.ApplicationException;
import cn.lfy.common.framework.exception.ErrorCode;
import cn.lfy.common.model.ResultDTO;
import cn.lfy.common.utils.MessageDigestUtil;
import cn.lfy.common.utils.Strings;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;
    
    @Autowired
	private RoleMenuService roleMenuService;
    
    @RequestMapping(value = "/manager/menu", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(value = "用户菜单", httpMethod = "GET", notes = "用户菜单")
    public ResultDTO<UserMenuVO> menu(LoginUser currentUser) throws ApplicationException {
    	ResultDTO<UserMenuVO> resultDTO = new ResultDTO<>();
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
        UserMenuVO userMenuVO = new UserMenuVO();
        userMenuVO.setMenus(menuList);
        userMenuVO.setUser(currentUser.getUser());
        resultDTO.setData(userMenuVO);
        return resultDTO;
    }
    
    @RequestMapping("/manager/login")
    @ResponseBody
    @ApiOperation(value = "登录接口", httpMethod = "GET", notes = "登录接口", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResultDTO<Void> doLogin(@RequestParam("username")String username, @RequestParam("password")String password) throws ApplicationException {
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
        HttpServletRequest request = getRequest();
        request.getSession().setAttribute(Constants.SESSION_LOGIN_USER, account);
        ResultDTO<Void> resultDTO = new ResultDTO<>();
        return resultDTO;
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

    @RequestMapping("/manager/logout")
    @ResponseBody
    @ApiOperation(value = "登出接口", httpMethod = "GET", notes = "登出接口")
    public ResultDTO<Void> logout() {
    	HttpServletRequest request = getRequest();
    	ResultDTO<Void> resultDTO = new ResultDTO<>();
        HttpSession session = request.getSession();
        session.removeAttribute(Constants.SESSION_LOGIN_USER);
        session.invalidate();
        resultDTO.setRedirect("/console/login.html");
        return resultDTO;
    }
    

}
