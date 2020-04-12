public class City
{
	private final int plateNum; // plaka
	private final String name; // şehir adı
	private final float lat; // enlem
	private final float lng; // boylam
	private final int[] connected; // bağlı şehirler

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
