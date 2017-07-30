package cn.lfy.base.model;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 登录名
     */
    private String username;

    /**
     * 登录密码，保存md5值
     */
    private String password;
    /**
     * 盐
     */
    private String salt;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 公司的个人邮箱，邮件提醒功能
     */
    private String email;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 数据状态
     */
    private Integer state;

    /**
     * 创建时间
     */
    private Date createTime;
    
    private Integer pageIndex;
    
    private Integer pageSize;
    
    /**
     * @return 主键
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id 
	 *            主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return 登录名
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username 
	 *            登录名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return 登录密码，保存md5值
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password 
	 *            登录密码，保存md5值
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	/**
     * @return 昵称
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 昵称
     * @param nickname 
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @return 公司的个人邮箱，邮件提醒功能
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email 
	 *            公司的个人邮箱，邮件提醒功能
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return 联系电话
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone 
	 *            联系电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
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

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
