import java.util.Collection;

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

	public Edge findInEdges(Collection<Edge> edges)
	{
		for (Edge e : edges)
		{
			if ((e.from == this.from && e.to == this.to) || (e.to == this.from && e.from == this.to))
				return e;
		}

		return null;
	}

	public boolean isEqualDirectionless(Edge other)
	{
		return ((other.from == this.from && other.to == this.to) || (other.to == this.from && other.from == this.to));
	}
}
