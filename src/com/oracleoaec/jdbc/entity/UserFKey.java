package com.oracleoaec.jdbc.entity;

public class UserFKey {
	private String table_name;//外键关联的表名 
	private String table_id;//外键关联的表id名
	private boolean isCreate = false;//关联的表是否已经创建
	@Override
	public String toString() {
		return "UserFKey [table_name=" + table_name + ", table_id=" + table_id + ", isCreate=" + isCreate + "]";
	}
	public UserFKey() {
		super();
	}
	public UserFKey(String table_name, String table_id) {
		super();
		this.table_name = table_name;
		this.table_id = table_id;
	}
	public UserFKey(String table_name, String table_id, boolean isCreate) {
		super();
		this.table_name = table_name;
		this.table_id = table_id;
		this.isCreate = isCreate;
	}
	public String getTable_name() {
		return table_name;
	}
	public void setTable_name(String table_name) {
		this.table_name = table_name;
	}
	public String getTable_id() {
		return table_id;
	}
	public void setTable_id(String table_id) {
		this.table_id = table_id;
	}
	public boolean isCreate() {
		return isCreate;
	}
	public void setCreate(boolean isCreate) {
		this.isCreate = isCreate;
	}
	
}
