package cn.lfy.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cn.lfy.base.dao.RoleDAO;
import cn.lfy.base.model.Criteria;
import cn.lfy.base.model.Role;
import cn.lfy.base.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDAO roleDAO;
    
    @Override
    public int insert(Role record) {
    	Role parent = getByIdInCache(record.getParentId());
    	record.setParentIdPath(parent.getParentIdPath() + record.getParentId() + "$");
        return roleDAO.insert(record);
    }

    @Override
    public List<Role> getByCriteria(Criteria criteria) {
        return roleDAO.selectByExample(criteria);
    }
    @Override
    public Role getById(Long id) {
        return roleDAO.selectByPrimaryKey(id);
    }
    
    @Cacheable(value = "commonCache", key = "'Role_id_' + #id")
    @Override
    public Role getByIdInCache(Long id) {
        return roleDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateByIdSelective(Role record) {
        return roleDAO.updateByPrimaryKeySelective(record);
    }

}
