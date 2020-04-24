import java.util.ArrayList;
import java.util.Random;

public class GeneticPathOptimizer implements Runnable
{
	private final RouteFinderListener listener;
	private final RouteFinder routeFinder;
	int populationSize = 250;
	ArrayList<ArrayList<City>> population = new ArrayList<>();
	private volatile boolean running = true;
	private Route optimizedRoute;

	public GeneticPathOptimizer(City[] cities, ArrayList<City> path, RouteFinderListener listener)
	{
		ArrayList<City> pathCopy = (ArrayList<City>) path.clone();
		this.routeFinder = new RouteFinder(cities);
		this.listener = listener;
		optimizedRoute = routeFinder.findMultiRoute(pathCopy);
		optimizedRoute.cities = pathCopy;
	}

	ArrayList<City> mutate(ArrayList<City> toMutate)
	{
		Random randommer = new Random();
		for (int i = 0; i < randommer.nextInt(5); i++)
		{
			int indexA = randommer.nextInt(toMutate.size() - 2);
			int indexB = randommer.nextInt(toMutate.size() - 2);
			indexA++;
			indexB++;

			City temp = toMutate.get(indexA);
			toMutate.set(indexA, toMutate.get(indexB));
			toMutate.set(indexB, temp);
		}
		return toMutate;
	}

	void optimize()
	{
		population.clear();
		for (int i = 0; i < populationSize; i++)
		{
			ArrayList<City> newPath = mutate((ArrayList<City>) optimizedRoute.cities.clone());
			population.add(newPath);

			double pathCost = routeFinder.findMultiRoute(newPath).cost;
			if (pathCost < optimizedRoute.cost)
			{
				optimizedRoute.cities = newPath;
				optimizedRoute.cost = pathCost;
			}
		}

		System.out.println("route found:");
		for (City x : optimizedRoute.cities)
		{
			System.out.print(x.getPlateNum() + " ");
		}
		System.out.println();
		listener.onRouteFound(routeFinder.findMultiRoute(optimizedRoute.cities));
	}

	public void stop()
	{
		this.running = false;
	}

	public boolean isRunning()
	{
		return running;
	}

	@Override
	public void run()
	{
		while (running)
			optimize();
	}
}
