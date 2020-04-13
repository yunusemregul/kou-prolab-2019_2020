import java.util.Objects;

public class Edge
{
	City from;
	City to;

	public Edge(City from, City to)
	{
		this.from = from;
		this.to = to;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || getClass() != o.getClass()) return false;
		Edge edge = (Edge) o;
		return (from.equals(edge.from) &&
				to.equals(edge.to)) || (from.equals(edge.to) &&
				to.equals(edge.from));
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(from, to);
	}
}
