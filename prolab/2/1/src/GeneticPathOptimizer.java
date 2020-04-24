import java.util.ArrayList;
import java.util.Random;

public class GeneticPathOptimizer implements Runnable
{
	private final RouteFinderListener listener;
	private final RouteFinder routeFinder;

	private final int populationSize = 100;

	ArrayList<ArrayList<City>> population = new ArrayList<>();
	float[] fitness = new float[populationSize];
	private volatile boolean running = true;
	private Route optimizedRoute;

	public GeneticPathOptimizer(City[] cities, ArrayList<City> path, RouteFinderListener listener)
	{
		ArrayList<City> pathCopy = (ArrayList<City>) path.clone();
		this.routeFinder = new RouteFinder(cities);
		this.listener = listener;
		optimizedRoute = routeFinder.findMultiRoute(pathCopy);
		optimizedRoute.cities = pathCopy;

		for (int i = 0; i < populationSize; i++)
		{
			ArrayList<City> newPath = mutate(path);
			population.add(newPath);
		}
	}

	private ArrayList<City> mutate(ArrayList<City> toMutate)
	{
		if (Math.random() < .1)
		{
			Random randommer = new Random();
			int indexA = 1 + randommer.nextInt(toMutate.size() - 2);
			int indexB = 1 + randommer.nextInt(toMutate.size() - 2);

			City temp = toMutate.get(indexA);
			toMutate.set(indexA, toMutate.get(indexB));
			toMutate.set(indexB, temp);
		}
		return toMutate;
	}

	private ArrayList<City> crossOver(ArrayList<City> pathA, ArrayList<City> pathB)
	{
		ArrayList<City> crossed = new ArrayList<>();
		Random randommer = new Random();
		int indexA = 1 + randommer.nextInt(pathA.size() - 2);
		int indexB = indexA + 1 + ((pathA.size() - 2 - indexA) > 0 ? randommer.nextInt(pathA.size() - 2 - indexA) : 0);

		crossed.add(pathA.get(0));

		for (int i = indexA; i <= indexB; i++)
			crossed.add(pathA.get(i));

		for (int i = 1; i < pathB.size() - 1; i++)
		{
			if (!crossed.contains(pathB.get(i)))
				crossed.add(pathB.get(i));
		}

		crossed.add(pathA.get(pathA.size() - 1));

		return crossed;
	}

	private void generateFitness()
	{
		for (int i = 0; i < populationSize; i++)
		{
			Route route = routeFinder.findMultiRoute(population.get(i));
			fitness[i] = route.cost;

			if (fitness[i] < optimizedRoute.cost)
			{
				optimizedRoute.cost = fitness[i];
				optimizedRoute.cities = population.get(i);

				listener.onRouteFound(routeFinder.findMultiRoute(optimizedRoute.cities));
			}

			fitness[i] = (float) (1 / (Math.pow(route.cost, 8) + 1));
		}

		float sum = 0;
		for (int i = 0; i < populationSize; i++)
			sum += fitness[i];
		for (int i = 0; i < populationSize; i++)
			fitness[i] = fitness[i] / sum;
	}

	private ArrayList<City> pickOneFromPopulation()
	{
		int index = 0;
		float r = (float) Math.random();
		while (r > 0)
		{
			r = r - fitness[index];
			index++;
		}
		index--;
		return (ArrayList<City>) population.get(index).clone();
	}

	private void generatePopulation()
	{
		ArrayList<ArrayList<City>> newPopulation = new ArrayList<>();

		for (int i = 0; i < populationSize; i++)
		{
			ArrayList<City> pathA = pickOneFromPopulation();
			ArrayList<City> pathB = pickOneFromPopulation();
			ArrayList<City> newPath = crossOver(pathA, pathB);
			newPath = mutate(newPath);
			newPopulation.add(newPath);
		}

		population = newPopulation;
	}

	void optimize()
	{
		generateFitness();
		generatePopulation();

		/*System.out.println("route found:");
		for (City x : optimizedRoute.cities)
		{
			System.out.print(x.getPlateNum() + " ");
		}
		System.out.println();*/
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
