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
import com.wordnik.swagger.annotations.ApiOperation;

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
import cn.lfy.common.model.ResultDTO;
import cn.lfy.common.utils.RequestUtil;
import cn.lfy.common.validator.Validators;

@Controller
@RequestMapping("/manager/role")
public class RoleController {

    public static final int listPageSize = 20;

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private RoleMenuService roleMenuService;

    @RequestMapping("/api/tree")
    @ResponseBody
    @ApiOperation(value = "角色树", httpMethod = "GET", notes = "角色树")
    public ResultDTO<List<TreeNode>> api_tree(LoginUser account) {
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
        ResultDTO<List<TreeNode>> resultDTO = new ResultDTO<>();
        resultDTO.setData(tree);
    	return resultDTO;
    }

    @RequestMapping("/del")
    @ResponseBody
    @ApiOperation(value = "删除角色", httpMethod = "POST", notes = "删除角色")
    public ResultDTO<Void> del(Long id) throws ApplicationException {
    	ResultDTO<Void> resultDTO = new ResultDTO<>();
        Role record = new Role();
        record.setId(id);
        record.setState(StateType.INACTIVE.getId());
        roleService.updateByIdSelective(record);
        return resultDTO;
    }

    @RequestMapping("/detail")
    @ResponseBody
    @ApiOperation(value = "角色信息", httpMethod = "GET", notes = "角色信息")
    public ResultDTO<Role> detail(HttpServletRequest request) throws ApplicationException {
    	ResultDTO<Role> resultDTO = new ResultDTO<>();
        Long id = RequestUtil.getLong(request, "id");
        Role role = roleService.getById(id);
        resultDTO.setData(role);
        return resultDTO;
    }

    @RequestMapping("/add")
    @ResponseBody
    @ApiOperation(value = "添加角色", httpMethod = "POST", notes = "添加角色")
    public ResultDTO<Void> add(Role role) throws ApplicationException {
    	ResultDTO<Void> resultDTO = new ResultDTO<>();
        Role parent = roleService.getById(role.getParentId());
        Validators.notNull(parent, ErrorCode.PARAM_ILLEGAL, "parentId");
        role.setLevel(parent.getLevel() + 1);
    	roleService.insert(role);
        return resultDTO;
    }

    @RequestMapping("/update")
    @ResponseBody
    @ApiOperation(value = "更新角色", httpMethod = "POST", notes = "更新角色")
    public ResultDTO<Void> update(Role role, HttpServletRequest request, HttpServletResponse response) throws ApplicationException {
    	ResultDTO<Void> resultDTO = new ResultDTO<>();
    	roleService.updateByIdSelective(role);
        return resultDTO;
    }
    
    /**
	 * 角色权限列表：角色拥有的权限默认选上
	 * @date 2015-10-09
	 * @param request
	 * @return
	 */
	@RequestMapping("/privileges")
	@ResponseBody
	@ApiOperation(value = "角色权限树", httpMethod = "GET", notes = "角色权限树")
	public ResultDTO<List<TreeNode>> privileges(Long id, LoginUser account) {
		Role role = roleService.getById(id);
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
		
		List<Menu> roleMenus = roleMenuService.getMenuListByRoleId(id);
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
		ResultDTO<List<TreeNode>> resultDTO = new ResultDTO<>();
		resultDTO.setData(treeList);
		return resultDTO;
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
	@ApiOperation(value = "角色权限保存", httpMethod = "POST", notes = "角色权限保存")
	public ResultDTO<Void> saveMenu(Long roleId, String menuIds, LoginUser currentUser) throws ApplicationException
	{
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
		ResultDTO<Void> resultDTO = new ResultDTO<>();
		return resultDTO;
	}

}
