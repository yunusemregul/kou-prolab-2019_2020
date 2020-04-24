import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * Tüm UI işlemlerinden sorumlu
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

	private ArrayList<Integer> selectedCities = new ArrayList<>();
	private ArrayList<Integer> mainCities = new ArrayList<>();
	private ArrayList<Edge> markedEdges = new ArrayList<>();
	private ArrayList<Path> routesSoFar = new ArrayList<>();

	private JPanel panel;
	private PathOptimizer optimizer;

	public MapDrawer(City[] cities)
	{
		this.cities = cities;
	}

	private boolean isMouseInRectangle(int rx, int ry, int rw, int rh)
	{
		return (mouse.x > rx && mouse.y > ry && mouse.x < rx + rw && mouse.y < ry + rh);
	}

	/**
	 * Calculates the cities 2D position based on its latitude and longitude.
	 * x position = (cities longitude) - (Turkey's longitude)
	 * y position = (Turkey's latitude) - (cities latitude)
	 * <p>
	 * y is reversed because latitudes increase from bottom to top instead top to bottom
	 *
	 * @param city to calculate 2D position
	 * @return 2D Point position of city
	 */
	public Point getCity2DPos(City city)
	{
		int x = (int) ((city.getLng() - 26) * mapScale) - 10; // 10 çıkartmak haritayı ortalamak için bi değer deneyerek buldum
		int y = 50 + (int) ((42 - city.getLat()) * mapScale);

		return new Point(x, y);
	}

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

				// bu yoldan geçiliyorsa kırmızı olması için
				if (new Edge(city.getPlateNum(), cities[plate - 1].getPlateNum()).findInEdges(markedEdges) != null)
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

			if (isMouseInRectangle(x, y, size, size))
			{
				g2d.setColor(Color.RED);
				g2d.drawRect(x, y, size, size); // debug
				hoveredCity = city.getPlateNum();
			}
			else
			{
				// şehirleri seçerken kırmızı olmaları için
				if (selectedCities.contains(city.getPlateNum()))
					g2d.setColor(Color.RED);
			}

			// eğer rota bu şehirden geçiyorsa kırmızı olması için
			for (int plate : city.getConnected())
			{
				if (new Edge(city.getPlateNum(), cities[plate - 1].getPlateNum()).findInEdges(markedEdges) != null)
					g2d.setColor(Color.RED);
			}

			// eğer bu şehir teslimat adresiyse mavi olması için
			if (mainCities.contains(city.getPlateNum()))
				g2d.setColor(Color.BLUE);

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

				Edge edge = new Edge(city.getPlateNum(), cities[plate - 1].getPlateNum());
				if (edge.findInEdges(markedEdges) != null)
				{
					g2d.setColor(Color.WHITE);

					String str = "";
					for (int i = 0; i < markedEdges.size(); i++)
					{
						if (markedEdges.get(i).isEqualDirectionless(edge))
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

	private void drawBottomPanels(Graphics2D g2d)
	{
		hoveredButton = null;

		g2d.setColor(new Color(44, 44, 44));
		int x = 5, y = 555;
		int w, h;

		g2d.setColor(Color.WHITE);
		Font font = new Font("", Font.PLAIN, 16);
		Font fontSmaller = new Font("", Font.PLAIN, 12);
		g2d.setFont(font);
		FontMetrics metrics = g2d.getFontMetrics(font);
		FontMetrics smetrics = g2d.getFontMetrics(fontSmaller);
		for (int i = 0; i < routesSoFar.size(); i++)
		{
			ArrayList<City> routeCities = routesSoFar.get(i).cities;
			int size = 25;
			for (int j = 0; j < routeCities.size(); j++)
			{
				City city = routeCities.get(j);

				g2d.setColor(Color.RED);

				if (mainCities.contains(city.getPlateNum()))
					g2d.setColor(Color.BLUE);

				int cx, cy;
				cx = x + j * (size + 9);
				cy = y + (i * (size + 5));

				Ellipse2D.Double circle = new Ellipse2D.Double(cx, cy, size, size);
				g2d.fill(circle);

				g2d.setColor(Color.RED);
				if (j != routeCities.size() - 1)
					g2d.drawLine(cx + size, cy + size / 2, x + (j + 1) * (size + 9), cy + size / 2);

				g2d.setColor(Color.WHITE);
				g2d.setFont(fontSmaller);
				g2d.drawString(String.format("%02d", city.getPlateNum()), cx + 4, cy + 16);
			}

			int sx = x + routeCities.size() * (size + 9), sy = y + (i * (size + 5) + 16);

			g2d.setColor(Color.RED);
			String costStr = (int) routesSoFar.get(i).cost + "km";
			g2d.drawRect(sx - 4, sy - 14, smetrics.stringWidth(costStr) + 8, 20);

			g2d.setColor(Color.WHITE);
			g2d.drawString(costStr, sx, sy);
		}

		g2d.setFont(font);

		w = 200;
		h = 40;
		x = width - w - 5;
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

		if (optimizer != null && optimizer.isRunning())
		{
			y += 56;
			g2d.drawString("Geçen süre: " + optimizer.secondsPastFromStart() + "s", x, y);
			y += 24;
			g2d.drawString("Rota geliştiriliyor..", x, y);
			y += 24;
			g2d.drawString("Jenerasyon: " + optimizer.getGenerationNumber(), x, y);
		}
	}

	private void drawRoute(Path path)
	{
		markedEdges.clear();

		for (int i2 = 0; i2 < path.cities.size() - 1; i2++)
			markedEdges.add(new Edge(path.cities.get(i2).getPlateNum(), path.cities.get(i2 + 1).getPlateNum()));

		panel.repaint();
		selectedCities.clear();
	}

	public void init()
	{
		JFrame frame = new JFrame();
		frame.setTitle("Prolab 2 - 1");
		frame.setSize(width, height);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setBackground(new Color(66, 66, 66));
		// genetic optimizer threadını ui threadı kapandığında kapat
		frame.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent windowEvent)
			{
				super.windowClosing(windowEvent);
				if (optimizer != null && optimizer.isRunning())
					optimizer.stop();
			}
		});

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
					routesSoFar.clear();
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

						optimizer = pathFinder.findOptimizedRoute(all, new PathOptimizerListener()
						{
							@Override
							public void onRouteFound(Path path)
							{
								if (!routesSoFar.contains(path))
									routesSoFar.add(0, path);

								if (optimizer != null)
								{
									if (optimizer.isRunning())
										drawRoute(path);
								}
								else
									drawRoute(path);
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
			}
		});
		panel.setOpaque(false);
		frame.add(panel);
		frame.setVisible(true);
	}
}
