import java.util.ArrayList;

/**
 * Bir sürü şehir içeren rota.
 */
public class Route
{
	public double cost = 0;
	public ArrayList<City> cities = new ArrayList<>();

	public Route()
	{
	}

	public Route(ArrayList<City> cities)
	{
		this.cities = (ArrayList<City>) cities.clone();
		this.cost = Double.POSITIVE_INFINITY;
	}
}
