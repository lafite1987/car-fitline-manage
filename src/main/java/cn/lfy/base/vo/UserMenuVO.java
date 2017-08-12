package cn.lfy.base.vo;

import java.util.List;

import cn.lfy.base.model.Menu;
import cn.lfy.base.model.User;

public class UserMenuVO {

	private User user;
	
	private List<Menu> menus;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

}
