import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;

/**
 * Responsible of all UI
 */
public class MapDrawer
{
	private final City[] cities;
	private ArrayList<Integer> selectedIds;
	private final int scale = 75;

	public MapDrawer(City[] cities)
	{
		this.cities = cities;
	}

	/**
	 * Calculates the cities 2D position based on its latitude and longitude.
	 * 	x position = (cities longitude) - (Turkey's longitude)
	 * 	y position = (Turkey's latitude) - (cities latitude)
	 *
	 * 	y is reversed because latitudes increase from bottom to top instead top to bottom
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
			g2d.setColor(new Color(33, 33, 33));
			Point cityPos = getCity2DPos(city);

			for (int plate : city.getConnected())
			{
				if (plate < city.getPlateNum())
					continue;

				City tCity = cities[plate - 1]; // target city to draw lines to
				Point tCityPos = getCity2DPos(tCity);

				g2d.drawLine(cityPos.x, cityPos.y, tCityPos.x, tCityPos.y);
			}
		}

		// second pass = circles and text
		for (City city : cities)
		{
			g2d.setColor(Color.black);
			Point cityPos = getCity2DPos(city);

			g2d.setColor(new Color(44, 44, 44));
			int size = 20 + city.getConnected().length * 2;
			Ellipse2D.Double circle = new Ellipse2D.Double(cityPos.x - size / 2.0f, cityPos.y - size / 2.0f, size, size);
			g2d.fill(circle);

			g2d.setColor(Color.white);
			Font font = new Font("TimesRoman", Font.PLAIN, 12);
			g2d.setFont(font);
			FontMetrics metrics = g2d.getFontMetrics(font);
			String text = String.format("%02d", city.getPlateNum());
			g2d.drawString(text, cityPos.x - metrics.stringWidth(text) / 2, cityPos.y + 4);
		}
	}

	public void draw()
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

				repaint();
			}
		};
		panel.setOpaque(false);
		frame.add(panel);
		frame.setVisible(true);
	}
}
