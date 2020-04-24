/**
 * A* algoritmasındaki bir düğümü temsil eden sınıf.
 */
public class PathCity implements Comparable<PathCity>
{
	private final City current;
	private City previous;
	private float pathScore;
	private float estimatedScore;

	public PathCity(City current)
	{
		this(current, null, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
	}

	public PathCity(City current, City previous, float pathScore, float estimatedScore)
	{
		this.current = current;
		this.previous = previous;
		this.pathScore = pathScore;
		this.estimatedScore = estimatedScore;
	}

	@Override
	public int compareTo(PathCity other)
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

	public float getPathScore()
	{
		return pathScore;
	}

	public void setPathScore(float pathScore)
	{
		this.pathScore = pathScore;
	}

	public float getEstimatedScore()
	{
		return estimatedScore;
	}

	public void setEstimatedScore(float estimatedScore)
	{
		this.estimatedScore = estimatedScore;
	}
}
