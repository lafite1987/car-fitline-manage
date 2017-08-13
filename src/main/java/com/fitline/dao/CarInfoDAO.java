package com.fitline.dao;

import org.apache.ibatis.annotations.Param;

import com.fitline.model.CarInfo;

import cn.lfy.base.model.Criteria;
import cn.lfy.common.page.Page;

public interface CarInfoDAO {

	/**
     * 保存记录,不管记录里面的属性是否为空
     */
    int insert(CarInfo record);
    
    /**
     * 根据主键删除记录
     */
    int deleteByPrimaryKey(Long id);
    
    /**
     * 根据主键更新属性不为空的记录
     */
    int updateByPrimaryKeySelective(CarInfo record);
    
    /**
     * 根据主键查询记录
     */
    CarInfo selectByPrimaryKey(Long id);
    
    /**
     * 根据条件查询记录集
     */
    Page<CarInfo> query(@Param("criteria")Criteria criteria, @Param("pageIndex") int pageindex, @Param("pageSize")int pageSize);


}
