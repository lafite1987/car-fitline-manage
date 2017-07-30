package cn.lfy.base.model;

import java.io.Serializable;

public class BaseEntity implements Serializable {
	
	private static final long serialVersionUID = -2704602336279655024L;
	protected Long id;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public boolean isNew() {
		return (id == null);
	}
	

}
