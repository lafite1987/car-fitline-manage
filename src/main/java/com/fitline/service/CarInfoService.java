package com.fitline.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fitline.dao.CarInfoDAO;
import com.fitline.model.CarInfo;

import cn.lfy.base.model.Criteria;
import cn.lfy.common.page.Page;

@Service
public class CarInfoService {

	@Autowired
	private CarInfoDAO carInfoDAO;
	
	/**
     * 保存记录,不管记录里面的属性是否为空
     */
    public int insert(CarInfo record) {
    	return carInfoDAO.insert(record);
    }
    
    /**
     * 根据主键删除记录
     */
    public int deleteByPrimaryKey(Long id) {
    	return carInfoDAO.deleteByPrimaryKey(id);
    }
    
    /**
     * 根据主键更新属性不为空的记录
     */
    public int updateByPrimaryKeySelective(CarInfo record) {
    	return carInfoDAO.updateByPrimaryKeySelective(record);
    }
    
    /**
     * 根据主键查询记录
     */
    public CarInfo selectByPrimaryKey(Long id) {
    	return carInfoDAO.selectByPrimaryKey(id);
    }
    
    /**
     * 根据条件查询记录集
     */
    public Page<CarInfo> query(Criteria criteria, int pageindex, int pageSize) {
    	return carInfoDAO.query(criteria, pageindex, pageSize);
    }
}
