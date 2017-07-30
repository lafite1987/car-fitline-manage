package cn.lfy.base.model;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 公用条件查询类
 */
public class Criteria extends HashMap<String, Object> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 3484255941736400409L;

    /**
     * 是否相异
     */
    protected boolean distinct;

    /**
     * 排序字段
     */
    protected LinkedHashMap<String, Boolean> orderMap = new LinkedHashMap<String, Boolean>();

    private Integer offset;

    private Integer rows;

    protected Criteria(Criteria example) {
        this.distinct = example.distinct;
        this.offset = example.offset;
        this.rows = example.rows;
        this.orderMap = example.orderMap;
    }

    public Criteria() {
        orderMap = new LinkedHashMap<String, Boolean>();
    }

    public void clear() {
        clear();
        distinct = false;
        this.offset = null;
        this.rows = null;
    }

    /**
     * @param condition 
	 *            查询的条件名称
	 * @param value
	 *            查询的值
     */
    public Criteria put(String condition, Object value) {
        super.put(condition, value);
        return (Criteria) this;
    }

    public Criteria putOrder(String condition, boolean isDesc) {
    	this.orderMap.put(condition, isDesc);
    	return (Criteria) this;
    }

	/**
     * @param distinct 
	 *            是否相异
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public LinkedHashMap<String, Boolean> getOrderMap() {
		return orderMap;
	}

	public void setOrderMap(LinkedHashMap<String, Boolean> orderMap) {
		this.orderMap = orderMap;
	}

	/**
     * @param mysqlOffset 
	 *            指定返回记录行的偏移量<br>
	 *            mysqlOffset= 5,mysqlLength=10;  // 检索记录行 6-15
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * @param mysqlLength 
	 *            指定返回记录行的最大数目<br>
	 *            mysqlOffset= 5,mysqlLength=10;  // 检索记录行 6-15
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }
}
