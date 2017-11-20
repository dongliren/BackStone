package com.oracleoaec.jdbc.entity;

public class DBUser {
	private long id;
	private String name;
	private String pwd;
	private String path;//xmlpath
	private String sqlpath;//数据的脚本文件

	public DBUser(){}
	
	public DBUser(String name, String pwd) {
		super();
		this.name = name;
		this.pwd = pwd;
	}

	public DBUser(long id, String name, String pwd, String path, String sqlpath) {
		super();
		this.id = id;
		this.name = name;
		this.pwd = pwd;
		this.path = path;
		this.sqlpath = sqlpath;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getSqlpath() {
		return sqlpath;
	}
	public void setSqlpath(String sqlpath) {
		this.sqlpath = sqlpath;
	}
	
}
