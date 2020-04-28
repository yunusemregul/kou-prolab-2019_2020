/**
 * A* algoritmasındaki bir düğümü temsil eden sınıf.
 * PriorityQueue listesine ekleyebilmek için Comparable sınıfından türüyor.
 */
public class PathCity implements Comparable<PathCity>
{
	private final City current;
	private City previous;
	private float pathScore;
	private float estimatedScore;

	/**
	 * PathCity sınıfındaki 1. yapıcı metot.
	 * A* algoritmasında yeni bir şehir keşfettiğimizde ilk olarak bu metot kullanılıyor.
	 *
	 * @param current düğümü oluşturulacak şehir
	 */
	public PathCity(City current)
	{
		this(current, null, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
	}

	/**
	 * PathCity sınıfındaki 2. yapıcı metot.
	 *
	 * @param current        düğümü oluşturulacak şehir
	 * @param previous       bu şehre hangi şehirden geldiysek o şehir
	 * @param pathScore      bu şehre geliş mesafesi
	 * @param estimatedScore bu şehirden rotanın varış şehrine olan mesafe
	 *                       bunu A* algoritması varış şehri yönünde ilerlemek için kullanıyor
	 */
	public PathCity(City current, City previous, float pathScore, float estimatedScore)
	{
		this.current = current;
		this.previous = previous;
		this.pathScore = pathScore;
		this.estimatedScore = estimatedScore;
	}

	/**
	 * Düğümleri PriorityQueue listesine ekleyebilmek için gereken metot.
	 * Bir düğümü diğerine göre karşılaştırmaya yarıyor.
	 * Düğümlerin rotanın varış şehrine olan mesafelerini karşılaştırıp
	 * ona göre bir dönüş yapıyor.
	 *
	 * @param other bu düğümün karşılaştırılacağı diğer düğüm
	 * @return diğer şehir varış şehrine daha yakınsa 1, bu şehir daha yakınsa -1, eşitlerse 0
	 */
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
