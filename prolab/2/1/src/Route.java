import java.util.ArrayList;
import java.util.List;

public class Route
{
	public double cost = 0;
	public List<City> cities = new ArrayList<>();

	public Route()
	{
	}

	public Route(ArrayList<City> cities)
	{
		this.cities = (List<City>) cities.clone();
	}
}
