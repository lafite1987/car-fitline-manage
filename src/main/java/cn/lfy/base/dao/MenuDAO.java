package cn.lfy.base.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import cn.lfy.base.model.Criteria;
import cn.lfy.base.model.Menu;

public interface MenuDAO {

    /**
     * 根据主键删除记录
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 保存记录,不管记录里面的属性是否为空
     */
    int insert(Menu record);

    /**
     * 根据条件查询记录集
     */
    List<Menu> selectByExample(@Param("condition")Criteria example);

    /**
     * 根据主键查询记录
     */
    Menu selectByPrimaryKey(Long id);

    /**
     * 根据主键更新属性不为空的记录
     */
    int updateByPrimaryKeySelective(Menu record);
    
    /**
     * 更新节点的子节点的parentIdPath
     * @param oldParentIdPath
     * @param newParentIdPath
     */
    void updateChildParentPath(@Param("oldParentIdPath") String oldParentIdPath, @Param("newParentIdPath") String newParentIdPath);

}