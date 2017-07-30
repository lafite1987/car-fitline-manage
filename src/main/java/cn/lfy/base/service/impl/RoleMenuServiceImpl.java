package cn.lfy.base.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import cn.lfy.base.dao.RoleMenuDAO;
import cn.lfy.base.model.LoginUser;
import cn.lfy.base.model.Menu;
import cn.lfy.base.model.Role;
import cn.lfy.base.model.RoleMenu;
import cn.lfy.base.service.RoleMenuService;
import cn.lfy.base.service.RoleService;
import cn.lfy.common.framework.exception.ErrorCode;
import cn.lfy.common.validator.Validators;

@Service
public class RoleMenuServiceImpl implements RoleMenuService {

    @Autowired
    private RoleMenuDAO roleMenuDAO;
    @Autowired
    private RoleService roleService;
    
    @Override
    public List<Menu> getMenuListByRoleId(Long roleId) {
        return roleMenuDAO.selectMenuListByRoleId(roleId);
    }

    @Override
    public void add(RoleMenu record) {
        roleMenuDAO.insert(record);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        roleMenuDAO.deleteByRoleId(roleId);
    }

    @Override
    public void deleteByMenuId(Long menuId) {
        roleMenuDAO.deleteByMenuId(menuId);
    }

    public void saveMenus(Long roleId, Set<Long> nowMenu, LoginUser currentUser) {
    	Validators.isFalse(roleId == 1, ErrorCode.UNAUTHORIZED_OPERATE);
    	Role role = roleService.getById(roleId);
    	List<Menu> parentRoleMenus = getMenuListByRoleId(role.getParentId());
    	List<Menu> roleMenus = getMenuListByRoleId(roleId);
		Set<Long> roleSet = Sets.newHashSet();
		for(Menu m : roleMenus) {
			roleSet.add(m.getId());
		}
		Set<Long> delSet = Sets.difference(roleSet, nowMenu);
		Set<Long> newSet = Sets.difference(nowMenu, roleSet);
		List<Menu> currentUserMenus = selectMenuListByRoleIds(Lists.newArrayList(currentUser.getRoleIds()));
		Set<Long> currentUserMenuSet = Sets.newHashSet();
		Set<Long> parentRoleMenuSet = Sets.newHashSet();
		for(Menu menu : parentRoleMenus) {
			parentRoleMenuSet.add(menu.getId());
		}
		Validators.isFalse(!parentRoleMenuSet.containsAll(newSet), ErrorCode.UNAUTHORIZED_OPERATE);
		for(Menu menu : currentUserMenus) {
			currentUserMenuSet.add(menu.getId());
		}
		Validators.isFalse(!currentUserMenuSet.containsAll(newSet), ErrorCode.UNAUTHORIZED_OPERATE);
		for(Long menuId : delSet) {
			roleMenuDAO.delete(roleId, menuId);
		}
		
		for(Long menuId : newSet) {
			RoleMenu record = new RoleMenu();
			record.setRoleId(roleId);
			record.setMenuId(menuId);
			roleMenuDAO.insert(record);
		}
    }

	@Override
	public List<Menu> selectMenuListByRoleIds(List<Long> list) {
		if(list == null || list.isEmpty()) {
			List<Long> tmp = Lists.newArrayList(0L);
			return roleMenuDAO.selectMenuListByRoleIds(tmp);
		}
		return roleMenuDAO.selectMenuListByRoleIds(list);
	}

}
