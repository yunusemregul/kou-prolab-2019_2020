import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		File costF = new File("res_mesafeler.txt");
		File cityF = new File("res_sehirler.txt");

		if (!costF.exists() || !cityF.exists())
		{
			if (!costF.exists())
				System.out.println("res_mesafeler.txt dosyasi bulunamadi.");
			if (!cityF.exists())
				System.out.println("res_sehirler.txt dosyasi bulunamadi.");
			return;
		}

		BufferedReader costsReader = new BufferedReader(new FileReader("res_mesafeler.txt"));
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

		ArrayList<City> citiesArrayList = new ArrayList<>();

		BufferedReader citiesReader = new BufferedReader(new FileReader("res_sehirler.txt"));
		line = citiesReader.readLine();
		while (line != null)
		{
			String[] pairs = line.split(" ");
			City toAdd = new City();

			for (String pair : pairs)
			{
				String[] kv = pair.split("=");
				String key = kv[0];
				String value = kv[1];

				switch (key)
				{
					case "plaka":
					{
						toAdd.setPlateNum(Integer.parseInt(value));
						break;
					}
					case "ad":
					{
						toAdd.setName(value);
						break;
					}
					case "enlem":
					{
						toAdd.setLat(Float.parseFloat(value));
						break;
					}
					case "boylam":
					{
						toAdd.setLng(Float.parseFloat(value));
						break;
					}
					case "komsular":
					{
						String[] adjacentsStr = value.split(",");
						int[] adjacentsInt = new int[adjacentsStr.length];

						for (int i = 0; i < adjacentsStr.length; i++)
							adjacentsInt[i] = Integer.parseInt(adjacentsStr[i]);

						toAdd.setConnected(adjacentsInt);
						break;
					}
				}
			}

			citiesArrayList.add(toAdd);
			line = citiesReader.readLine();
		}

		City[] cities = citiesArrayList.toArray(new City[0]);

		new MapDrawer(cities);
	}
}