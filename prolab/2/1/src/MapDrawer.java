import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Tüm UI işlemlerinden sorumlu sınıf.
 */
public class MapDrawer
{
	private final City[] cities;

	private final int mapScale = 75;
	private final int width = 1366;
	private final int height = 768;

	private Point mouse = new Point(0, 0);
	private String hoveredButton;
	private Integer hoveredCity;
	private Integer hoveredShowPath;

	private ArrayList<Integer> selectedCities = new ArrayList<>();
	private ArrayList<Integer> mainCities = new ArrayList<>();
	private ArrayList<HashSet<Integer>> markedEdges = new ArrayList<>();
	private ArrayList<Path> pathsSoFar = new ArrayList<>();
	private int drawnPathIndex = 0;
	private long lastPathFoundTime;

	private JPanel panel;
	private PathOptimizer optimizer;

	/**
	 * MapDrawer sınıfı yapıcı metodu.
	 *
	 * @param cities tüm şehirler
	 */
	public MapDrawer(City[] cities)
	{
		this.cities = cities;

		JFrame frame = new JFrame();
		frame.setTitle("Prolab 2 - 1");
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setBackground(new Color(66, 66, 66));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panel = new JPanel()
		{
			@Override
			protected void paintComponent(Graphics g)
			{
				super.paintComponent(g);

				Graphics2D g2d = (Graphics2D) g;
				g2d.setStroke(new BasicStroke(2));
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				drawCities(g2d);
				drawBottomPanels(g2d);
			}
		};
		panel.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent mouseEvent)
			{
				panel.repaint();
				mouse = mouseEvent.getPoint();
			}
		});
		panel.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent mouseEvent)
			{
				// eğer kullanıcı bir şehire tıkladıysa
				if (hoveredCity != null)
				{
					pathsSoFar.clear();
					markedEdges.clear();
					mainCities.clear();
					selectedCities.add(hoveredCity);

					if (optimizer != null && optimizer.isRunning())
						optimizer.stop();
				}

				// eğer kullanıcı bir butona tıkladıysa
				if (hoveredButton != null)
				{
					if (hoveredButton.equals("ROTA BUL") && selectedCities.size() > 0)
					{
						optimizer = null;

						PathFinder pathFinder = new PathFinder(cities);
						ArrayList<City> all = new ArrayList<>();

						for (Integer plate : selectedCities)
							all.add(cities[plate - 1]);

						mainCities = (ArrayList<Integer>) selectedCities.clone();

						optimizer = pathFinder.findOptimizedPath(all, new PathOptimizerListener()
						{
							@Override
							public void onPathFound(Path path)
							{
								path.findTime = optimizer != null ? optimizer.secondsPastFromStart() : 0;
								if (!pathsSoFar.contains(path))
								{
									pathsSoFar.add(0, path);
									lastPathFoundTime = System.currentTimeMillis();
								}

								if (optimizer != null)
								{
									if (optimizer.isRunning())
										drawPath(path);
								}
								else
									drawPath(path);
							}

							@Override
							public void onNextGeneration()
							{
								panel.repaint();
							}
						});
					}

					if (hoveredButton.equals("DUR") && optimizer != null && optimizer.isRunning())
						optimizer.stop();
				}

				if (hoveredShowPath != null && hoveredShowPath < pathsSoFar.size())
					drawPath(pathsSoFar.get(hoveredShowPath));

				panel.repaint();
			}
		});
		panel.setOpaque(false);
		frame.add(panel);
		frame.setVisible(true);
	}

	/**
	 * Mousenin ekrandaki bir alanın içinde olup olmadığını döndüren metot.
	 *
	 * @param x alanın x konumu
	 * @param y alanın y konumu
	 * @param w alanın genişliği
	 * @param h alanın uzunluğu
	 * @return içindeyse true değilse false
	 */
	private boolean isMouseInRectangle(int x, int y, int w, int h)
	{
		return (mouse.x > x && mouse.y > y && mouse.x < x + w && mouse.y < y + h);
	}

	/**
	 * Bir şehrin enlem boylamlarına göre ekrandaki konumunu döndüren metot.
	 * x pozisyonu = (şehrin boylamı) - (Türkiye'nin boylamı)
	 * y pozisyonu = (Türkiye'nin enlemi) - (şehrin enlemi)
	 * <p>
	 * y değeri x e göre ters çünkü enlemler aşağıdan yukarı doğru artıyor
	 *
	 * @param city ekrandaki konumu hesaplanacak şehir
	 * @return şehrin ekrandaki konumu
	 */
	public Point getCity2DPos(City city)
	{
		int x = (int) ((city.getLng() - 26) * mapScale) - 10; // 10 çıkartmak haritayı ortalamak için bi değer deneyerek buldum
		int y = 50 + (int) ((42 - city.getLat()) * mapScale);

		return new Point(x, y);
	}

	/**
	 * Haritayı çizen metot.
	 *
	 * @param g2d çizim yapılacak graphics2d nesnesi
	 */
	private void drawCities(Graphics2D g2d)
	{
		// ilk geçişte şehirler arası çizgileri çiziyoruz
		for (City city : cities)
		{
			Point cityPos = getCity2DPos(city);

			for (int plate : city.getConnected())
			{
				g2d.setColor(new Color(33, 33, 33));
				if (plate < city.getPlateNum())
					continue;

				City tCity = cities[plate - 1]; // target city to draw lines to
				Point tCityPos = getCity2DPos(tCity);

				HashSet<Integer> set = new HashSet<>();
				set.add(city.getPlateNum());
				set.add(cities[plate - 1].getPlateNum());
				// bu yoldan geçiliyorsa kırmızı olması için
				if (markedEdges.contains(set))
					g2d.setColor(Color.RED);

				g2d.drawLine(cityPos.x, cityPos.y, tCityPos.x, tCityPos.y);
			}
		}

		hoveredCity = null;
		// ikinci geçişte şehirlerin yuvarlaklarını ve plakalarını çiziyoruz
		for (City city : cities)
		{
			Point cityPos = getCity2DPos(city);

			g2d.setColor(new Color(44, 44, 44));
			int size = 20 + city.getConnected().length * 2;
			int x = cityPos.x - size / 2,
					y = cityPos.y - size / 2;
			Ellipse2D.Double circle = new Ellipse2D.Double(x, y, size, size);

			if (isMouseInRectangle(x, y, size, size) && city.getPlateNum() != 41 && !selectedCities.contains(city.getPlateNum()))
			{
				g2d.setColor(Color.RED);
				hoveredCity = city.getPlateNum();
			}
			else
			{
				// şehirleri seçerken kırmızı olmaları için
				if (selectedCities.contains(city.getPlateNum()))
					g2d.setColor(Color.BLUE);
			}

			// eğer rota bu şehirden geçiyorsa kırmızı olması için
			for (int plate : city.getConnected())
			{
				HashSet<Integer> set = new HashSet<>();
				set.add(city.getPlateNum());
				set.add(cities[plate - 1].getPlateNum());

				if (markedEdges.contains(set))
					g2d.setColor(Color.RED);
			}

			// eğer bu şehir teslimat adresiyse mavi olması için
			if (mainCities.contains(city.getPlateNum()))
				g2d.setColor(Color.BLUE);

			if (city.getPlateNum() == 41)
				g2d.setColor(new Color(21, 173, 72));

			g2d.fill(circle);

			g2d.setColor(Color.WHITE);
			Font font = new Font("", Font.PLAIN, 12);
			g2d.setFont(font);
			FontMetrics metrics = g2d.getFontMetrics(font);
			String text = String.format("%02d", city.getPlateNum());
			g2d.drawString(text, cityPos.x - metrics.stringWidth(text) / 2, cityPos.y + 4);
		}

		// üçüncü geçişte rota bulunduğunda kenarların üzerindeki adım sayısını yazıyoruz
		for (City city : cities)
		{
			Point cityPos = getCity2DPos(city);

			for (int plate : city.getConnected())
			{
				City tCity = cities[plate - 1]; // target city to draw lines to
				Point tCityPos = getCity2DPos(tCity);

				HashSet<Integer> set = new HashSet<>();
				set.add(city.getPlateNum());
				set.add(cities[plate - 1].getPlateNum());

				if (markedEdges.contains(set))
				{
					g2d.setColor(Color.WHITE);

					String str = "";
					for (int i = 0; i < markedEdges.size(); i++)
					{
						if (markedEdges.get(i) != null && markedEdges.get(i).equals(set))
						{
							if (str.isEmpty())
								str += (i + 1);
							else
								str += "|" + (i + 1);
						}
					}
					g2d.drawString(str, (cityPos.x + tCityPos.x) / 2, (cityPos.y + tCityPos.y) / 2);
				}
			}
		}
	}

	/**
	 * Haritanın altındaki tüm rotaları ve diğer butonları çizen metot.
	 *
	 * @param g2d çizim yapılacak graphics2d nesnesi
	 */
	private void drawBottomPanels(Graphics2D g2d)
	{
		hoveredButton = null;
		hoveredShowPath = null;

		g2d.setColor(new Color(44, 44, 44));
		int x = 5, y = 555;
		int w, h;

		g2d.setColor(Color.WHITE);
		Font font = new Font("", Font.BOLD, 16);
		Font fontSmaller = new Font("", Font.PLAIN, 12);
		g2d.setFont(font);
		FontMetrics metrics = g2d.getFontMetrics(font);
		FontMetrics smetrics = g2d.getFontMetrics(fontSmaller);
		for (int i = 0; i < Math.min(pathsSoFar.size(), 6); i++)
		{
			ArrayList<City> pathCities = pathsSoFar.get(i).cities;
			int size = 25;
			for (int j = 0; j < pathCities.size(); j++)
			{
				City city = pathCities.get(j);

				g2d.setColor(Color.RED);

				if (mainCities.contains(city.getPlateNum()))
					g2d.setColor(Color.BLUE);

				int cx, cy;
				cx = x + j * (size + 9);
				cy = y + (i * (size + 5));

				Ellipse2D.Double circle = new Ellipse2D.Double(cx, cy, size, size);

				if (city.getPlateNum() == 41)
					g2d.setColor(new Color(21, 173, 72));

				g2d.fill(circle);

				g2d.setColor(Color.RED);
				if (j != pathCities.size() - 1)
					g2d.drawLine(cx + size, cy + size / 2, x + (j + 1) * (size + 9), cy + size / 2);

				g2d.setColor(Color.WHITE);
				g2d.setFont(fontSmaller);
				g2d.drawString(String.format("%02d", city.getPlateNum()), cx + 4, cy + 16);
			}

			int sx = x + pathCities.size() * (size + 9), sy = y + (i * (size + 5) + 16);

			g2d.setColor(Color.RED);
			String costStr = (int) pathsSoFar.get(i).cost + "km";
			g2d.drawRect(sx - 4, sy - 14, smetrics.stringWidth(costStr) + 8, 20);

			g2d.setColor(Color.WHITE);
			g2d.drawString(costStr, sx, sy);

			sx += smetrics.stringWidth(costStr) + 12;

			g2d.setColor(Color.RED);
			String timeStr = pathsSoFar.get(i).findTime + "sn";
			g2d.drawRect(sx - 4, sy - 14, smetrics.stringWidth(timeStr) + 8, 20);

			g2d.setColor(Color.WHITE);
			g2d.drawString(timeStr, sx, sy);

			g2d.setColor(new Color(44, 44, 44));
			String showStr = "☐ GÖSTER";
			if (i == drawnPathIndex)
			{
				showStr = "☑ GÖSTERİLİYOR";
				g2d.setColor(Color.RED);
			}
			sx = sx + smetrics.stringWidth(timeStr) + 8;
			if (isMouseInRectangle(sx, sy - 14, smetrics.stringWidth(showStr) + 8, 20) && showStr.equals("☐ GÖSTER"))
			{
				showStr = "☑ GÖSTER";
				g2d.setColor(Color.RED);
				hoveredShowPath = i;
			}
			g2d.fillRect(sx, sy - 14, smetrics.stringWidth(showStr) + 8, 20);
			g2d.setColor(Color.WHITE);
			g2d.drawString(showStr, sx + 4, sy);
		}

		g2d.setFont(font);

		w = 200;
		h = 40;
		x = width - w - 15;
		y = 440;
		String text = "ROTA BUL";

		if (optimizer != null && optimizer.isRunning())
			text = "DUR";

		g2d.setColor(new Color(44, 44, 44));
		if (isMouseInRectangle(x, y, w, h))
		{
			hoveredButton = text;
			g2d.setColor(Color.RED);
		}
		g2d.fillRect(x, y, w, h);

		g2d.setColor(Color.WHITE);
		g2d.drawString(text, x + w / 2 - metrics.stringWidth(text) / 2, y + metrics.getHeight() + 7);

		g2d.setFont(fontSmaller);
		if (optimizer != null && !markedEdges.isEmpty())
		{
			y += 57;
			int yGap = smetrics.getHeight() + 4;
			g2d.drawString("Geçen süre: " + optimizer.secondsPastFromStart() + "sn", x, y);
			y += yGap;
			if (optimizer.isRunning())
				g2d.drawString("Rota geliştiriliyor..", x, y);
			else
				g2d.drawString("Geliştirici durduruldu.", x, y);
			y += yGap;
			if ((int) (optimizer.getEndTime() - lastPathFoundTime) / 1000 > 8)
			{
				y += yGap / 4;
				g2d.drawString("(Bulunan rota en iyisi", x, y);
				y += yGap;
				g2d.drawString("olabilir)", x, y);
				y += yGap;
				y += yGap / 4;
			}
			g2d.drawString("Jenerasyon: " + optimizer.getGenerationNumber(), x, y);
		}
	}

	/**
	 * Haritadaki bir yolu çizmeyi sağlayan metot. Rotalar bulunduğunda kullanılıyor.
	 *
	 * @param path çizilecek yol
	 */
	private void drawPath(Path path)
	{
		markedEdges.clear();

		for (int i2 = 0; i2 < path.cities.size() - 1; i2++)
		{
			HashSet<Integer> set = new HashSet<>();
			set.add(path.cities.get(i2).getPlateNum());
			set.add(path.cities.get(i2 + 1).getPlateNum());

			markedEdges.add(set);
		}
		drawnPathIndex = pathsSoFar.indexOf(path);
		panel.repaint();
		selectedCities.clear();
	}
}
