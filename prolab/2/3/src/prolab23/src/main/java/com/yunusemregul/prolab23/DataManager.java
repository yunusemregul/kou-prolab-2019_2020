package com.yunusemregul.prolab23;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Database ile ilgili işlemlerden sorumlu sınıf.
 */
public class DataManager
{

	private static DataManager instance = null;
	private Connection conn = null;
	public String lastError = "";

	/**
	 * DataManager constructor metodu. SQLite driver sınıfına ulaşılamadığında
	 * hata veriyor.
	 */
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

		connect();
	}

	/**
	 * Tüm uygulama genelinde 1 tane DataManager olmasının daha iyi olacağını
	 * düşündüğümden Singleton patternini kullandım. Bu metot eğer bir
	 * DataManager oluşturulmadıysa oluşturup, oluşturulduysa var olan
	 * DataManager i döndürüyor.
	 *
	 * Sadece 1 tane DataManager olmazsa database üzerinde birden çok connection
	 * olmasından 'database is locked' hatası alınabilir.
	 *
	 * @return DataManager
	 */
	public static DataManager getInstance()
	{
		if (instance == null)
		{
			instance = new DataManager();
		}

		return instance;
	}

	/**
	 * Databaseye bağlanmak için kullanılan metot.
	 */
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
			lastError = e.getMessage();
		}
	}

	/**
	 * Tüm türleri döndüren metot.
	 *
	 * @return tüm türleri içeren ArrayList
	 */
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
			lastError = e.getMessage();
			return null;
		}
	}

	/**
	 * Belirli bir tür için en iyi puanlı iki filmi ve puanlarını döndüren
	 * metot.
	 *
	 * @param tur tür
	 * @return key leri film adı, value leri film puanı olan HashMap
	 */
	public HashMap<String, Float> getTopTwoForTur(String tur)
	{
		String sql = "SELECT Program.ad, AVG(KullaniciProgram.puan) AS puan FROM Program "
				+ "JOIN KullaniciProgram ON Program.id = KullaniciProgram.program_id "
				+ "JOIN ProgramTur ON ProgramTur.program_id = Program.id "
				+ "WHERE ProgramTur.tur_id = (SELECT Tur.id FROM Tur WHERE Tur.ad = ?) "
				+ "GROUP BY Program.ad "
				+ "ORDER BY puan DESC LIMIT 2";

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
			System.out.println(e.getMessage());
			lastError = e.getMessage();
			return null;
		}
	}

	public ArrayList<Movie> getMovies()
	{
		String sql = "SELECT Program.ad, AVG(KullaniciProgram.puan) AS puan, GROUP_CONCAT(DISTINCT Tur.ad) AS tur, Program.tip FROM Program "
				+ "LEFT JOIN KullaniciProgram ON Program.id = KullaniciProgram.program_id "
				+ "JOIN ProgramTur ON ProgramTur.program_id = Program.id "
				+ "JOIN Tur ON Tur.id = ProgramTur.tur_id "
				+ "GROUP BY Program.ad";

		try (Statement stat = conn.createStatement())
		{
			ResultSet result = stat.executeQuery(sql);

			ArrayList<Movie> movies = new ArrayList<Movie>();

			while (result.next())
			{
				Movie movie = new Movie(
						result.getString("ad"),
						String.format("%.1f", result.getFloat("puan")).replace(",", "."),
						result.getString("tur"),
						result.getString("tip")
				);

				movies.add(movie);
			}

			return movies;
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			lastError = e.getMessage();
			return null;
		}
	}

	/**
	 * Kullanıcı şifrelerini databasede düz halde saklamak güvenlik açısından
	 * çok sıkıntılı bir şey olduğundan kullanıcı şifrelerini hashlenmiş halde
	 * saklıyoruz. Normalde bcrypt gibi algoritmalar kullanılmalı ama bu proje
	 * için MD5 kullandım.
	 *
	 * Bu proje için gerekli olmayabilir ama alışkanlık oluşması ve özen
	 * açısından önemli.
	 *
	 * @param pass hashlenecek şifre
	 * @return hashlenmiş şifre
	 */
	public String MD5(String pass)
	{
		String hashed = null;

		try
		{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(pass.getBytes());
			byte[] bytes = md.digest();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++)
			{
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			hashed = sb.toString();
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}

		return hashed;
	}

	/**
	 * Bir kullanıcıyı databaseye kayıt etmek için çağrılan metot.
	 *
	 * @param name kullanıcı ismi
	 * @param email emaili
	 * @param pass şifresi
	 * @param birthdate doğum tarihi
	 * @return kayıt başarılıysa true değilse false
	 */
	public boolean registerUser(String name, String email, String pass, String birthdate)
	{
		String sql = "INSERT INTO Kullanici (ad, email, sifre_hash, dogum_tarihi) VALUES (?, ?, ?, ?)";
		try (PreparedStatement stat = conn.prepareStatement(sql))
		{
			stat.setObject(1, name);
			stat.setObject(2, email);

			String hashedPass = MD5(pass);

			stat.setObject(3, hashedPass);
			stat.setObject(4, birthdate);
			stat.executeUpdate();
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			lastError = e.getMessage();
			return false;
		}

		return true;
	}

	public boolean loginUser(String email, String pass)
	{
		String hashedPass = MD5(pass);

		String sql = "SELECT ad FROM Kullanici WHERE email = ? AND sifre_hash = ?";

		try (PreparedStatement stat = conn.prepareStatement(sql))
		{
			stat.setObject(1, email);
			stat.setObject(2, hashedPass);

			ResultSet result = stat.executeQuery();

			if (result.next())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
			lastError = e.getMessage();
			return false;
		}
	}
}
