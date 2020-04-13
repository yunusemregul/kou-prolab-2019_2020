import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * Responsible of all UI
 */
public class MapDrawer
{
	private final City[] cities;
	private final int scale = 75;

	private Point mouse = new Point(0, 0);
	private Integer hoveredCity;
	private ArrayList<Integer> selectedCities = new ArrayList<>();
	private ArrayList<Integer> mainCities = new ArrayList<>();
	private ArrayList<Edge> markedEdges = new ArrayList<>();

	public MapDrawer(City[] cities)
	{
		this.cities = cities;
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
		int x = (int) ((city.getLng() - 26) * scale) - 10; // minus 10 to center the map on UI
		int y = 50 + (int) ((42 - city.getLat()) * scale);

		return new Point(x, y);
	}

	private void drawCities(Graphics2D g2d)
	{
		// first pass = lines
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

				if (markedEdges.contains(new Edge(city, cities[plate - 1])))
					g2d.setColor(Color.red);

				g2d.drawLine(cityPos.x, cityPos.y, tCityPos.x, tCityPos.y);
			}
		}

		hoveredCity = null;
		// second pass = circles and text
		for (City city : cities)
		{
			Point cityPos = getCity2DPos(city);

			g2d.setColor(new Color(44, 44, 44));
			int size = 20 + city.getConnected().length * 2;
			double x = cityPos.x - size / 2.0d,
					y = cityPos.y - size / 2.0d;
			Ellipse2D.Double circle = new Ellipse2D.Double(x, y, size, size);

			if (mouse.x > x && mouse.y > y && mouse.x < x + size && mouse.y < y + size)
			{
				g2d.setColor(Color.red);
				g2d.drawRect((int) x, (int) y, size, size); // debug
				hoveredCity = city.getPlateNum();
			}
			else
			{
				if (selectedCities.contains(city.getPlateNum()))
					g2d.setColor(Color.red);
			}

			for (int plate : city.getConnected())
			{
				if (markedEdges.contains(new Edge(city, cities[plate - 1])))
					g2d.setColor(Color.red);
			}

			if (mainCities.contains(city.getPlateNum()))
				g2d.setColor(Color.BLUE);

			g2d.fill(circle);

			g2d.setColor(Color.white);
			Font font = new Font("", Font.PLAIN, 12);
			g2d.setFont(font);
			FontMetrics metrics = g2d.getFontMetrics(font);
			String text = String.format("%02d", city.getPlateNum());
			g2d.drawString(text, cityPos.x - metrics.stringWidth(text) / 2, cityPos.y + 4);
		}
	}

	private void drawBottomPanels(Graphics2D g2d)
	{
		g2d.setColor(new Color(44, 44, 44));
		int x = 5, y = 555;
		int w = 450, h = 768 - 27 - 555 - 5;
		g2d.fillRect(x, y, w, h);

		g2d.setColor(Color.white);
		Font font = new Font("", Font.PLAIN, 16);
		g2d.setFont(font);
		FontMetrics metrics = g2d.getFontMetrics(font);
		g2d.drawString("Teslimat adresleri: ", x + 5, y + metrics.getHeight() + 5);
	}

	public void init()
	{
		JFrame frame = new JFrame();
		frame.setTitle("Hello");
		frame.setSize(1366, 768);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setBackground(new Color(66, 66, 66));

		JPanel panel = new JPanel()
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

				//repaint();
			}
		};
		panel.addMouseMotionListener(new MouseMotionListener()
		{
			@Override
			public void mouseDragged(MouseEvent mouseEvent)
			{

			}

			@Override
			public void mouseMoved(MouseEvent mouseEvent)
			{
				panel.repaint();
				mouse = mouseEvent.getPoint();
			}
		});
		panel.addMouseListener(new MouseListener()
		{
			@Override
			public void mouseClicked(MouseEvent mouseEvent)
			{
			}

			@Override
			public void mousePressed(MouseEvent mouseEvent)
			{
				if (hoveredCity != null)
				{
					markedEdges.clear();
					selectedCities.add(hoveredCity);

					if (selectedCities.size() == 10)
					{
						ArrayList<City> all = new ArrayList<>();
						all.add(cities[40]);

						for (Integer plate : selectedCities)
							all.add(cities[plate - 1]);

						ArrayList<City> visited = new ArrayList<>();
						City current = all.get(0);

						while (all.size() > 0)
						{
							all.remove(current);

							double closestCost = Double.POSITIVE_INFINITY;
							City closest = null;
							for (City x : all)
							{
								double cost = HaversineScorer.computeCost(current, x);
								if (cost < closestCost)
								{
									closestCost = cost;
									closest = x;
								}
							}
							visited.add(current);
							current = closest;
						}

						visited.add(cities[40]);

						for (City city :
								visited)
						{
							System.out.print(city.getPlateNum() + " ");
						}
						System.out.println("");

						int astaradet = 0;
						RouteFinder routeFinder = new RouteFinder(cities);
						for (int i = 0; i < visited.size() - 1; i++)
						{
							astaradet++;
							Route route = routeFinder.findRoute(visited.get(i), visited.get(i + 1));
							for (int i2 = 0; i2 < route.cities.size() - 1; i2++)
							{
								markedEdges.add(new Edge(route.cities.get(i2), route.cities.get(i2 + 1)));
							}
						}
						System.out.println(astaradet);

						panel.repaint();
						mainCities = (ArrayList<Integer>) selectedCities.clone();
						selectedCities.clear();
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent mouseEvent)
			{
			}

			@Override
			public void mouseEntered(MouseEvent mouseEvent)
			{
			}

			@Override
			public void mouseExited(MouseEvent mouseEvent)
			{
			}
		});
		panel.setOpaque(false);
		frame.add(panel);
		frame.setVisible(true);
	}
}
