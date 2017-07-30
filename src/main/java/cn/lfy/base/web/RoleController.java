package cn.lfy.base.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.lfy.base.model.Criteria;
import cn.lfy.base.model.LoginUser;
import cn.lfy.base.model.Menu;
import cn.lfy.base.model.Role;
import cn.lfy.base.model.TreeNode;
import cn.lfy.base.model.type.StateType;
import cn.lfy.base.service.MenuService;
import cn.lfy.base.service.RoleMenuService;
import cn.lfy.base.service.RoleService;
import cn.lfy.common.framework.exception.ApplicationException;
import cn.lfy.common.framework.exception.ErrorCode;
import cn.lfy.common.model.Message;
import cn.lfy.common.utils.RequestUtil;
import cn.lfy.common.validator.Validators;

@Controller
@RequestMapping("/manage/role")
public class RoleController {

    public static final int listPageSize = 20;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private RoleMenuService roleMenuService;

    @RequestMapping("/list")
    public String list(HttpServletRequest request) throws ApplicationException {
        return "/system/role/role-tree";
    }
    
    @RequestMapping("/api/tree")
    @ResponseBody
    public Object api_tree(HttpServletRequest request, LoginUser account) {
    	List<Role> roleTree = new ArrayList<Role>();
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
		for(Role role : roleTree) {
			treeList.add(new TreeNode(role.getId(), role.getName(), role.getParentId(), false));
		}
		List<TreeNode> tree = Lists.newArrayList();
		for(TreeNode node1 : treeList){  
			tree.add(node1);   
		}
        Message.Builder builder = Message.newBuilder();
        builder.data(tree);
    	return builder.build();
    }

    @RequestMapping("/del")
    @ResponseBody
    public Object del(HttpServletRequest request) throws ApplicationException {
        Long id = RequestUtil.getLong(request, "id");
        Role record = new Role();
        record.setId(id);
        record.setState(StateType.INACTIVE.getId());
        roleService.updateByIdSelective(record);
        return Message.newBuilder().build();
    }

    @RequestMapping("/detail")
    @ResponseBody
    public Object detail(HttpServletRequest request) throws ApplicationException {
    	Message.Builder builder = Message.newBuilder();
        Long id = RequestUtil.getLong(request, "id");
        Role role = roleService.getById(id);
        builder.data(role);
        return builder.build();
    }

    @RequestMapping("/add")
    @ResponseBody
    public Object add(Role role, HttpServletRequest request) throws ApplicationException {
        Role parent = roleService.getById(role.getParentId());
        Validators.notNull(parent, ErrorCode.PARAM_ILLEGAL, "parentId");
        role.setLevel(parent.getLevel() + 1);
    	roleService.insert(role);
        return Message.newBuilder().build();
    }

    @RequestMapping("/update")
    @ResponseBody
    public Object update(Role role, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
        roleService.updateByIdSelective(role);
        return Message.newBuilder().build();
    }
    
    /**
	 * 角色权限列表：角色拥有的权限默认选上
	 * @date 2015-10-09
	 * @param request
	 * @return
	 */
	@RequestMapping("/privileges")
	@ResponseBody
	public Object privileges(HttpServletRequest request, LoginUser account) {
		Long roleId = RequestUtil.getLong(request, "id");
		Role role = roleService.getById(roleId);
		List<Menu> menus = null;
		if(account.getRoleIds().contains(1L)) {
			if(role.getId() == 1) {
				menus = menuService.findMenuList();
			} else if(role.getParentId() == 1) {
				menus = menuService.findMenuList();
			} else {
				menus = roleMenuService.getMenuListByRoleId(role.getParentId());
			}
			
		} else {
			if(account.getRoleIds().contains(role.getId())) {
				menus = roleMenuService.getMenuListByRoleId(role.getId());
			} else {
				menus = roleMenuService.getMenuListByRoleId(role.getParentId());
			}
		}
		
		List<Menu> roleMenus = roleMenuService.getMenuListByRoleId(roleId);
		HashSet<Long> roleMenuIdSet = Sets.newHashSet();
		for(Menu m : roleMenus) {
			roleMenuIdSet.add(m.getId());
		}
		List<TreeNode> treeList = Lists.newArrayList();
		for(Menu menu : menus) {
			boolean checked = false;
			boolean chkDisabled = false;
			if(account.getRoleIds().contains(1L) && role.getId() == 1) {
				checked = true;
				chkDisabled = true;
			} else {
				checked = roleMenuIdSet.contains(menu.getId());
			}
			TreeNode treeNode = new TreeNode(menu.getId(), menu.getName(), menu.getParentId(), checked);
			treeNode.setChkDisabled(chkDisabled);
			treeList.add(treeNode);
		}
		Message.Builder builder = Message.newBuilder();
		builder.data(treeList);
		return builder.build();
	}
	/**
	 * 保存角色对应权限：做差集
	 * 删除权限：（1，2，3，4）-（1，2，5）=3，4
	 * 新增权限：（1，2，5）-（1，2，3，4）=5
	 * @param request
	 * @return
	 * @throws AdminException
	 */
	@RequestMapping("/privileges/save")
	@ResponseBody
	public Object saveMenu(HttpServletRequest request, LoginUser currentUser) throws ApplicationException
	{
		Long roleId = RequestUtil.getLong(request, "roleId");
		String menuIds = RequestUtil.getString(request, "menuIds");
		if (null == roleId || roleId <= 0)
		{
			throw ApplicationException.newInstance(ErrorCode.PARAM_ILLEGAL, "roleId");
		}
		if (null == menuIds || "".equals(menuIds))
        {
			throw ApplicationException.newInstance(ErrorCode.PARAM_ILLEGAL, "menuIds");
        }
		Role role = roleService.getById(roleId);
		if (null == role)
		{
			throw ApplicationException.newInstance(ErrorCode.NOT_EXIST, "角色");
		}
		Iterator<String> it = Splitter.on(",").trimResults().split(menuIds).iterator();
		Set<Long> nowSet = Sets.newHashSet();
		while(it.hasNext()) {
			nowSet.add(Long.valueOf(it.next()));
		}
		roleMenuService.saveMenus(roleId, nowSet, currentUser);
		return Message.newBuilder().build();
	}

}
