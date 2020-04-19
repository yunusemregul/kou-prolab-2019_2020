import java.util.HashSet;

public class City
{
	public static HashSet<Edge> costs; // bir şehirden diğerine giden costları içerecek dizi

	private final int plateNum; // plaka
	private final String name; // şehir adı
	private final float lat; // enlem
	private final float lng; // boylam
	private final int[] connected; // bağlı şehirler

	public static void loadCosts(HashSet<Edge> c)
	{
		costs = c;
	}

	public static Edge getEdge(City from, City to)
	{
		return (new Edge(from.getPlateNum(), to.getPlateNum())).findInEdges(costs);
	}

	public static int getCost(City from, City to)
	{
		if (from == to)
			return 0;

		return getEdge(from, to).cost;
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
