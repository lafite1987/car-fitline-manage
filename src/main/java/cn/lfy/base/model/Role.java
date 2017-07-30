package cn.lfy.base.model;

import java.io.Serializable;
import java.util.Date;

public class Role implements Serializable, Comparable<Role> {
    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 父角色ID
     */
    private Long parentId;
    /**
     * 父節點路徑
     */
    private String parentIdPath;

    /**
     * 员工管理角色名称
     */
    private String name;

    /**
     * 角色分类，1 员工，2 客户
     */
    private Integer type;

    /**
     * 描述
     */
    private String desc;

    /**
     * 数据状态
     */
    private Integer state = 1;
    
    /**
     * 权限
     */
    private Integer popedom;

    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 角色級別
     */
    private Integer level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	/**
     * @return 内勤管理角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            内勤管理角色名称
     */
    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
    
    /**
     * @return 描述
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc
     *            描述
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getPopedom() {
		return popedom;
	}

	public void setPopedom(Integer popedom) {
		this.popedom = popedom;
	}

	/**
     * @return 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createtime
     *            创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getParentIdPath() {
		return parentIdPath;
	}

	public void setParentIdPath(String parentIdPath) {
		this.parentIdPath = parentIdPath;
	}

	@Override
	public int compareTo(Role o) {
		if(this.id == o.getId()) {
			return 0;
		} else if(this.id > o.getId()) {
			return 1;
		}
		return -1;
	}
}
