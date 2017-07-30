package cn.lfy.base.dao;

import org.apache.ibatis.annotations.Param;

import cn.lfy.base.model.Criteria;
import cn.lfy.base.model.User;
import cn.lfy.common.page.Page;

public interface UserDAO {
    /**
     * 根据条件查询记录总数
     */
    int countByExample(Criteria example);

    /**
     * 根据主键删除记录
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 保存记录,不管记录里面的属性是否为空
     */
    int insert(User record);

    /**
     * 根据条件查询记录集
     */
    Page<User> selectByExample(@Param("condition")Criteria example, @Param("pageIndex") int pageindex, @Param("pageSize")int pageSize);

    /**
     * 根据主键查询记录
     */
    User selectByPrimaryKey(Long id);

    /**
     * 根据主键更新属性不为空的记录
     */
    int updateByPrimaryKeySelective(User record);
    
    /**
     * 根据登录名查询，username唯一
     * @param username
     * @return
     */
    User selectByUsername(String username);

    Page<User> listPage(@Param("pageIndex") int pageNum, @Param("pageSize") int pageSize);
    
    Page<User> list2(User user);
    
    Page<User> list3();
}