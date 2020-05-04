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

	private Point dragStartingPoint = new Point(0, 0);
	private Point bottomPanelDrawOffset = new Point(0, 0);

	private ArrayList<Integer> mainCities = new ArrayList<>();
	private final ArrayList<Integer> selectedCities = new ArrayList<>();
	private final ArrayList<HashSet<Integer>> markedEdges = new ArrayList<>();
	private final ArrayList<Path> pathsSoFar = new ArrayList<>();

	private int selectedCitiesSizeSave;
	private int biggestSizedPathsSize = 0;
	private int drawnPathIndex = 0;
	private long lastPathFoundTime;

	private final JPanel panel;
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
		frame.setResizable(false);
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
				mouse = mouseEvent.getPoint();
				panel.repaint();
			}

			@Override
			public void mouseDragged(MouseEvent mouseEvent)
			{
				if (isPointInRectangle(dragStartingPoint, 0, 545 - 10, width, height - 545))
				{
					int xDiff = mouseEvent.getPoint().x - mouse.x;
					int yDiff = mouseEvent.getPoint().y - mouse.y;
					if (-(bottomPanelDrawOffset.x + xDiff) + 350 < biggestSizedPathsSize * 20)
					{
						bottomPanelDrawOffset.x += xDiff;
						bottomPanelDrawOffset.x = Math.min(bottomPanelDrawOffset.x, 0);
					}
					if (-(bottomPanelDrawOffset.y + yDiff) + 180 < pathsSoFar.size() * 30)
					{
						bottomPanelDrawOffset.y += yDiff;
						bottomPanelDrawOffset.y = Math.min(bottomPanelDrawOffset.y, 0);
					}
					mouse = mouseEvent.getPoint();
					panel.repaint();
				}
			}
		});
		panel.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent mouseEvent)
			{
				dragStartingPoint = mouseEvent.getPoint();
				// eğer kullanıcı bir şehire tıkladıysa
				if (hoveredCity != null)
				{
					pathsSoFar.clear();
					markedEdges.clear();
					mainCities.clear();
					selectedCities.add(hoveredCity);
					selectedCitiesSizeSave = selectedCities.size();

					if (optimizer != null && optimizer.isRunning())
						optimizer.stop();
				}

				// eğer kullanıcı bir butona tıkladıysa
				if (hoveredButton != null)
				{
					if (hoveredButton.equals("ROTA BUL") && selectedCities.size() > 0)
					{
						bottomPanelDrawOffset = new Point(0, 0);
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
									bottomPanelDrawOffset = new Point(0, 0);
									if (path.cities.size() > biggestSizedPathsSize)
										biggestSizedPathsSize = path.cities.size();
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
	 * Bir noktanın ekrandaki bir alanın içinde olup olmadığını döndüren metot.
	 *
	 * @param point nokta
	 * @param x     alanın x konumu
	 * @param y     alanın y konumu
	 * @param w     alanın genişliği
	 * @param h     alanın uzunluğu
	 * @return içindeyse true değilse false
	 */
	private boolean isPointInRectangle(Point point, int x, int y, int w, int h)
	{
		return (point.x > x && point.y > y && mouse.x < x + w && mouse.y < y + h);
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
		Font font = new Font("", Font.PLAIN, 12);
		FontMetrics metrics = g2d.getFontMetrics(font);
		// ikinci geçişte şehirlerin yuvarlaklarını ve plakalarını çiziyoruz
		for (City city : cities)
		{
			Point cityPos = getCity2DPos(city);

			g2d.setColor(new Color(44, 44, 44));
			int size = 20 + city.getConnected().length * 2;
			int x = cityPos.x - size / 2,
					y = cityPos.y - size / 2;
			Ellipse2D.Double circle = new Ellipse2D.Double(x, y, size, size);

			if (isPointInRectangle(mouse, x, y, size, size) && city.getPlateNum() != 41 && !selectedCities.contains(city.getPlateNum()))
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
			g2d.setFont(font);
			String text = String.format("%02d", city.getPlateNum());
			g2d.drawString(text, cityPos.x - metrics.stringWidth(text) / 2, cityPos.y + 4);
		}

		for (City city : cities)
		{
			if (hoveredCity != null && hoveredCity == city.getPlateNum())
			{
				int w = metrics.stringWidth(city.getName());
				int size = 20 + city.getConnected().length * 2;

				Point point = getCity2DPos(city);
				point.y += size / 2 + 4 + (Math.max(0, mouse.y - point.y));
				point.x = mouse.x - w / 2;

				g2d.setColor(Color.RED);
				if (mainCities.contains(city.getPlateNum()))
					g2d.setColor(Color.BLUE);

				g2d.fillRect(point.x, point.y, w, metrics.getHeight());
				g2d.setFont(font);
				g2d.setColor(Color.WHITE);
				g2d.drawString(city.getName(), point.x, point.y + metrics.getHeight() - 4);
			}
		}

		font = new Font("", Font.PLAIN, 11);
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

					StringBuilder str = new StringBuilder();
					for (int i = 0; i < markedEdges.size(); i++)
					{
						if (markedEdges.get(i) != null && markedEdges.get(i).equals(set))
						{
							if (str.length() == 0)
								str.append(i + 1);
							else
								str.append("|").append(i + 1);
						}
					}
					g2d.setFont(font);
					g2d.drawString(str.toString(), (cityPos.x + tCityPos.x) / 2, (cityPos.y + tCityPos.y) / 2);
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
		int x = 12, y = 545;
		int w, h;

		w = 25;
		h = 185;
		g2d.setColor(new Color(55, 55, 55));
		g2d.fillRect(x, y, width - w - 210, h);
		g2d.setColor(new Color(44, 44, 44));
		g2d.fillRect(x, y, w, h);

		x += w + 5;
		y += 5;
		g2d.setColor(Color.WHITE);
		Font font = new Font("", Font.BOLD, 16);
		Font fontSmaller = new Font("", Font.PLAIN, 12);
		g2d.setFont(fontSmaller);
		FontMetrics metrics = g2d.getFontMetrics(font);
		FontMetrics smetrics = g2d.getFontMetrics(fontSmaller);
		for (int i = 0; i < pathsSoFar.size(); i++)
		{
			ArrayList<City> pathCities = pathsSoFar.get(i).cities;
			int size = 25;

			int sx = x;
			int sy = y + (i * (size + 5) + 16) + bottomPanelDrawOffset.y;

			Rectangle clip = new Rectangle(x - w - 5, y - 5, width - sx - 227, h);
			g2d.setClip(clip);

			g2d.setColor(new Color(50, 50, 50));
			if (i % 2 == 0)
				g2d.setColor(new Color(60, 60, 60));
			if (i == drawnPathIndex)
				g2d.setColor(Color.RED);
			g2d.fillRect(x - w - 5, sy - 21, w, 31);

			g2d.setColor(Color.WHITE);
			g2d.drawString((i + 1) + ".", x - w + 3, sy);

			g2d.setColor(new Color(44, 44, 44));
			String showStr = "☐ GÖSTER";
			if (i == drawnPathIndex)
			{
				showStr = "☑ GÖSTER";
				g2d.setColor(Color.RED);
			}
			if (isPointInRectangle(mouse, sx, sy - 14, smetrics.stringWidth(showStr) + 8, 20) && showStr.equals("☐ GÖSTER"))
			{
				showStr = "☑ GÖSTER";
				g2d.setColor(Color.RED);
				hoveredShowPath = i;
			}
			g2d.fillRect(sx, sy - 14, smetrics.stringWidth(showStr) + 8, 20);
			g2d.setColor(Color.WHITE);
			g2d.drawString(showStr, sx + 4, sy);

			sx = sx + smetrics.stringWidth(showStr) + 8 + 8;

			g2d.setColor(new Color(44, 44, 44));
			if (i == drawnPathIndex)
				g2d.setColor(Color.RED);

			String costStr = (int) pathsSoFar.get(i).cost + "km";
			g2d.drawRect(sx - 4, sy - 14, smetrics.stringWidth(costStr) + 8, 20);

			g2d.setColor(Color.WHITE);
			g2d.drawString(costStr, sx, sy);

			sx += smetrics.stringWidth(costStr) + 12;

			g2d.setColor(new Color(44, 44, 44));
			if (i == drawnPathIndex)
				g2d.setColor(Color.RED);

			String timeStr = pathsSoFar.get(i).findTime + "sn";
			g2d.drawRect(sx - 4, sy - 14, smetrics.stringWidth(timeStr) + 8, 20);

			g2d.setColor(Color.WHITE);
			g2d.drawString(timeStr, sx, sy);

			sx += smetrics.stringWidth(timeStr) + 10;

			clip = new Rectangle(sx, y - 5, width - sx - 227, h);
			g2d.setClip(clip);

			for (int j = 0; j < pathCities.size(); j++)
			{
				City city = pathCities.get(j);

				g2d.setColor(new Color(44, 44, 44));

				if (i == drawnPathIndex)
					g2d.setColor(Color.RED);

				if (mainCities.contains(city.getPlateNum()) && i == drawnPathIndex)
					g2d.setColor(Color.BLUE);

				int cx, cy;
				cx = sx + bottomPanelDrawOffset.x + j * (size + 9);
				cy = y + bottomPanelDrawOffset.y + (i * (size + 5));

				Ellipse2D.Double circle = new Ellipse2D.Double(cx, cy, size, size);

				if (city.getPlateNum() == 41 && i == drawnPathIndex)
					g2d.setColor(new Color(21, 173, 72));

				g2d.fill(circle);

				g2d.setColor(new Color(33, 33, 33));
				if (i == drawnPathIndex)
					g2d.setColor(Color.RED);
				if (j != pathCities.size() - 1)
					g2d.drawLine(cx + size, cy + size / 2, sx + bottomPanelDrawOffset.x + (j + 1) * (size + 9), cy + size / 2);

				g2d.setColor(Color.WHITE);
				g2d.setFont(fontSmaller);
				g2d.drawString(String.format("%02d", city.getPlateNum()), cx + 4, cy + 16);
			}

			g2d.setClip(null);
		}

		g2d.setFont(font);

		w = 200;
		h = 40;
		x = width - w - 15;
		y = 545;
		String text = "ROTA BUL";

		if (optimizer != null && optimizer.isRunning())
			text = "DUR";

		g2d.setColor(new Color(44, 44, 44));
		if (isPointInRectangle(mouse, x, y, w, h))
		{
			hoveredButton = text;
			g2d.setColor(Color.RED);
		}
		g2d.fillRect(x, y, w, h);

		g2d.setColor(Color.WHITE);
		g2d.drawString(text, x + w / 2 - metrics.stringWidth(text) / 2, y + metrics.getHeight() + 7);

		g2d.setFont(fontSmaller);

		g2d.setColor(new Color(50, 50, 50));
		g2d.fillRect(x, y + h, w, 185 - h);

		x += 6;
		g2d.setColor(Color.WHITE);

		int yGap = smetrics.getHeight() + 4;
		g2d.drawString(selectedCitiesSizeSave + " şehir seçildi.", x, y + h + metrics.getHeight());
		if (optimizer != null && !markedEdges.isEmpty())
		{
			y += h + metrics.getHeight();
			y += yGap;
			g2d.drawString("Geçen süre: " + optimizer.secondsPastFromStart() + "sn", x, y);
			y += yGap;
			g2d.drawString(pathsSoFar.size() + " rota bulundu", x, y);
			y += yGap;
			if (optimizer.isRunning())
				g2d.drawString("En iyi rota geliştiriliyor..", x, y);
			else
				g2d.drawString("Geliştirici durduruldu.", x, y);
			y += yGap;
			if ((int) (optimizer.getEndTime() - lastPathFoundTime) / 1000 > 8)
			{
				g2d.drawString("(Bulunan rota en iyisi", x, y);
				y += yGap;
				g2d.drawString("olabilir)", x, y);
				y += yGap;
			}
			g2d.drawString("Jenerasyon: " + optimizer.getGenerationNumber(), x, y);
		}
		else
		{
			g2d.setColor(Color.WHITE);
			if (selectedCities.size() < 1)
				g2d.drawString("En az 1 şehir seçilmeli.", x, y + h + metrics.getHeight() + yGap);
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
