package cn.lfy.base.service;

import java.util.List;
import java.util.Set;

import cn.lfy.base.model.Role;

public interface UserRoleService
{
    /**
     * 根据内勤人员id获取菜单列表
     * @param userId
     * @return
     */
    List<Role> getRoleListByUserId(Long userId);
    
    /**
     * 添加内勤人员菜单
     * @param record
     */
    void add(Long userId, Long menuId);
    
    /**
     * 删除内勤人员ID的菜单 
     * @param userId
     */
    void deleteByUserId(Long  userId);
    
    /**
     * 删除某菜单 
     * @param role
     */
    void deleteByRoleId(Long  roleId);
    /**
     * 保存用戶角色列表
     * @param userId
     * @param roleIds
     */
    void saveRoles(Long userId, List<Long> roleIds, Set<Role> currentUserRoles);
}
