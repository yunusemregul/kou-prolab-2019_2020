package com.yunusemregul.prolab23;

import java.sql.*;

/**
 * Database ile ilgili işlemlerden sorumlu sınıf.
 */
public class Database
{

	public void connect()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection("jdbc:sqlite:crud.s3db");
		}
		catch (ClassNotFoundException | SQLException e)
		{
			
		}
	}
}
