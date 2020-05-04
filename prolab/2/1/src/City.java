import java.util.HashMap;
import java.util.HashSet;

/**
 * Bir şehri temsil eden sınıf.
 */
public class City
{
	public static HashMap<HashSet<Integer>, Integer> costs; // bir şehirden diğerine giden costları içerecek dizi

	private int plateNum; // plaka
	private String name; // şehir adı
	private float lat; // enlem
	private float lng; // boylam
	private int[] connected; // bağlı şehirler

	/**
	 * Şehir yapıcı metodu.
	 *
	 * @param plateNum  plaka
	 * @param name      isim
	 * @param lat       enlem
	 * @param lng       boylam
	 * @param connected komşu şehirler
	 */
	public City(int plateNum, String name, float lat, float lng, int[] connected)
	{
		this.plateNum = plateNum;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.connected = connected;
	}

	public City()
	{
		this(-1, null, -1, -1, null);
	}

	/**
	 * Şehirler arası mesafeleri bu sınıfın üzerine static olarak kaydeden metot.
	 *
	 * @param c şehirler arası mesafeler
	 */
	public static void loadCosts(HashMap<HashSet<Integer>, Integer> c)
	{
		costs = c;
	}

	/**
	 * Bir şehirden diğerine mesafeyi döndüren metot.
	 *
	 * @param from başlangıç şehri
	 * @param to   hedef şehir
	 * @return mesafe
	 */
	public static int getCost(City from, City to)
	{
		if (from == to)
			return 0;

		HashSet<Integer> set = new HashSet<>();
		set.add(from.plateNum);
		set.add(to.plateNum);

		return costs.get(set);
	}

	public int getPlateNum()
	{
		return this.plateNum;
	}

	public String getName()
	{
		return this.name;
	}

	public float getLat()
	{
		return this.lat;
	}

	public float getLng()
	{
		return this.lng;
	}

	public int[] getConnected()
	{
		return this.connected;
	}

	public void setPlateNum(int plateNum)
	{
		this.plateNum = plateNum;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setLat(float lat)
	{
		this.lat = lat;
	}

	public void setLng(float lng)
	{
		this.lng = lng;
	}

	public void setConnected(int[] connected)
	{
		this.connected = connected;
	}
}
