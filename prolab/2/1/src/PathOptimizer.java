import java.util.ArrayList;
import java.util.Random;

/**
 * Genetik algoritma kullanarak verilen rotayı optimize eden sınıf.
 * Kendi threadında çalışır.
 */
public class PathOptimizer implements Runnable
{
	private final PathOptimizerListener listener; // bu sınıftan gelecek cevapları dinleyen nesne
	private final PathFinder pathFinder; // A* algoritmasını uygulayacak nesne

	private final int populationSize = 300; // popülasyon nüfusu
	ArrayList<City>[] population = new ArrayList[populationSize]; // popülasyon
	private double mutationRate = .4; // mutasyon şans oranı
	private int generationNumber = 0; // kaçıncı jenerasyonda olduğumuz
	private long startTime; // optimizerin çalışmaya başladığı zaman
	private long endTime; // optimizerin durduğu zaman
	private volatile boolean running = true; // optimizerin şuanda çalışıp çalışmadığı

	final float[] fitness = new float[populationSize];
	private final Path optimizedPath;

	/**
	 * PathOptimizer sınıfı yapıcı metodu.
	 *
	 * @param cities   tüm şehirler
	 * @param path     optimize edilecek yol
	 * @param listener bu sınıftan gelecek cevapları dinleyen nesne
	 */
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

	/**
	 * Bir rota üzerinde mutationRate değişkenine bağlı olarak rastgele bir mutasyon yapan metot.
	 * <p>
	 * Örneğin rota:
	 * 2 -> 5 -> 8 ise
	 * 2 -> 8 -> 5 haline gelebilir.
	 * <p>
	 * Mutasyonlar çeşitliliğe katkı sağlar.
	 *
	 * @param toMutate mutasyona uğratılacak rota
	 * @return mutasyona uğratılmış hali
	 */
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
		mutationRate = Math.max(mutationRate - 0.00001, 0.1);
		return toMutate;
	}

	/**
	 * İki rotayı rastgele şekilde çaprazlayan metot.
	 * Bir rotadan rastgele bir aralığı alır, diğer rotadan kalan kısmı alır.
	 * Alınan kısımlarla yeni bir rota oluşturur.
	 * <p>
	 * Çaprazlama işlemi çeşitliliğe katkı sağlar.
	 *
	 * @param pathA çaprazlanacak 1. rota
	 * @param pathB çaprazlanacak 2. rota
	 * @return çaprazlanmış rota
	 */
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

	/**
	 * Popülasyondaki rotalar için genetik algoritmada bir kavram olan fitness değerini hesaplayan metot.
	 * Bu uygulama için fitness değeri o rotanın ne kadar kısa olduğuna dayanıyor.
	 * Bir rota ne kadar kısaysa fit olma oranı o kadar yüksektir.
	 */
	private void generateFitness()
	{
		float sum = 0;
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

			fitness[i] = (float) (1 / Math.pow(path.cost, 8));
			sum += fitness[i];
		}

		for (int i = 0; i < populationSize; i++)
			fitness[i] = fitness[i] / sum;
	}

	/**
	 * Popülasyondaki rotalardan en fit olanlardan bir tane seçip döndüren metot.
	 *
	 * @return fitliği yüksek olan bir rota
	 */
	private ArrayList<City> pickFit()
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

	/**
	 * Sınıfta belirlenen popülasyon nüfusu kadar rota oluşturan metot.
	 * Rotaları oluştururken crossover ve mutate metodlarını kullanarak çeşitlilik sağlar.
	 */
	private void generatePopulation()
	{
		ArrayList<City>[] newPopulation = new ArrayList[populationSize];

		for (int i = 0; i < populationSize; i++)
		{
			ArrayList<City> pathA = pickFit();
			ArrayList<City> pathB = pickFit();
			ArrayList<City> newPath = crossOver(pathA, pathB);
			mutate(newPath);
			newPopulation[i] = newPath;
		}

		population = newPopulation;
		generationNumber++;
		listener.onNextGeneration();
	}

	/**
	 * Optimize işlemini uygun sırada çalıştıran metot.
	 */
	void optimize()
	{
		generateFitness();
		generatePopulation();
	}

	/**
	 * Optimize işlemini durduran metot.
	 */
	public void stop()
	{
		this.running = false;
	}

	/**
	 * Optimizer in kaç saniyedir çalıştığını döndüren metot.
	 *
	 * @return kaç saniyedir çalışıyor
	 */
	public int secondsPastFromStart()
	{
		if (endTime > startTime)
			return (int) (endTime - startTime) / 1000;
		else
			return (int) (System.currentTimeMillis() - startTime) / 1000;
	}

	/**
	 * Bu sınıfın başka bir threaddan çalıştırılabilmesini sağlayan başlangıç metotu.
	 */
	@Override
	public void run()
	{
		endTime = System.currentTimeMillis();
		startTime = System.currentTimeMillis();
		while (running)
			optimize();
		endTime = System.currentTimeMillis();
	}

	public boolean isRunning()
	{
		return running;
	}

	public int getGenerationNumber()
	{
		return generationNumber;
	}

	public long getEndTime()
	{
		if (endTime > startTime)
			return endTime;
		else
			return System.currentTimeMillis();
	}
}
