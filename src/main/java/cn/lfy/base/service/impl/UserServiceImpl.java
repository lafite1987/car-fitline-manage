package cn.lfy.base.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lfy.base.dao.UserDAO;
import cn.lfy.base.model.Criteria;
import cn.lfy.base.model.User;
import cn.lfy.base.service.UserService;
import cn.lfy.common.page.Page;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDAO userDAO;
    
    @Override
    public int countByCriteria(Criteria criteria) {
        return userDAO.countByExample(criteria);
    }

    @Override
    public Long add(User record) {
        userDAO.insert(record);
        return record.getId();
    }

    @Override
    public User findById(Long id) {
        return userDAO.selectByPrimaryKey(id);
    }

    @Override
    public int updateByIdSelective(User record) {
        return userDAO.updateByPrimaryKeySelective(record);
    }

    @Override
    public User findByUsername(String username) {
        return userDAO.selectByUsername(username);
    }

    @Override
    public Page<User> findListByCriteria(Criteria criteria, int pageIndex, int pageSize) {
        return userDAO.selectByExample(criteria, pageIndex, pageSize);
    }
    
}
