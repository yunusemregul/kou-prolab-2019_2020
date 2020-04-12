import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Gson gson = new Gson();
		BufferedReader reader = new BufferedReader(new FileReader("data.json"));
		City[] cities = gson.fromJson(reader, City[].class);

		MapDrawer mapDrawer = new MapDrawer(cities);
		mapDrawer.init();

		RouteFinder routeFinder = new RouteFinder(cities);
		long startTime = System.nanoTime();
		Route route = routeFinder.findRoute(cities[33], cities[30]);
		long endTime = System.nanoTime();
		System.out.println((endTime - startTime) / 1000000);
		System.out.println(route.cities.stream().map(City::getName).collect(Collectors.toList()));
		System.out.println(route.cost);
	}
}