public class Node
{
	private int id; // plaka
	private String name; // şehir adı
	private float lat; // latitude
	private float lng; // longitude

	public Node(int id, String name, float lat, float lng)
	{
		this.id = id;
		this.name = name;
		this.lat = lat;
		this.lng = lng;
	}

	public int getId()
	{
		return id;
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
}
