package cn.lfy.base.service;

import java.util.List;

import cn.lfy.base.model.Criteria;
import cn.lfy.base.model.Role;

public interface RoleService {

    /**
     * 保存记录,不管记录里面的属性是否为空
     */
    int insert(Role record);

    /**
     * 根据条件查询记录集
     */
    List<Role> getByCriteria(Criteria criteria);
    
    /**
     * 根据主键查询记录
     */
    Role getById(Long id);
    
    /**
     * 根据主键查询记录
     */
    Role getByIdInCache(Long id);

    /**
     * 根据主键更新属性不为空的记录
     */
    int updateByIdSelective(Role record);

}