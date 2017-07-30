package cn.lfy.base.model;

import java.io.Serializable;

public class RoleMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 权限菜单id
     */
    private Long menuId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * @return 权限菜单id
     */
    public Long getMenuId() {
        return menuId;
    }

    /**
     * @param menuid 
	 *            权限菜单id
     */
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    /**
     * @return 角色id
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * @param roleid 
	 *            角色id
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
