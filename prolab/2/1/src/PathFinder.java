import java.util.*;

/**
 * A* algoritması kullanarak iki şehir/bir çok şehir arasındaki en kısa mesafeyi bulan sınıf.
 */
public class PathFinder
{
	private final City[] cities;

	/**
	 * PathFinder sınıfı yapıcı metodu.
	 *
	 * @param cities tüm şehirler
	 */
	public PathFinder(City[] cities)
	{
		this.cities = cities;
	}

	/**
	 * İki şehir arasındaki en kısa yolu A* algoritması kullanarak bulan metot.
	 *
	 * @param from başlangıç şehri
	 * @param to   varış şehri
	 * @return en kısa yol
	 */
	public Path findPath(City from, City to)
	{
		Queue<PathCity> openSet = new PriorityQueue<>();
		Map<City, PathCity> allNodes = new HashMap<>();

		PathCity start = new PathCity(from, null, 0.f, City.getCost(from, to));
		openSet.add(start);
		allNodes.put(from, start);

		while (!openSet.isEmpty())
		{
			PathCity next = openSet.poll();
			if (next.getCurrent().equals(to))
			{
				Path path = new Path();
				PathCity current = next;
				path.cost = current.getPathScore();
				do
				{
					path.cities.add(0, current.getCurrent());
					current = allNodes.get(current.getPrevious());
				} while (current != null);
				return path;
			}

			for (int plateNum : next.getCurrent().getConnected())
			{
				City connection = cities[plateNum - 1];
				PathCity nextNode = allNodes.getOrDefault(connection, new PathCity(connection));
				allNodes.put(connection, nextNode);

				float newScore = next.getPathScore() + City.getCost(next.getCurrent(), connection);
				if (newScore < nextNode.getPathScore())
				{
					nextNode.setPrevious(next.getCurrent());
					nextNode.setPathScore(newScore);
					nextNode.setEstimatedScore(newScore + City.getCost(connection, to));
					openSet.add(nextNode);
				}
			}
		}

		return null;
	}


	/**
	 * Gidilecek şehir dizisi arasındaki rotayı bulan metot.
	 * Gidilecek şehirler arasında A* algoritmasını çağırmaya dayanıyor.
	 * Bu metot gidilecek şehirler arasındaki en kısa rota hangisi olduğunu umursamadan verilen sıraya göre gider.
	 *
	 * @param destinations gidilecek şehir dizisi
	 * @return bulunan rota
	 */
	public Path findMultiPath(ArrayList<City> destinations)
	{
		Path totalPath = new Path();
		for (int i = 0; i < destinations.size() - 1; i++)
		{
			Path path = this.findPath(destinations.get(i), destinations.get(i + 1));

			// tüm parça rotaları birleştiriyoruz
			if (i > 0)
				path.cities = new ArrayList<>(path.cities.subList(1, path.cities.size()));

			totalPath.cities.addAll(path.cities);
			totalPath.cost += path.cost;
		}

		return totalPath;
	}

	/**
	 * Gidilecek şehirler arasındaki en kısa, optimize edilmiş rotayı bulan metot.
	 * Optimize etme işlemini PathOptimizer sınıfı genetik optimizasyon algoritması kullanarak yapıyor.
	 * <p>
	 * Bu metot rotayı optimize etmeden önce greedy en yakın komşu algoritması kullanarak optimize edilecek
	 * yol için bir kolaylaştırma yapar. Bu greedy algoritma genelde en kısa yolu vermez ama en kısa yola
	 * yakın yollar verir. Böylece genetik algoritmanın işi bir nevi kolaylaştırılmış olur.
	 * <p>
	 * Bu metodun rotayı optimize etmek için oluşturduğu PathOptimizer sınıfı başka bir threadda çalışmalı.
	 * Çünkü optimizasyon işlemi sürekli ve maliyetli olacağı için UI threadını bloke eder ve kullanıcıya
	 * geri dönüş yapılamaz, kullanıcı uygulamayı kontrol edemez.
	 *
	 * @param destinations gidilecek şehirler
	 * @param listener     başka threadda çalışan PathOptimizer sınıfından gelecek cevapları dinleyecek nesne
	 * @return oluşturulan PathOptimizer sınıfı
	 */
	public PathOptimizer findOptimizedPath(ArrayList<City> destinations, PathOptimizerListener listener)
	{
		destinations.add(0, cities[40]);
		ArrayList<City> visited = new ArrayList<>();
		City current = destinations.get(0);

		while (destinations.size() > 0)
		{
			destinations.remove(current);

			float closestCost = Float.POSITIVE_INFINITY;
			City closest = null;
			for (City x : destinations)
			{
				Path path = this.findPath(current, x);
				if (path.cost < closestCost)
				{
					closestCost = path.cost;
					closest = x;
				}
			}
			visited.add(current);
			current = closest;
		}

		visited.add(cities[40]);

		listener.onPathFound(this.findMultiPath(visited));

		PathOptimizer pathOptimizer = new PathOptimizer(cities, visited, listener);
		Thread thread = new Thread(pathOptimizer);
		thread.start();

		return pathOptimizer;
	}
}
