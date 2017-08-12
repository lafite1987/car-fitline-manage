package cn.lfy.base.web;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wordnik.swagger.annotations.ApiOperation;

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
import cn.lfy.common.model.ResultDTO;
import cn.lfy.common.page.Page;
import cn.lfy.common.utils.MessageDigestUtil;
import cn.lfy.common.utils.UUIDUtil;

@Controller
@RequestMapping("/manager/user")
public class UserController extends BaseController {

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private UserRoleService userRoleService;

	@RequestMapping(value = "/api/list")
	@ResponseBody
	@ApiOperation(value = "用户列表", httpMethod = "GET", notes = "用户列表接口")
	public ResultDTO<Page<User>> api_list(
			@RequestParam(name = "state", required = false) Integer state,
			@RequestParam(name = "currentPage", required = false, defaultValue = "0") Integer currentPage,
			@RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize)
			throws ApplicationException {

		ResultDTO<Page<User>> result = new ResultDTO<>();
		Criteria criteria = new Criteria();
		criteria.put("state", state);
		Page<User> page = userService.findListByCriteria(criteria, currentPage,
				pageSize);
		result.setData(page);
		return result;
	}

	/**
	 * 删除用户
	 * 
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/del")
	@ResponseBody
	@ApiOperation(value = "删除用户", httpMethod = "POST", notes = "删除指定用户")
	public ResultDTO<Boolean> del(
			@RequestParam(name = "id", required = false) Long id)
			throws ApplicationException {
		User record = new User();
		record.setId(id);
		record.setState(StateType.DELETED.getId());
		userService.updateByIdSelective(record);
		ResultDTO<Boolean> result = new ResultDTO<>();
		return result;
	}

	/**
	 * 详情
	 * 
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/detail")
	@ResponseBody
	@ApiOperation(value = "获取用户详情", httpMethod = "GET", notes = "获取用户详情")
	public ResultDTO<User> detail(
			@RequestParam(name = "id", required = true) Long id)
			throws ApplicationException {
		ResultDTO<User> result = new ResultDTO<>();
		User user = userService.findById(id);
		result.setData(user);
		return result;
	}

	/**
	 * 添加
	 * 
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/add")
	@ResponseBody
	@ApiOperation(value = "新增用户", httpMethod = "POST", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, notes = "新增用户")
	public ResultDTO<Void> add(User user) throws ApplicationException {
		ResultDTO<Void> result = new ResultDTO<>();
		String username = user.getUsername();
		User extuser = userService.findByUsername(username);
		if (extuser != null) {
			throw new ApplicationException(ErrorCode.EXIST, "用户名存在",
					new String[]{"用户名"});
		}
		String password = user.getPassword();
		try {
			password = MessageDigestUtil.getSHA256(password).toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		user.setPassword(password);
		user.setEmail("");
		user.setState(1);
		userService.add(user);
		return result;
	}

	/**
	 * 更新
	 * 
	 * @throws ApplicationException
	 */
	@RequestMapping(value = "/update")
	@ResponseBody
	@ApiOperation(value = "更新用户", httpMethod = "POST", notes = "更新用户")
	public ResultDTO<Void> update(User user) throws ApplicationException {
		ResultDTO<Void> result = new ResultDTO<>();
		if (StringUtils.isNotBlank(user.getPassword())) {// 判断password是否为空
			try {
				String salt = UUIDUtil.salt();
				String password = MessageDigestUtil
						.getSHA256(user.getPassword() + salt);
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
		return result;
	}

	@RequestMapping(value = "/role/tree")
	@ResponseBody
	@ApiOperation(value = "用户角色树", httpMethod = "POST", notes = "用户角色树")
	public ResultDTO<List<TreeNode>> api_tree(
			@RequestParam(name = "id", required = true) Long userId,
			LoginUser account) {
		List<Role> roleTree = new ArrayList<Role>();
		List<Role> list = userRoleService.getRoleListByUserId(userId);
		Set<Long> roleSet = Sets.newHashSet();
		for (Role role : list) {
			roleSet.add(role.getId());
		}
		Set<Role> roles = account.getRoles();
		roleTree.addAll(roles);
		while (!roles.isEmpty()) {
			Set<Role> next = new TreeSet<Role>();
			for (Role role : roles) {
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
		if (account.getRoleIds().contains(Long.valueOf(1L))
				&& roleSet.contains(Long.valueOf(1L))) {
			chkDisabled = true;
		} else if (roleSet.contains(Long.valueOf(1L))) {
			chkDisabled = true;
		}
		for (Role role : roleTree) {
			boolean checked = roleSet.contains(role.getId());
			TreeNode treeNode = new TreeNode(role.getId(), role.getName(),
					role.getParentId(), checked);
			treeNode.setChkDisabled(chkDisabled);
			treeList.add(treeNode);
		}
		List<TreeNode> tree = Lists.newArrayList();
		for (TreeNode node1 : treeList) {
			tree.add(node1);
		}
		ResultDTO<List<TreeNode>> result = new ResultDTO<>();
		result.setData(tree);
		return result;
	}

	@RequestMapping(value = "/role/save")
	@ResponseBody
	@ApiOperation(value = "保存用户角色", httpMethod = "POST", notes = "保存用户角色")
	public Object save(
			@RequestParam(name = "userId", required = true) Long userId,
			@RequestParam(name = "roleIds", required = true) String roleIdsString,
			LoginUser currentUser) throws ApplicationException {
		ResultDTO<Void> result = new ResultDTO<>();
		List<Long> roleIds = Lists.newArrayList();
		if (StringUtils.isNotBlank(roleIdsString)) {
			Iterator<String> it = Splitter.on(",").split(roleIdsString)
					.iterator();
			while (it.hasNext()) {
				roleIds.add(Long.parseLong(it.next()));
			}
		}
		if (null == userId || userId <= 0) {
			throw ApplicationException.newInstance(ErrorCode.PARAM_ILLEGAL,
					"userId");
		}

		User user = userService.findById(userId);
		if (null == user) {
			throw ApplicationException.newInstance(ErrorCode.NOT_EXIST, "用户");
		}
		userRoleService.saveRoles(user.getId(), roleIds,
				currentUser.getRoles());
		return result;
	}
}
