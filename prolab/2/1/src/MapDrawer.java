import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
	private Map<Integer, Integer> markedPath = new HashMap<>();

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

	public void drawCities(Graphics2D g2d)
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

				if (markedPath.containsKey(city.getPlateNum()) && markedPath.get(city.getPlateNum()) == plate)
					g2d.setColor(Color.red);
				if (markedPath.containsKey(plate) && markedPath.get(plate) == city.getPlateNum())
					g2d.setColor(Color.red);

				g2d.drawLine(cityPos.x, cityPos.y, tCityPos.x, tCityPos.y);
			}
		}

		g2d.drawString("HERE", mouse.x, mouse.y); // debug
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
				if (markedPath.containsKey(city.getPlateNum()) && markedPath.get(city.getPlateNum()) == plate)
					g2d.setColor(Color.red);
				if (markedPath.containsKey(plate) && markedPath.get(plate) == city.getPlateNum())
					g2d.setColor(Color.red);
			}

			g2d.fill(circle);

			g2d.setColor(Color.white);
			Font font = new Font("TimesRoman", Font.PLAIN, 12);
			g2d.setFont(font);
			FontMetrics metrics = g2d.getFontMetrics(font);
			String text = String.format("%02d", city.getPlateNum());
			g2d.drawString(text, cityPos.x - metrics.stringWidth(text) / 2, cityPos.y + 4);
		}
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
					markedPath.clear();
					selectedCities.add(hoveredCity);
					System.out.println(cities[hoveredCity - 1].getName()); // debug

					if (selectedCities.size() == 2)
					{
						RouteFinder routeFinder = new RouteFinder(cities);
						Route route = routeFinder.findRoute(cities[selectedCities.get(0) - 1], cities[selectedCities.get(1) - 1]);
						System.out.println(route.cities.stream().map(City::getName).collect(Collectors.toList()));
						System.out.println(route.cost);
						for (int i = 0; i < route.cities.size() - 1; i++)
						{
							markedPath.put(route.cities.get(i).getPlateNum(), route.cities.get(i + 1).getPlateNum());
						}

						panel.repaint();
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
