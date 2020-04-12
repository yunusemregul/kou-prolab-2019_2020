public class RouteCity implements Comparable<RouteCity>
{
	private final City current;
	private City previous;
	private double routeScore;
	private double estimatedScore;

	public RouteCity(City current)
	{
		this(current, null, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
	}

	public RouteCity(City current, City previous, double routeScore, double estimatedScore)
	{
		this.current = current;
		this.previous = previous;
		this.routeScore = routeScore;
		this.estimatedScore = estimatedScore;
	}

	@Override
	public int compareTo(RouteCity other)
	{
		if (this.estimatedScore > other.estimatedScore)
			return 1;
		else if (this.estimatedScore < other.estimatedScore)
			return -1;
		else
			return 0;
	}

	public City getCurrent()
	{
		return current;
	}

	public City getPrevious()
	{
		return previous;
	}

	public void setPrevious(City previous)
	{
		this.previous = previous;
	}

	public double getRouteScore()
	{
		return routeScore;
	}

	public void setRouteScore(double routeScore)
	{
		this.routeScore = routeScore;
	}

	public double getEstimatedScore()
	{
		return estimatedScore;
	}

	public void setEstimatedScore(double estimatedScore)
	{
		this.estimatedScore = estimatedScore;
	}
}
