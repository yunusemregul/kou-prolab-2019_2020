import java.util.HashSet;
import java.util.Objects;

public class Edge
{
	int from; // başlangıç şehrin plakası
	int to; // hedef şehrin plakası
	int cost = -1;

	public Edge(int from, int to)
	{
		this.from = from;
		this.to = to;
	}

	public Edge(int from, int to, int cost)
	{
		this(from, to);
		this.cost = cost;
	}

	public Edge findInEdges(HashSet<Edge> edges)
	{
		if (edges.contains(this))
		{
			for (Edge e : edges)
			{
				if (e.equals(this))
					return e;
			}
		}

		return null;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || getClass() != o.getClass()) return false;
		Edge edge = (Edge) o;
		return ((from == edge.from && to == edge.to) || (from == edge.to && to == edge.from));
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(from, to);
	}
}
