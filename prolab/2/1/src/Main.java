import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Gson gson = new Gson();
		BufferedReader reader = new BufferedReader(new FileReader("data.json"));
		City[] cities = gson.fromJson(reader, City[].class);

		MapDrawer mapDrawer = new MapDrawer(cities);
		mapDrawer.init();
	}
}