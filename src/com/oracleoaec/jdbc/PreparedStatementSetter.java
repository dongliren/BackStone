package com.oracleoaec.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface PreparedStatementSetter {
	//一个回调函数，给PreparedStatement的所要执行的SQL语句中的占位符替换成具体的数据
	public void setValues(PreparedStatement ps) throws SQLException;
}
