package com.oracleoaec.jdbc.service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.oracleoaec.jdbc.ConnectionFactory;
import com.oracleoaec.jdbc.entity.DBUser;
import com.oracleoaec.jdbc.entity.UserFKey;
import com.oracleoaec.jdbc.entity.UserTable;
import com.oracleoaec.jdbc.util.DBUtil;

public class MyDBservice {
	DBUser db_user = null;
	Connection conn = null;
	DatabaseMetaData dmd = null;

	public MyDBservice(DBUser db_user) {
		this.db_user = db_user;
		conn = ConnectionFactory.getConnection(db_user.getName(),db_user.getPwd());
		try {
			dmd = conn.getMetaData();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// 获取逆向生成的sql对象集合
	public List<UserTable> getUserTable() {
		List<UserTable> table_list = new ArrayList<>();
		try {
			List<String> userTableNames = getUserTableNames();// 获得表名
			for (int i = 0; i < userTableNames.size(); i++) {
				table_list.add(new UserTable(userTableNames.get(i)));// 根据表名创建表对象，存入表集合中
			}
			for (int i = 0; i < table_list.size(); i++) {
				// 填充主键
				table_list.get(i).setT_p_keys(getUserTablePrimaryKey(table_list.get(i).getT_name()));
				// 填充建表语句
				table_list.get(i).setCreate_sql(getUserTableCreateSql(table_list.get(i).getT_name()));
				// 填充外键
				table_list.get(i).setT_f_keys(getUserTableImportedKey(table_list.get(i).getT_name()));
			}
			//TODO
			for (UserTable t : table_list) {
				System.out.print(t.getT_name()+":");
				for (String pk : t.getT_p_keys()) {
					System.out.println("主键："+pk);
				}
				for (UserFKey fk : t.getT_f_keys()) {
					System.out.print("外键："+fk.getTable_name()+"--"+fk.getTable_id());
				}
				System.out.println("建表sql:"+t.getCreate_sql());
				System.out.println();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return table_list;
	}
	
	// 获取用户表名
	private List<String> getUserTableNames() throws SQLException {
		List<String> list = new ArrayList<>();
		ResultSet rs = dmd.getTables(null, null, "%", null);
		//TODO
//		 System.out.println("表名" + "," + "表类型" + "," + "表类别");
		while (rs.next()) {
			String name = rs.getString("TABLE_NAME");
			String schem = rs.getString("TABLE_SCHEM");// OAEC用户名
			if (!schem.equals(db_user.getName().toUpperCase())) {
				continue;
			}
			list.add(name);
		}
		DBUtil.close(rs);
		return list;
	}
	//***********************************************************************************************************************
	// 根据表名获取里面的主键
	private List<String> getUserTablePrimaryKey(String name) throws SQLException {
		List<String> pk_list = new ArrayList<>();
		ResultSet rs = dmd.getPrimaryKeys(null, db_user.getName().toUpperCase(), name);
		while (rs.next()) {
			//TODO
			 System.out.println(rs.getString(3) + "表的主键是：" + rs.getString(4) +
			 rs.getString(5) + rs.getString(6));
			pk_list.add(rs.getString(4));
		}
		return pk_list;
	}
	
	// 获取建表语句
	private String getUserTableCreateSql(String name) throws SQLException {
		Connection conn = ConnectionFactory.getConnection(db_user.getName(),db_user.getPwd());
		String sql = "SELECT DBMS_METADATA.GET_DDL('TABLE','" + name + "','" + db_user.getName().toUpperCase()
				+ "') a FROM DUAL";
		Statement cs = conn.createStatement();
		ResultSet rs = cs.executeQuery(sql);
		String str = "";
		if (rs.next()) {
			str = rs.getString(1);
		}
		DBUtil.close(conn,cs, null, rs);
		return str;
	}
	
	// 根据表名中被引用的外键列
	private List<UserFKey> getUserTableImportedKey(String name) throws SQLException {
		List<UserFKey> list = new ArrayList<>();
		ResultSet rs = dmd.getImportedKeys(null, db_user.getName().toUpperCase(), name);
		while (rs.next()) {
			list.add(new UserFKey(rs.getString(3), rs.getString(4)));
		}
		DBUtil.close(rs);
		return list;
	}
	
	public String getUserTableDataSqlFilePath(List<UserTable> list) {
		String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
		Random ran = new Random();
		for (int i = 0; i < 6; i++) {
			date += ran.nextInt(10);
		}
		String path = "datasql/" + date + ".sql";
		String sql_path = null;
		int count = list.size();
		// 把没有外键的表先创建
		for (UserTable t : list) {
			if (t.getT_f_keys().size() == 0) {
				sql_path = insertUserTableDataSqlFilePath(t.getT_name(), path);
				t.setIs_create(true);
				changeIsCreate(list, t);
				count--;
			}
		}
		while (count > 0) {
			// 把外键创建好的表 创建
			for (UserTable t : list) {
				if (t.getT_f_keys().size() > 0) {
					if (!t.isIs_create() && t.isAnyOtherTableNotCreate()) {
						insertUserTableDataSqlFilePath(t.getT_name(), path);
						t.setIs_create(true);
						changeIsCreate(list, t);
						count--;
					}
				}
			}
		}

		return sql_path;
	}
	
	private void changeIsCreate(List<UserTable> table_list, UserTable t) {
		for (UserTable ut : table_list) {
			if (!ut.isIs_create() && ut.getT_f_keys().size() > 0) {
				for (UserFKey fk : ut.getT_f_keys()) {
					if (fk.getTable_name().equals(t.getT_name())) {
						fk.setCreate(true);
					}
				}
			}
		}
	}
	//***************************************************************************************************************
	private String insertUserTableDataSqlFilePath(String t_name, String path) {
		String sql = "select * from " + t_name;
		String sqlPath = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				// 获取此 ResultSet 对象的列的编号、类型和属性。id,name,pwd
				ResultSetMetaData rsmd = rs.getMetaData();
				List<String> list_name = new ArrayList<>();
				List<Object> list_value = new ArrayList<>();
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
					String name = rsmd.getColumnName(i + 1);// 取列名称
					Object value = rs.getObject(name);// 取查到的值
					list_name.add(name);
					list_value.add(value);
				}
				// 拼接sql语句
				String dataSql = createDataSql(list_name, list_value, t_name);
				sqlPath = myWriterDataSql(dataSql, path);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtil.close(null,null, ps, rs);
		}
		return sqlPath;
	}
	
	// 拼接insert_sql
	private String createDataSql(List<String> list_name, List<Object> list_value, String t_name) {
		// INSERT INTO employees (employee_id, name) VALUES (1, 'Zhangsan');

		StringBuilder sb = new StringBuilder("INSERT INTO ");
		sb.append(t_name + " (");
		for (int i = 0; i < list_name.size(); i++) {
			if (i == list_name.size() - 1) {
				sb.append(list_name.get(i) + ") VALUES (");
			} else {
				sb.append(list_name.get(i) + ",");
			}
		}
		for (int i = 0; i < list_value.size(); i++) {
			if (i == list_value.size() - 1) {
				sb.append("'" + list_value.get(i) + "');");
			} else {
				sb.append("'" + list_value.get(i) + "',");
			}
		}
		return sb.toString();
	}
	
	private String myWriterDataSql(String sql, String path) {

		Writer w = null;
		try {
			w = new FileWriter(path, true);// 追加模式
			w.write(sql);
			System.out.println("备份数据：" + sql);
			w.flush();
			return path;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (w != null) {
				try {
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
//	// 测试
//	public static void main(String[] args) {
//		MyDBservice db = new MyDBservice(new DBUser("projectDemo", "123"));
//		List<UserTable> list = db.getUserTable();
//		db.getUserTableDataSqlFilePath(list);
//	}
}
