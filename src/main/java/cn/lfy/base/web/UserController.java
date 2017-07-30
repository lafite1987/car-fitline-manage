package cn.lfy.base.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.lfy.base.model.Criteria;
import cn.lfy.base.model.LoginUser;
import cn.lfy.base.model.Role;
import cn.lfy.base.model.TreeNode;
import cn.lfy.base.model.User;
import cn.lfy.base.model.type.StateType;
import cn.lfy.base.service.RoleService;
import cn.lfy.base.service.UserRoleService;
import cn.lfy.base.service.UserService;
import cn.lfy.common.framework.exception.ApplicationException;
import cn.lfy.common.framework.exception.ErrorCode;
import cn.lfy.common.model.Message;
import cn.lfy.common.page.Page;
import cn.lfy.common.utils.MessageDigestUtil;
import cn.lfy.common.utils.RequestUtil;
import cn.lfy.common.utils.UUIDUtil;

@Controller
@RequestMapping("/manage/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private UserRoleService userRoleService;
    
    /**
     * 用户详情列表
     * 
     * @param request
     * @return ModelAndView
     * @throws ApplicationException
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list(HttpServletRequest request) throws ApplicationException {
        return new ModelAndView("/system/user/user-list");
    }
    
    @RequestMapping(value = "/api/list")
    @ResponseBody
    public Object api_list(HttpServletRequest request) throws ApplicationException {
        Integer pageNum = RequestUtil.getInteger(request, "currentPage");
        Integer pageSize = RequestUtil.getInteger(request, "pageSize");
        Criteria criteria = new Criteria();
        Page<User> result = userService.findListByCriteria(criteria, pageNum, pageSize);
        JSONObject json = new JSONObject();
        json.put("code", 200);
        json.put("total", result.getTotalResult());
        json.put("value", result.getList());
        return json;
    }
    
    /**
     * 删除用户
     * 
     * @param request
     * @return ModelAndView
     * @throws ApplicationException
     */
    @RequestMapping("/del")
    @ResponseBody
    public Object del(HttpServletRequest request) throws ApplicationException {
        Long id = RequestUtil.getLong(request, "id");
        User record = new User();
        record.setId(id);
        record.setState(StateType.DELETED.getId());
        userService.updateByIdSelective(record);
        Message.Builder builder = Message.newBuilder();
        return builder.build();
    }
    
    /**
     * 详情
     * 
     * @param request
     * @return
     * @throws ApplicationException
     */
    @RequestMapping("/detail")
    @ResponseBody
    public Object detail(HttpServletRequest request) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        Long id = RequestUtil.getLong(request, "id");
        User user = userService.findById(id);
        builder.data(user);
        List<Role> roles = roleService.getByCriteria(new Criteria());
        request.setAttribute("roles", roles);
        request.setAttribute("uri", "update");
        return builder.build();
    }

    /**
     * 添加
     * 
     * @param request
     * @throws ApplicationException
     */
    @RequestMapping("/add")
    @ResponseBody
    public Object add(User user, HttpServletRequest request) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        String username = RequestUtil.getString(request, "username");
        User extuser = userService.findByUsername(username);
        if (extuser != null) {
            throw new ApplicationException(ErrorCode.EXIST, "用户名存在", new String[]{"用户名"});
        }
        String password = RequestUtil.getString(request, "passward");
        try {
            password = MessageDigestUtil.getSHA256(password).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setPassword(password);
        user.setEmail("");
        user.setState(1);
        userService.add(user);
        return builder.build();
    }

    /**
     * 更新
     * 
     * @param request
     * @return ModelAndView
     * @throws ApplicationException
     */
    @RequestMapping("/update")
    @ResponseBody
    public Object update(User user, HttpServletRequest request,
            HttpServletResponse response) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        if (StringUtils.isNotBlank(user.getPassword())) {// 判断password是否为空
            try {
            	String salt = UUIDUtil.salt();
                String password = MessageDigestUtil.getSHA256(user.getPassword() + salt);
                user.setPassword(password);
                user.setSalt(salt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
        	user.setPassword(null);
        	user.setSalt(null);
        }
        userService.updateByIdSelective(user);
        return builder.build();
    }
    
    @RequestMapping("/role/tree")
    @ResponseBody
    public Object api_tree(HttpServletRequest request, LoginUser account) {
    	List<Role> roleTree = new ArrayList<Role>();
    	Long userId = RequestUtil.getLong(request, "id");
    	List<Role> list = userRoleService.getRoleListByUserId(userId);
    	Set<Long> roleSet = Sets.newHashSet();
    	for(Role role : list) {
    		roleSet.add(role.getId());
    	}
    	Set<Role> roles = account.getRoles();
    	roleTree.addAll(roles);
    	while(!roles.isEmpty()) {
    		Set<Role> next = new TreeSet<Role>();
    		for(Role role : roles) {
    			Criteria criteria = new Criteria();
    			criteria.put("parentId", role.getId());
    			List<Role> tmpRoles = roleService.getByCriteria(criteria);
    			roleTree.addAll(tmpRoles);
    			next.addAll(tmpRoles);
    		}
    		roles = next;
    	}
		List<TreeNode> treeList = Lists.newArrayList();
		boolean chkDisabled = false;
		if(account.getRoleIds().contains(Long.valueOf(1L)) && roleSet.contains(Long.valueOf(1L))) {
			chkDisabled = true;
		} else if(roleSet.contains(Long.valueOf(1L))) {
			chkDisabled = true;
		}
		for(Role role : roleTree) {
			boolean checked = roleSet.contains(role.getId());
			TreeNode treeNode = new TreeNode(role.getId(), role.getName(), role.getParentId(), checked);
			treeNode.setChkDisabled(chkDisabled);
			treeList.add(treeNode);
		}
		List<TreeNode> tree = Lists.newArrayList();
		for(TreeNode node1 : treeList){  
			tree.add(node1);   
		}
        Message.Builder builder = Message.newBuilder();
        builder.data(tree);
    	return builder.build();
    }
    
	@RequestMapping("/role/save")
	@ResponseBody
	public Object save(HttpServletRequest request, LoginUser currentUser) throws ApplicationException
	{
		Long userId = RequestUtil.getLong(request, "userId");
		String roleIdsString = RequestUtil.getString(request, "roleIds");
		List<Long> roleIds = Lists.newArrayList();
		if(StringUtils.isNotBlank(roleIdsString)) {
			Iterator<String> it = Splitter.on(",").split(roleIdsString).iterator();
			while(it.hasNext()) {
				roleIds.add(Long.parseLong(it.next()));
			}
		}
		if (null == userId || userId <= 0)
		{
			throw ApplicationException.newInstance(ErrorCode.PARAM_ILLEGAL, "userId");
		}
		
		User user = userService.findById(userId);
		if (null == user)
		{
			throw ApplicationException.newInstance(ErrorCode.NOT_EXIST, "用户");
		}
		userRoleService.saveRoles(user.getId(), roleIds, currentUser.getRoles());
		return Message.newBuilder().build();
	}
}
