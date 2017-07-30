package cn.lfy.base.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.lfy.base.model.Menu;
import cn.lfy.base.model.RoleMenu;

public interface RoleMenuDAO {
    /**
     * 根据角色查询默认的菜单列表
     * @param operatorId
     * @return
     */
    List<Menu> selectMenuListByRoleId(Long roleId);
    
    /**
     * 根据角色查询默认的菜单列表
     * @param operatorId
     * @return
     */
    List<Menu> selectMenuListByRoleIds(@Param("list")List<Long> list);
    
    /**
     * 添加角色默认菜单
     * @param record
     */
    void insert(RoleMenu record);
    
    /**
     * 删除角色的菜单 
     * @param operatorId
     */
    void deleteByRoleId(Long roleId);
    
    /**
     * 删除某菜单 
     * @param operatorId
     */
    void deleteByMenuId(Long menuId);
    
    void delete(@Param("roleId")Long roleId, @Param("menuId")Long menuId);
}