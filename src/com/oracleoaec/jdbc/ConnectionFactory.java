package com.oracleoaec.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class ConnectionFactory {
	private static String DRIVER ;
	private static String URL ;
	private static String USER ;
	private static String PASSWORD ;

	//解析properties文件，静态方法，加载类时已经初始化
	static{
		Properties proper = new Properties();
		//解析配置文件转化成流
		InputStream is = ConnectionFactory.class.getResourceAsStream("jdbcinfo.properties");
		try {
			//加载流，  读取流放置到properties中 通过键值对形式
			proper.load(is);
			DRIVER = proper.getProperty("oracle.driver");
			URL = proper.getProperty("oracle.url");
			USER = proper.getProperty("oracle.user");
			PASSWORD = proper.getProperty("oracle.password");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 工厂方法
	 * @retur Connection
	 */
	public static Connection getConnection(){
		Connection conn = null;
		try {
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URL,USER,PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	public static Connection getConnection(String user,String passWord){
		Connection conn = null;
		try {
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URL,user,passWord);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
/*	public static void main(String[] args) {
		try {
			System.out.println(getConnection().getTransactionIsolation());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}
