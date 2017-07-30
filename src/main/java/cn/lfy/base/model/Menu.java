package cn.lfy.base.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Menu implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /**
     * 菜单名称
     */
    private String name = "";

    /**
     * 菜单所属分类，1为内勤人员功能菜单，2为外勤人员功能菜单
     */
    private Integer type = 1;

    /**
     * 父级菜单ID
     */
    private Long parentId;

    /**
     * 父级菜单ID串联，便于查询，格式：$1$2$
     */
    private String parentIdPath = "";

    /**
     * 菜单链接
     */
    private String url = "";

    /**
     * 排序
     */
    private Integer orderNo = 0;

    /**
     * 备注
     */
    private String remark = "";

    /**
     * 数据状态
     */
    private Integer state = 1;

    /**
     * 创建时间
     */
    private Date createTime = new Date();

    /**
     * 菜单所有者，adminId或者operatorId或者roleId
     */
    private Long ownerId;
    
    private Integer onMenu = 0;
    
    private String icon;

    private List<Menu> childList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return 菜单名称
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            菜单名称
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * @return 父级菜单ID串联，便于查询，格式：$1$2$
     */
    public String getParentIdPath() {
        return parentIdPath;
    }

    /**
     * @param parentidpath
     *            父级菜单ID串联，便于查询，格式：$1$2$
     */
    public void setParentIdPath(String parentIdPath) {
        this.parentIdPath = parentIdPath;
    }

    /**
     * @return 菜单链接
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            菜单链接
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return 排序
     */
    public Integer getOrderNo() {
        return orderNo;
    }

    /**
     * @param orderno
     *            排序
     */
    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    /**
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark
     *            备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return 数据状态
     */
    public Integer getState() {
        return state;
    }

    /**
     * @param state
     *            数据状态
     */
    public void setState(Integer state) {
        this.state = state;
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

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public List<Menu> getChildList() {
        return childList;
    }

    public void setChildList(List<Menu> childList) {
        this.childList = childList;
    }

	public Integer getOnMenu() {
		return onMenu;
	}

	public void setOnMenu(Integer onMenu) {
		this.onMenu = onMenu;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
    

}
