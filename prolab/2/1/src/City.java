import java.util.HashMap;
import java.util.HashSet;

/**
 * Bir şehri temsil eden sınıf.
 */
public class City
{
	public static HashMap<HashSet<Integer>, Integer> costs; // bir şehirden diğerine giden costları içerecek dizi

	private final int plateNum; // plaka
	private final String name; // şehir adı
	private final float lat; // enlem
	private final float lng; // boylam
	private final int[] connected; // bağlı şehirler

	public static void loadCosts(HashMap<HashSet<Integer>, Integer> c)
	{
		costs = c;
	}

	public static int getCost(City from, City to)
	{
		if (from == to)
			return 0;

		HashSet<Integer> set = new HashSet<>();
		set.add(from.plateNum);
		set.add(to.plateNum);

		return costs.get(set);
	}

	public City(int plateNum, String name, float lat, float lng, int[] connected)
	{
		this.plateNum = plateNum;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.connected = connected;
	}

	public int getPlateNum()
	{
		return plateNum;
	}

	public String getName()
	{
		return name;
	}

	public float getLat()
	{
		return lat;
	}

	public float getLng()
	{
		return lng;
	}

	public int[] getConnected()
	{
		return connected;
	}
}
