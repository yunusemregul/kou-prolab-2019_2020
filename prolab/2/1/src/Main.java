import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader costsReader = new BufferedReader(new FileReader("costs.txt"));
		String line = costsReader.readLine();

		HashSet<Edge> costs = new HashSet<>();
		for (int lineNum = 0; line != null; lineNum++)
		{
			String[] sCosts = line.split(" ");

			for (int i = 0; i < sCosts.length; i++)
			{
				if (i == lineNum)
					continue;

				int cost = Integer.parseInt(sCosts[i]);
				Edge edge = new Edge(lineNum + 1, i + 1, cost);
				costs.add(edge);
			}

			line = costsReader.readLine();
		}

		City.loadCosts(costs);

		Gson gson = new Gson();
		BufferedReader citiesReader = new BufferedReader(new FileReader("cities.json"));
		City[] cities = gson.fromJson(citiesReader, City[].class);

		MapDrawer mapDrawer = new MapDrawer(cities);
		mapDrawer.init();
	}
}