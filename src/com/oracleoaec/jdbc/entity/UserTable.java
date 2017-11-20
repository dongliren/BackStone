package com.oracleoaec.jdbc.entity;

import java.util.List;

public class UserTable {
	private String t_name;//当前表的表名
	private List<String> t_p_keys;//当前表所拥有的主键集合
	private List<UserFKey> t_f_keys;//当前表所拥有的外键集合
	private String create_sql;//建表语句
	private boolean is_create = false;
	
	public UserTable() {
		super();
	}
	public UserTable(String t_name) {
		super();
		this.t_name = t_name;
	}
	public UserTable(String t_name, List<String> t_p_keys, List<UserFKey> t_f_keys, String create_sql) {
		super();
		this.t_name = t_name;
		this.t_p_keys = t_p_keys;
		this.t_f_keys = t_f_keys;
		this.create_sql = create_sql;
	}
	
	public boolean isAnyOtherTableNotCreate(){
		for (UserFKey fk : t_f_keys) {
			if(!fk.isCreate()){
				return false;
			}
		}
		return true;
	}
	@Override
	public String toString() {
		return "UserTable [t_name=" + t_name + ", t_p_keys=" + t_p_keys + ", t_f_keys=" + t_f_keys + ", create_sql="
				+ create_sql + "]";
	}
	public String getT_name() {
		return t_name;
	}
	public void setT_name(String t_name) {
		this.t_name = t_name;
	}
	public List<String> getT_p_keys() {
		return t_p_keys;
	}
	public void setT_p_keys(List<String> t_p_keys) {
		this.t_p_keys = t_p_keys;
	}
	public List<UserFKey> getT_f_keys() {
		return t_f_keys;
	}
	public void setT_f_keys(List<UserFKey> t_f_keys) {
		this.t_f_keys = t_f_keys;
	}
	public String getCreate_sql() {
		return create_sql;
	}
	public void setCreate_sql(String create_sql) {
		this.create_sql = create_sql;
	}
	public boolean isIs_create() {
		return is_create;
	}
	public void setIs_create(boolean is_create) {
		this.is_create = is_create;
	}
}
