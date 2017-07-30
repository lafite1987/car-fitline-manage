package cn.lfy.base.service;

import java.util.List;
import java.util.Set;

import cn.lfy.base.model.LoginUser;
import cn.lfy.base.model.Menu;
import cn.lfy.base.model.RoleMenu;

public interface RoleMenuService {
    /**
     * 根据角色查询默认的菜单列表
     * @param operatorId
     * @return
     */
    List<Menu> getMenuListByRoleId(Long roleId);
    
    /**
     * 根据角色查询默认的菜单列表
     * @param operatorId
     * @return
     */
    List<Menu> selectMenuListByRoleIds(List<Long> list);
    
    /**
     * 添加角色默认菜单
     * @param record
     */
    void add(RoleMenu record);
    
    /**
     * 删除角色的菜单 
     * @param operatorId
     */
    void deleteByRoleId(Long  roleId);
    
    /**
     * 删除某菜单 
     * @param operatorId
     */
    void deleteByMenuId(Long  menuId);
    /**
     * 分配角色菜單
     * @param roleId
     * @param nowMenu
     * @param currentUser
     */
    void saveMenus(Long roleId, Set<Long> nowMenu, LoginUser currentUser);
}