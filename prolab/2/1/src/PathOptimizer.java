import java.util.ArrayList;
import java.util.Random;

/**
 * Genetik algoritma kullanarak verilen rotayı optimize eden sınıf.
 * Kendi threadında çalışır.
 */

public class PathOptimizer implements Runnable
{
	private final PathOptimizerListener listener;
	private final PathFinder pathFinder;

	private final int populationSize = 100;
	private final double mutationRate = .1;

	ArrayList<City>[] population = new ArrayList[populationSize];
	private int generationNumber = 0;
	private long startTime;
	private long endTime;
	private volatile boolean running = true;

	float[] fitness = new float[populationSize];
	private Path optimizedPath;

	public PathOptimizer(City[] cities, ArrayList<City> path, PathOptimizerListener listener)
	{
		ArrayList<City> pathCopy = (ArrayList<City>) path.clone();
		this.pathFinder = new PathFinder(cities);
		this.listener = listener;
		optimizedPath = pathFinder.findMultiPath(pathCopy);
		optimizedPath.cities = pathCopy;

		for (int i = 0; i < populationSize; i++)
		{
			ArrayList<City> newPath = mutate(path);
			population[i] = newPath;
		}
	}

	private ArrayList<City> mutate(ArrayList<City> toMutate)
	{
		if (Math.random() < mutationRate)
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
			Path path = pathFinder.findMultiPath(population[i]);
			fitness[i] = path.cost;

			if (fitness[i] < optimizedPath.cost)
			{
				optimizedPath.cost = fitness[i];
				optimizedPath.cities = population[i];

				listener.onPathFound(pathFinder.findMultiPath(optimizedPath.cities));
			}

			fitness[i] = (float) (1 / (Math.pow(path.cost, 8) + 1));
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
		while (r > 0 && fitness.length > index)
		{
			r = r - fitness[index];
			index++;
		}
		index--;
		return (ArrayList<City>) population[index].clone();
	}

	private void generatePopulation()
	{
		ArrayList<City>[] newPopulation = new ArrayList[populationSize];

		for (int i = 0; i < populationSize; i++)
		{
			ArrayList<City> pathA = pickOneFromPopulation();
			ArrayList<City> pathB = pickOneFromPopulation();
			ArrayList<City> newPath = crossOver(pathA, pathB);
			newPath = mutate(newPath);
			newPopulation[i] = newPath;
		}

		population = newPopulation;
		generationNumber++;
		listener.onNextGeneration();
	}

	void optimize()
	{
		generateFitness();
		generatePopulation();
	}

	public void stop()
	{
		this.running = false;
	}

	public boolean isRunning()
	{
		return running;
	}

	public int getGenerationNumber()
	{
		return generationNumber;
	}

	public int secondsPastFromStart()
	{
		if (endTime > startTime)
			return (int) (endTime - startTime) / 1000;
		else
			return (int) (System.currentTimeMillis() - startTime) / 1000;
	}

	@Override
	public void run()
	{
		endTime = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		while (running)
			optimize();
		endTime = System.currentTimeMillis();
	}
}
