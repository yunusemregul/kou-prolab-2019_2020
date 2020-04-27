import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		BufferedReader costsReader = new BufferedReader(new FileReader("costs.txt"));
		String line = costsReader.readLine();

		HashMap<HashSet<Integer>, Integer> costs = new HashMap<>();
		for (int lineNum = 0; line != null; lineNum++)
		{
			String[] sCosts = line.split(" ");

			for (int i = 0; i < sCosts.length; i++)
			{
				if (i == lineNum)
					continue;

				int cost = Integer.parseInt(sCosts[i]);
				HashSet<Integer> set = new HashSet<>();
				set.add(lineNum + 1);
				set.add(i + 1);

				costs.put(set, cost);
			}

			line = costsReader.readLine();
		}

		City.loadCosts(costs);

		Gson gson = new Gson();
		BufferedReader citiesReader = new BufferedReader(new FileReader("cities.json"));
		City[] cities = gson.fromJson(citiesReader, City[].class);

		new MapDrawer(cities);
	}
}