/**
 * PathOptimizer sınıfı UI threadından farklı bir threadda çalıştığı için,
 * PathOptimizer sınıfından gelecek bilgileri dinlemeye aracılık eden arayüz.
 */
public interface PathOptimizerListener
{
	void onPathFound(Path path);

	void onNextGeneration();
}
