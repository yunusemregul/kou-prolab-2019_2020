import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

public class RouteFinder
{
	private final City[] cities;

	public RouteFinder(City[] cities)
	{
		this.cities = cities;
	}

	public Route findRoute(City from, City to)
	{
		Queue<RouteCity> openSet = new PriorityQueue<>();
		Map<City, RouteCity> allNodes = new HashMap<>();

		RouteCity start = new RouteCity(from, null, 0d, HaversineScorer.computeCost(from, to));
		openSet.add(start);
		allNodes.put(from, start);

		while (!openSet.isEmpty())
		{
			RouteCity next = openSet.poll();
			if (next.getCurrent().equals(to))
			{
				Route route = new Route();
				RouteCity current = next;
				route.cost = current.getRouteScore();
				do
				{
					route.cities.add(0, current.getCurrent());
					current = allNodes.get(current.getPrevious());
				} while (current != null);
				return route;
			}

			for (int plateNum : next.getCurrent().getConnected())
			{
				City connection = cities[plateNum - 1];
				RouteCity nextNode = allNodes.getOrDefault(connection, new RouteCity(connection));
				allNodes.put(connection, nextNode);

				double newScore = next.getRouteScore() + HaversineScorer.computeCost(next.getCurrent(), connection);
				if (newScore < nextNode.getRouteScore())
				{
					nextNode.setPrevious(next.getCurrent());
					nextNode.setRouteScore(newScore);
					nextNode.setEstimatedScore(newScore + HaversineScorer.computeCost(connection, to));
					openSet.add(nextNode);
				}
			}
		}

		throw new IllegalStateException("No route found");
	}

	/*public Route findMultiRoute(int... ids)
	{

	}*/
}
