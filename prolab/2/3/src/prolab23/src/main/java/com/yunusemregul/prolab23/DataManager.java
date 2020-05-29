package com.yunusemregul.prolab23;

import java.sql.*;
import java.util.ArrayList;

/**
 * Database ile ilgili işlemlerden sorumlu sınıf.
 */
public class DataManager
{
	public Connection conn = null;
	
	public void connect()
	{
		try
		{
			String url = "jdbc:sqlite:data.db";
			conn = DriverManager.getConnection(url);

			System.out.println("Connection to SQLite has been established.");
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	
	public ArrayList<String> getTurler()
	{
		try(Statement stat = conn.createStatement())
		{
			ResultSet result = stat.executeQuery("SELECT ad FROM Tur");
			
			ArrayList<String> turler = new ArrayList<String>();
			
			int count = 0;
			while(result.next())
			{
				turler.add(result.getString("ad"));
				count++;
			}
			
			return turler;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
