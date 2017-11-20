package com.oracleoaec.jdbc;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.oracleoaec.jdbc.util.DBUtil;


public class JDBCTemplate<T> {
	/**
	 * 执行数据库更新操作(insert delete update)
	 * @param sql SQL语句
	 * @param params 参数
	 * @return 更新是否成功
	 */
	public int update(String sql,PreparedStatementSetter setter) {
		Connection conn = null;
		PreparedStatement ps = null;
		int row = 0;//受影响的行数
		try {
			//1.加载驱动2.连接数据库
			conn = ConnectionFactory.getConnection();
			//3.欲加载sql语句 获得数据库操作对象
			ps = conn.prepareStatement(sql);//装载sql语句
			//3.5占位符替换
			if (setter != null) {
				setter.setValues(ps);// 替换占位符
			}
			//4.执行sql语句
			row = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			//6.关闭资源
			DBUtil.close(conn,null, ps, null);
		}
		return row;
	}
//	User.class
	public List<T> query(Class<T> t,String sql,PreparedStatementSetter setter){
		List<T> list = null;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			conn = ConnectionFactory.getConnection();
			ps = conn.prepareStatement(sql);
			if (setter != null) {
				setter.setValues(ps);// 替换占位符
			}
			rs = ps.executeQuery();
			list = new ArrayList<T>();
			//5.使用结果集
			while (rs.next()) {
				//获取此 ResultSet 对象的列的编号、类型和属性。id,name,pwd
				ResultSetMetaData rsmd = rs.getMetaData();
				T obj = t.newInstance();//获得实例
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					String name = rsmd.getColumnName(i+1);//取列名称
					Object value = rs.getObject(name);//取查到的值
					Field field = t.getDeclaredField(name.toLowerCase());
					field.setAccessible(true);// 使用单一安全性检查（为了提高效率）为一组对象设置 accessible 标志的便捷方法。
					if(value instanceof BigDecimal){
						BigDecimal v = (BigDecimal) value;
						long longValue = v.longValue();
						field.set(obj, longValue);
					}else{
						field.set(obj, value);
					}
				}
				list.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}finally{
			DBUtil.close(conn,null, ps, rs);
		}
		return list;
	}
}
