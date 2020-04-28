/**
 * PathOptimizer sınıfı UI threadından farklı bir threadda çalıştığı için,
 * PathOptimizer sınıfından gelecek bilgileri dinlemeye aracılık eden arayüz.
 */
public interface PathOptimizerListener
{
	/**
	 * Optimizer yeni bir yol bulduğunda bu metot çağrılır.
	 *
	 * @param path bulunan yol
	 */
	void onPathFound(Path path);

	/**
	 * Optimizer yeni bir jenerasyon oluşturduğunda bu metot çağrılır.
	 */
	void onNextGeneration();
}
