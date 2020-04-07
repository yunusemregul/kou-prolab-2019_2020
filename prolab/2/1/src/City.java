public class City
{
	private int plateNum; // plaka
	private String name; // şehir adı
	private float lat; // enlem
	private float lng; // boylam
	private int[] connected; // bağlı şehirler

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
