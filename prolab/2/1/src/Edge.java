import java.util.Collection;

/**
 * Şehirler arasındaki kenarları temsil eden sınıf.
 */
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
		if (edges.size() == 0)
			return null;

		for (Edge e : edges)
		{
			if (e == null)
				continue;

			if (e.from == this.from && e.to == this.to)
				return e;

			if (e.to == this.from && e.from == this.to)
				return e;
		}

		return null;
	}

	public boolean isEqualDirectionless(Edge other)
	{
		return ((other.from == this.from && other.to == this.to) || (other.to == this.from && other.from == this.to));
	}
}
