import java.util.*;

public class PathFinder
{
	private final City[] cities;

	public PathFinder(City[] cities)
	{
		this.cities = cities;
	}

	public Path findRoute(City from, City to)
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
				path.cost = current.getRouteScore();
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

				float newScore = next.getRouteScore() + City.getCost(next.getCurrent(), connection);
				if (newScore < nextNode.getRouteScore())
				{
					nextNode.setPrevious(next.getCurrent());
					nextNode.setRouteScore(newScore);
					nextNode.setEstimatedScore(newScore + City.getCost(connection, to));
					openSet.add(nextNode);
				}
			}
		}

		throw new IllegalStateException("No route found");
	}


	/**
	 * Gidilecek şehir dizisi arasındaki rotayı ve costunu bulan fonksiyon.
	 *
	 * @param destinations şehir dizisi
	 * @return toplam costu belli
	 */
	public Path findMultiRoute(ArrayList<City> destinations)
	{
		Path totalPath = new Path();
		for (int i = 0; i < destinations.size() - 1; i++)
		{
			Path path = this.findRoute(destinations.get(i), destinations.get(i + 1));

			// tüm parça rotaları birleştiriyoruz
			if (i > 0)
				path.cities = new ArrayList<>(path.cities.subList(1, path.cities.size()));

			totalPath.cities.addAll(path.cities);
			totalPath.cost += path.cost;
		}

		return totalPath;
	}

	public GeneticPathOptimizer findOptimizedRoute(ArrayList<City> destinations, PathOptimizerListener listener)
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
				Path path = this.findRoute(current, x);
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

		System.out.println("initial greedy path: ");
		for (City x : visited)
		{
			System.out.print(x.getPlateNum() + " ");
		}
		System.out.println();

		listener.onRouteFound(this.findMultiRoute(visited));

		GeneticPathOptimizer pathOptimizer = new GeneticPathOptimizer(cities, visited, listener);
		Thread thread = new Thread(pathOptimizer);
		thread.start();

		return pathOptimizer;
	}
}
