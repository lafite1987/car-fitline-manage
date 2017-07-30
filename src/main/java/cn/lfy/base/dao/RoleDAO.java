package cn.lfy.base.dao;

import java.util.Collection;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.lfy.base.model.Criteria;
import cn.lfy.base.model.Role;

public interface RoleDAO {

    /**
     * 根据主键删除记录
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 保存记录,不管记录里面的属性是否为空
     */
    int insert(Role record);

    /**
     * 根据条件查询记录集
     */
    List<Role> selectByExample(@Param("condition")Criteria example);

    /**
     * 根据主键查询记录
     */
    Role selectByPrimaryKey(Long id);

    /**
     * 根据主键更新属性不为空的记录
     */
    int updateByPrimaryKeySelective(Role record);
    
    /**
     * 根據角色ID列表查詢角色列表
     * @param list
     */
    List<Role> getRoles(@Param("list")Collection<Long> list);
    
}