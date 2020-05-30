package com.yunusemregul.prolab23;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Database ile ilgili işlemlerden sorumlu sınıf.
 */
public class DataManager
{

	public Connection conn = null;

	public DataManager()
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e)
		{
			System.out.println("org.sqlite.JDBC bulunamadi!");
		}
	}
	
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
		try (Statement stat = conn.createStatement())
		{
			ResultSet result = stat.executeQuery("SELECT ad FROM Tur");

			ArrayList<String> turler = new ArrayList<String>();

			int count = 0;
			while (result.next())
			{
				turler.add(result.getString("ad"));
				count++;
			}

			return turler;
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			return null;
		}
	}

	public HashMap<String, Float> getTopTwoForTur(String tur)
	{
		String sql = "SELECT Program.ad, KullaniciProgram.puan FROM Program "
				+ "INNER JOIN KullaniciProgram ON Program.id = KullaniciProgram.program_id "
				+ "INNER JOIN ProgramTur ON ProgramTur.program_id = Program.id "
				+ "WHERE ProgramTur.tur_id = (SELECT Tur.id FROM Tur WHERE Tur.ad = ?) "
				+ "ORDER BY KullaniciProgram.puan DESC LIMIT 2";

		try (PreparedStatement stat = conn.prepareStatement(sql))
		{
			stat.setObject(1, tur);
			ResultSet result = stat.executeQuery();

			HashMap<String, Float> adpuan = new HashMap<String, Float>();

			int count = 0;
			while (result.next())
			{
				adpuan.put(result.getString("ad"), result.getFloat("puan"));
				count++;
			}

			return adpuan;
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}
