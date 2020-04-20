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

	private final int mapScale = 75;
	private final int width = 1366;
	private final int height = 768;

	private Point mouse = new Point(0, 0);
	private String hoveredButton;
	private Integer hoveredCity;
	private ArrayList<Integer> selectedCities = new ArrayList<>();
	private ArrayList<Integer> mainCities = new ArrayList<>();
	private ArrayList<Edge> markedEdges = new ArrayList<>();

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
		int x = (int) ((city.getLng() - 26) * mapScale) - 10; // minus 10 to center the map on UI
		int y = 50 + (int) ((42 - city.getLat()) * mapScale);

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


				// bu yoldan geçiliyorsa kırmızı olması için
				if (markedEdges.contains(new Edge(city.getPlateNum(), cities[plate - 1].getPlateNum())))
					g2d.setColor(Color.RED);

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
			int x = cityPos.x - size / 2,
					y = cityPos.y - size / 2;
			Ellipse2D.Double circle = new Ellipse2D.Double(x, y, size, size);

			if (isMouseInRectangle(x, y, size, size))
			{
				g2d.setColor(Color.red);
				g2d.drawRect(x, y, size, size); // debug
				hoveredCity = city.getPlateNum();
			}
			else
			{
				// şehirleri seçerken kırmızı olmaları için
				if (selectedCities.contains(city.getPlateNum()))
					g2d.setColor(Color.red);
			}

			// eğer rota bu şehirden geçiyorsa kırmızı olması için
			for (int plate : city.getConnected())
			{
				if (markedEdges.contains(new Edge(city.getPlateNum(), cities[plate - 1].getPlateNum())))
					g2d.setColor(Color.red);
			}

			// eğer bu şehir teslimat adresiyse mavi olması için
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
		hoveredButton = null;

		g2d.setColor(new Color(44, 44, 44));
		int x = 5, y = 555;
		int w = 450, h = height - 27 - 555 - 5;
		g2d.fillRect(x, y, w, h);

		g2d.setColor(Color.white);
		Font font = new Font("", Font.PLAIN, 16);
		g2d.setFont(font);
		FontMetrics metrics = g2d.getFontMetrics(font);
		g2d.drawString("Teslimat adresleri: ", x + 5, y + metrics.getHeight() + 5);
		if (hoveredCity != null)
			g2d.drawString(cities[hoveredCity - 1].getName(), x + 5, y + 40);

		w = 200;
		h = 40;
		x = width - w - 5;
		String text = "ROTA BUL";

		g2d.setColor(new Color(44, 44, 44));
		if (isMouseInRectangle(x, y, w, h))
		{
			hoveredButton = text;
			g2d.setColor(Color.red);
		}
		g2d.fillRect(x, y, w, h);

		g2d.setColor(Color.white);
		g2d.drawString(text, x + w / 2 - metrics.stringWidth(text) / 2, y + metrics.getHeight() + 7);
	}

	public void init()
	{
		JFrame frame = new JFrame();
		frame.setTitle("Hello");
		frame.setSize(width, height);
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
					mainCities.clear();
					selectedCities.add(hoveredCity);
				}

				if (hoveredButton != null)
				{
					if (hoveredButton.equals("ROTA BUL") && selectedCities.size() > 0)
					{
						RouteFinder routeFinder = new RouteFinder(cities);
						ArrayList<City> all = new ArrayList<>();

						for (Integer plate : selectedCities)
							all.add(cities[plate - 1]);

						Route route = routeFinder.findMultiRoute(all);
						for (City city : route.cities)
							System.out.print(city.getPlateNum() + " ");
						System.out.println();
						for (int i2 = 0; i2 < route.cities.size() - 1; i2++)
						{
							markedEdges.add(new Edge(route.cities.get(i2).getPlateNum(), route.cities.get(i2 + 1).getPlateNum()));
						}
						System.out.println("total cost : " + route.cost);

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
