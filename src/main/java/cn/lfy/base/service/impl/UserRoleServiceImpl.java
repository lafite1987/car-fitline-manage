package cn.lfy.base.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cn.lfy.base.dao.RoleDAO;
import cn.lfy.base.dao.UserRoleDAO;
import cn.lfy.base.model.Role;
import cn.lfy.base.model.UserRole;
import cn.lfy.base.service.UserRoleService;
import cn.lfy.common.framework.exception.ApplicationException;
import cn.lfy.common.framework.exception.ErrorCode;

@Service
public class UserRoleServiceImpl implements UserRoleService
{
	@Autowired
	private UserRoleDAO userRoleDAO;
	
	@Autowired
	private RoleDAO roleDAO;

    @Override
    public List<Role> getRoleListByUserId(Long adminId) {
        return userRoleDAO.getRoleListByUserId(adminId);
    }

    @Override
    public void add(Long adminId, Long menuId) {
        UserRole record=new UserRole();
        record.setUserId(adminId);
        record.setRoleId(menuId);
        userRoleDAO.insert(record);
    }

    @Override
    public void deleteByUserId(Long adminId) {
        userRoleDAO.deleteByUserId(adminId);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        userRoleDAO.deleteByRoleId(roleId);
    }

    public void saveRoles(Long userId, List<Long> roleIds, Set<Role> currentUserRoles) {
    	if(roleIds != null && roleIds.isEmpty()) {
    		roleIds = Lists.newArrayList(0L);
    	}
    	List<Role> roles = roleDAO.getRoles(roleIds);
    	Map<Long, Role> userRoleMap = Maps.newHashMap();
    	//有效的角色ID
    	Set<Long> nowRoleIds = Sets.newHashSet();
    	for(Role role : roles) {
    		nowRoleIds.add(role.getId());
    		userRoleMap.put(role.getId(), role);
    	}
    	List<Role> userRoles = getRoleListByUserId(userId);
    	Set<Long> userRolesSet = Sets.newHashSet();
    	
    	for(Role role : userRoles) {
    		userRolesSet.add(role.getId());
    		userRoleMap.put(role.getId(), role);
    	}
    	Set<Long> delSet = Sets.difference(userRolesSet, nowRoleIds);
		Set<Long> newSet = Sets.difference(nowRoleIds, userRolesSet);
		for(Long delRoleId : delSet) {
			Role role = userRoleMap.get(delRoleId);
			//判断操作者的用户的角色ID是否在该角色的父路径里
			if(!isCurrentUserRolesOfChild(role, currentUserRoles)) {
				throw ApplicationException.newInstance(ErrorCode.UNAUTHORIZED_OPERATE);
			}
		}
		for(Long roleId : newSet) {
			Role role = userRoleMap.get(roleId);
			if(!isCurrentUserRolesOfChild(role, currentUserRoles)) {
				throw ApplicationException.newInstance(ErrorCode.UNAUTHORIZED_OPERATE);
			}
		}
		for(Long delRoleId : delSet) {
			userRoleDAO.delete(userId, delRoleId);
		}
		for(Long roleId : newSet) {
			this.add(userId, roleId);
		}
    }
    
    /**
     * 判断role是否是currentUserRoles（当前用户角色集合）的子孙
     * @param role
     * @param currentUserRoles
     * @return
     */
    private boolean isCurrentUserRolesOfChild(Role role, Set<Role> currentUserRoles) {
    	for(Role crole : currentUserRoles) {
    		int idx = role.getParentIdPath().indexOf(crole.getParentIdPath() + crole.getId() + "$");
    		if(idx >= 0) {
    			return true;
    		}
    	}
    	return false;
    }

}
