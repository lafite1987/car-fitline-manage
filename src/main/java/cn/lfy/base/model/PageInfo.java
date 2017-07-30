package cn.lfy.base.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询结果集合返回
 * 
 * 
 * @param <T>
 */
public class PageInfo<T> {
	// 总数
	private int total;

	// 开始,默认为0
	private int offset;
	
	// 当前页码,默认为1
	private int nowPage;

	// 页面大小,默认为20
	private int pageSize;

	//总页数
	private int totalPage;

	// 查询出来的数据
	private List<T> data = new ArrayList<T>();

	public PageInfo() {
	}

	public PageInfo(int currentPage, int pageSize) {
		init(currentPage, pageSize);
	}

	public void init(int nowPage, int pageSize) {
		// 当传入的数不大于0时,把当前页面设为了1,页面大小设为20
		if (nowPage < 1) {
			this.nowPage = 1;
		} else {
			this.nowPage = nowPage + 1;
		}
		this.pageSize = pageSize;
		this.offset = (this.nowPage - 1) * this.pageSize;
	}

	public void setTotal(int total) {
		this.total = total;
		this.totalPage = (int) Math.ceil((double) total / this.pageSize);
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getNowPage() {
		return nowPage;
	}

	public void setNowPage(int nowPage) {
		this.nowPage = nowPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public List<T> getData() {
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}

	public int getTotal() {
		return total;
	}


}
