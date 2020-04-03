import Oyuncular.*;
import Pokemonlar.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class Masa extends JPanel
{
	/*
			oyunModu
				-1: Yok/Belirlenmedi
				0: Kullanıcı vs Bilgisayar
				1: Bilgisayar vs Bilgisayar
		 */
	private int oyunModu = -1;
	private static String[] oyunModlari = {"Kullanici vs Bilgisayar", "Bilgisayar vs Bilgisayar"};

	/*
			gameState
				0: Oyun başlamadı
				1: Oyuncular hazır
				2: Oyuncuların ellerindeki kartları oynaması, yeni kart alması bekleniyor
				3: Oyuncular kartları oynadı, kapışma
				4: Oyun bitti
		 */
	// oyun durumunu tutuyor
	private volatile int gameState;
	// oyun durumunun yazılı anlamını tutuyor
	// loglarda vs kullanmak    için
	private static String[] gameStates = {"Oyun Baslamadi", "Oyuncular Hazir", "Oyun", "Kapisma", "Oyun Bitti"};

	// masa envanterindeki kartları tutuyor
	private Pokemon[] kartListesi;
	// şuanda kapışan kartları tutuyor
	public Pokemon[] kapisanKartlar = new Pokemon[2];
	// kapışma başladığında sistemin current mili saniyesini tutacak değer
	// animasyonda kullanılıyor
	public long kapismaBaslangicTime;

	// ana framemiz
	private JFrame frame;

	// gui_oyunModu paneline erişim için burada tanımladım
	private JPanel gui_oyunModu;

	// masadaki oyuncuları tutuyor
	// 0. oyuncu = kullanıcı veya bilgisayar
	// 1. oyuncu = bilgisayar
	public Oyuncu[] oyuncular = new Oyuncu[2];
	public Oyuncu kazanan;

	// açılmamış kart imajını tutuyor
	private Image img_kart;
	private Image img_menu;
	private Image img_closedmenu;
	private Image img_game;
	// pokemonlara ait imajları tutuyor
	private Image[] img_pokemonlar;

	// çizimde gerekli imajları hazırlayan fonksiyon
	private void LoadImages() throws IOException
	{
		img_kart = ImageIO.read(getClass().getResource("kart.png"));
		img_kart = img_kart.getScaledInstance(369 / 2, 512 / 2, Image.SCALE_SMOOTH);

		img_menu = ImageIO.read(getClass().getResource("menu.png"));
		img_closedmenu = ImageIO.read(getClass().getResource("closedmenu.png"));
		img_game = ImageIO.read(getClass().getResource("game.png"));

		// kartlistesindeki pokemonların imajlarını hazırlıyoruz
		for (int i = 0; i < this.kartListesi.length; i++)
		{
			// pokemon adını lowercase yaparak resource klasöründeki png adına ulaşıyoruz
			String name = this.kartListesi[i].getPokemonAdi().toLowerCase() + ".png";
			img_pokemonlar[i] = ImageIO.read(getClass().getResource(name));
			img_pokemonlar[i] = img_pokemonlar[i].getScaledInstance(369 / 2, 512 / 2, Image.SCALE_SMOOTH);
		}
	}

	// constructor
	public Masa() throws IOException
	{
		// başlık
		frame = new JFrame("Pokemon Kart Oyunu");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// bu kısmın tümü ana pencereyi boyutlandırma ile alakalı
		Dimension windowSize = new Dimension(1150, 768);
		frame.setSize(windowSize);
		frame.setMaximumSize(windowSize);
		frame.setPreferredSize(windowSize);
		frame.setMinimumSize(windowSize);
		frame.setResizable(false);
		frame.setLayout(new BorderLayout());

		// pencerenin arkaplan rengi
		frame.getContentPane().setBackground(new Color(33, 33, 33));

		// pencereyi ekranın ortasına koyan kısım
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

		// oyun seçim paneli
		gui_oyunModu = new JPanel();
		gui_oyunModu.setOpaque(false);

		// kullanıcıya oyun modu seçimi sunan öğeler
		JLabel OM = new JLabel("Oyun modu seçiniz:");
		OM.setForeground(new Color(255, 255, 255));

		JButton KB = new JButton("Kullanıcı vs Bilgisayar");
		KB.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent actionEvent)
			{
				// kullanıcı vs bilgisayar seçimi yapıldığında oyunu
				// ilgili mod ile başlat
				JPanel IP = new JPanel();
				IP.setOpaque(false);
				IP.setLayout(null);

				JLabel IL = new JLabel("Kullanıcı ismi giriniz:");
				IL.setForeground(new Color(255, 255, 255));

				JTextField IF = new JTextField(16);
				IF.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent actionEvent)
					{
						startGame(0);
						oyuncular[0].setOyuncuAdi(IF.getText());
					}
				});

				IP.setBounds(1150 / 2 - 100, 768 / 2 - 100, 200, 200);
				IL.setBounds(0, 0, 200, 20);
				IF.setBounds(0, 24, 200, 20);
				IP.add(IL);
				IP.add(IF);
				gui_oyunModu.removeAll();
				gui_oyunModu.add(IP);
				gui_oyunModu.revalidate();
				gui_oyunModu.repaint();
			}
		});
		JButton BB = new JButton("Bilgisayar vs Bilgisayar");
		BB.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent actionEvent)
			{
				// bilg. vs bilg. seçilirse oyunu
				// ilgili mod ile başlat
				startGame(1);
			}
		});

		// oluşturulan gui öğelerini gui_oyunmodu paneline ekliyoruz
		int centerx = 1150 / 2;
		int centery = 768 / 2;
		int w = 200;
		int h = 80;
		OM.setBounds(centerx - w / 2, centery - h / 2, 200, 16);
		KB.setBounds(centerx - w / 2, centery + 24 - h / 2, 200, 24);
		BB.setBounds(centerx - w / 2, centery + 24 + 24 + 4 - h / 2, 200, 24);
		gui_oyunModu.add(OM);
		gui_oyunModu.add(KB);
		gui_oyunModu.add(BB);

		gui_oyunModu.setLayout(null);
		gui_oyunModu.revalidate();

		// gui_oyunmodu panelini ana pencereye ekliyoruz
		frame.add(gui_oyunModu);

		// penceremiz üzerinde yaptığımız ekleme işlemleri bitince görünür hale getiriyoruz
		frame.setVisible(true);

		// tıklamalar için mouse listener
		this.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				super.mouseClicked(e);
				Point mouse = new Point(e.getX(), e.getY());

				// eğer oyun modu kullanıcı vs bilgisayarsa
				// ve oyun başladıysa
				if (getOyunModu() == 0 && gameState >= 2 && gameState != 3)
				{

					int count;
					if (oyuncular[0].kartSayisi() < 3)
					{
						count = 0;

						for (Pokemon kart : kartListesi)
						{
							if (kart != null && !kart.kartKullanildiMi)
							{
								// 370/kartsayısı kısmı kart sayısı çoğaldığında kartlar arasındaki boşluğu
								// daraltmak için
								// 370 de deneyerek bulduğum özel ayar
								int x = 25;
								int y = 73 + count * (370 / (kartSayisi()));

								if (getOyunModu() == 0 && (mouse.x > x && mouse.x < x + (369 / 2) && mouse.y > y && mouse.y < y + (370 / (kartSayisi()))))
								{
									// oyuncuya masa destesinden seçtiği kartı ver
									kartVer(oyuncular[0], kart);
									// bilgisayara masa destesinden rastgele bir kart ver
									kartVer(oyuncular[1], rastgeleKart());
								}
								count++;
							}
						}
					}

					// eğer oyuncunun kart seçmesi bekleniyorsa, kart seçimine izin ver
					if (gameState == 2)
					{
						count = 0;
						for (int i = 0; i < oyuncular[0].kartListesi.length; i++)
						{
							if (oyuncular[0].kartListesi[i] != null && !oyuncular[0].kartListesi[i].kartKullanildiMi)
							{
								int x = 280 + count * 369 / 2 + 10 * count;
								int y = 468;

								if (getOyunModu() == 0 && (mouse.x > x && mouse.x < x + (369 / 2) && mouse.y > y))
								{
									// oyuncu kendi destesinden istediği kartı seçti
									kapisanKartlar[0] = oyuncular[0].kartSec(oyuncular[0].kartListesi[i]);
									// bilgisayardan rastgele bir kart seçmesini iste
									kapisanKartlar[1] = oyuncular[1].kartSec(null);

									// oyun durumunu kapışma olarak değiştir
									setGameState(3);
								}
								count++;
							}
						}
					}
				}
			}
		});

		// oyunu başlamamış olarak belirliyoruz
		this.setGameState(0);
	}

	public Masa(Pokemon[] kartListesi) throws IOException
	{
		this();

		this.kartListesi = kartListesi;
		for (int i = 0; i < this.kartListesi.length; i++)
		{
			this.kartListesi[i].setPokemonID(i); // kart kullanıcıya verildiğinde
			// masadaki indexine ulaşamayacığımız için
			// id değerine masadaki indexini kaydediyoruz
			// ki imajına ulaşabilelim
		}

		System.out.println("Masa " + this.kartListesi.length + " kart ile olusturuldu.");

		img_pokemonlar = new Image[this.kartListesi.length];
		// çizimde gerekli imajları yükle
		this.LoadImages();
	}

	/*
		x,y konumundaki kart ın üzerine hasar ve tip bilgisini yazar
		direk fotoğrafların üzerine de yazılabilirdi ama böyle daha
		değiştirilebilir dinamik olacağını düşünüyorum bu proje için ne kadar gereksiz olsa da
	 */
	private void drawKartInfo(Graphics2D g2, Pokemon kart, int x, int y)
	{
		// kartlar üzerindeki sarı şerit ve hasar puanını gösteren kısım
		// ile ilgili ayarlamalar
		int sy = 145; // şeritin kart'a relatif y konumu
		int tx = 50; // yazının kart'a relatif x konumu
		int ty = 25; // yazının şerit'e relatif y konumu
		int sh = 60; // şeritin uzunluğu

		if (kart == null)
		{
			g2.drawImage(img_kart, x, y, null);
		}
		else
		{
			g2.drawImage(img_pokemonlar[kart.getPokemonID()], x, y, null);
			g2.setColor(new Color(255, 224, 105));
			g2.fillRect(x, y + sy, 369 / 2, sh);
			g2.setColor(Color.black);
			g2.drawString("Hasar: " + kart.hasarPuaniGoster(), x + tx, y + sy + ty);
			g2.drawString("Tip: " + kart.getPokemonTip(), x + tx, y + sy + ty + 20);
		}
	}

	@Override
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g; // ekstra fonksiyonlar için
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // estetik için

		Point mouse = MouseInfo.getPointerInfo().getLocation();
		if (frame != null)
			SwingUtilities.convertPointFromScreen(mouse, frame);

		// arkaplanlar
		if (this.getGameState() < 1 || this.getGameState() == 4)
			g2.drawImage(img_menu, 0, 0, null);

		// oyun durumuna göre çizim
		if (this.getGameState() >= 1 && this.getGameState() != 4)
		{
			g2.drawImage(img_game, 0, 0, null);
			g2.setFont(new Font("TimesRoman", Font.PLAIN, 16));
			g2.setColor(Color.white);
			g2.drawString("Masa Kartları", 56, 44);

			// masaya ait kartları çiz
			Pokemon masadakiHoveredKart = null;
			int[] masadakiHoveredKartCoords = new int[2];

			int count = 0;
			for (Pokemon kart : this.kartListesi)
			{
				if (kart != null && !kart.kartKullanildiMi)
				{
					// 370/kartsayısı kısmı kart sayısı çoğaldığında kartlar arasındaki boşluğu
					// daraltmak için
					// 370 de deneyerek bulduğum özel ayar
					int x = 25;
					int y = 73 + count * (370 / (this.kartSayisi()));

					// eğer oyun modu kullanıcı vs bilgisayarsa ve kullanıcının kart sayısı 3 ten fazla değilse
					// ve oyun kapışma anında değilse
					if (this.getOyunModu() == 0 && this.oyuncular[0].kartSayisi() < 3 && this.getGameState() != 3)
					{
						// eğer kullanıcı mousesini masa destesindeki kartların birinin üzerine tutuyorsa
						if (mouse.x > x && mouse.x < x + (369 / 2) && mouse.y > y && mouse.y < y + (370 / (this.kartSayisi())))
						{
							masadakiHoveredKart = kart;
							masadakiHoveredKartCoords[0] = x;
							masadakiHoveredKartCoords[1] = y;
						}
					}
					// kartlar görünür: drawKartInfo(g2, kart, x, y);
					drawKartInfo(g2, null, x, y);

					count++;
				}
			}

			if (masadakiHoveredKart != null)
			{
				int x = masadakiHoveredKartCoords[0];
				int y = masadakiHoveredKartCoords[1];
				// üzerine tutulan karta arkaplan çiz
				g2.setColor(Color.red);
				g2.fillRect(x - 4, y - 4, 369 / 2 + 8, 512 / 2 + 8);

				// kartlar görünür: drawKartInfo(g2,masadakiHoveredKart,x,y);
				drawKartInfo(g2, null, x, y);
			}

			// Oyuncu isimlerini ve skorlarını çiz
			g2.setColor(Color.white);
			g2.drawString(this.oyuncular[0].getOyuncuAdi(), 515, 441);
			g2.drawString("Skor: " + this.oyuncular[0].getSkor(), 959, 710);
			g2.drawString(this.oyuncular[1].getOyuncuAdi(), 515, 314);
			g2.drawString("Skor: " + this.oyuncular[1].getSkor(), 959, 45);

			// 0. oyuncu kartlarını çiz (alt)
			count = 0;
			for (int i = 0; i < this.oyuncular[0].kartListesi.length; i++)
			{
				if (this.oyuncular[0].kartListesi[i] != null && !this.oyuncular[0].kartListesi[i].kartKullanildiMi)
				{
					int x = 280 + count * 369 / 2 + 10 * count;
					int y = 468;

					// eğer oyun modu kullanıcı vs bilgisayarsa ve oyun başladıysa
					if (this.getOyunModu() == 0 && this.getGameState() == 2)
					{
						// eğer kullanıcı mousesini kendi kartlarının birinin üzerine tutuyorsa
						if (mouse.x > x && mouse.x < x + (369 / 2) && mouse.y > y)
						{
							// karta arkaplan çiz
							g2.setColor(Color.red);
							g2.fillRect(x - 4, y - 4, 369 / 2 + 8, 512 / 2 + 8);
						}
					}
					this.drawKartInfo(g2, this.oyuncular[0].kartListesi[i], x, y);
					count++;
				}
			}

			// 1. oyuncu kartlarını çiz (üst)
			count = 0;
			for (int i = 0; i < this.oyuncular[1].kartListesi.length; i++)
			{
				if (this.oyuncular[1].kartListesi[i] != null && !this.oyuncular[1].kartListesi[i].kartKullanildiMi)
				{
					int x = 280 + count * 369 / 2 + 10 * count;
					int y = 18;
					this.drawKartInfo(g2, this.oyuncular[1].kartListesi[i], x, y);
					count++;
				}
			}

			// kapışan kartları çiz
			for (int i = 0; i < this.kapisanKartlar.length; i++)
			{
				if (this.kapisanKartlar[i] == null)
					continue;

				int x = 855 + 72;
				int y = 72 + i * (512 / 2) + 87 * i;

				if ((System.currentTimeMillis() - kapismaBaslangicTime > 1400))
					y = y + (i == 0 ? 1 : -1) * (int) (System.currentTimeMillis() - kapismaBaslangicTime - 1400);

				if ((System.currentTimeMillis()) - kapismaBaslangicTime < 700)
					this.drawKartInfo(g2, null, x, y);
				else
					this.drawKartInfo(g2, this.kapisanKartlar[this.kapisanKartlar.length - 1 - i], x, y);
			}

			// bu oyun halinde sürekli çizim güncellenmesi gerektiğinden
			// güncelle
			repaint();
		}
		// eğer oyun bittiyse
		else if (this.getGameState() == 4)
		{
			g2.setFont(new Font("TimesRoman", Font.PLAIN, 32));
			g2.setColor(Color.white);
			g2.drawString("Oyun Bitti", 484, 356);
			g2.setFont(new Font("TimesRoman", Font.PLAIN, 16));
			if (kazanan != null)
			{
				g2.drawString("Kazanan: " + this.kazanan.getOyuncuAdi(), 484, 356 + 36);
				g2.drawString("Skor: " + this.kazanan.getSkor(), 484, 356 + 36 + 20);
			}
			else
				g2.drawString("Berabere!", 484, 356 + 36);
		}
	}

	// masada kalan kullanılmamış kart sayısını döndüren fonksiyon
	public int kartSayisi()
	{
		int count = 0;
		for (Pokemon kart : this.kartListesi)
		{
			if (kart != null && !kart.kartKullanildiMi)
				count++;
		}
		return count;
	}

	// masada belirli bir kartın varlığını ve kullanılıp kullanılmadığını döndüren fonksiyon
	public boolean kartVarMi(Pokemon hedef)
	{
		for (Pokemon kart : this.kartListesi)
		{
			if (kart.getPokemonAdi() == hedef.getPokemonAdi())
			{
				return !kart.kartKullanildiMi;
			}
		}

		return false;
	}

	// masadaki belirlenen kartı kullanılmış hale getiren fonksiyon
	// kartın tekrar kullanılmasını engellemek için
	private void kartKullan(Pokemon hedef)
	{
		if (!this.kartVarMi(hedef))
			return;

		for (Pokemon kart : this.kartListesi)
		{
			if (kart.getPokemonAdi() == hedef.getPokemonAdi())
			{
				kart.kartKullanildiMi = true;
			}
		}
	}

	// belirli bir oyuncuya masadan kart veren fonksiyon, masa envanterinden kartı azaltıyor
	public void kartVer(Oyuncu ply, Pokemon kart)
	{
		if (!this.kartVarMi(kart))
			return;

		System.out.println(kart.getPokemonAdi() + " to " + ply.getOyuncuAdi());

		// oyuncuya masadaki kartın kopyasını vermek için
		// bu şekilde class ı instance ediyoruz
		// kart.clone gibi şeyleri denedim ama olmadı
		try
		{
			ply.kartEkle((Pokemon) Class.forName("Pokemonlar." + kart.getPokemonAdi()).getDeclaredConstructor(int.class).newInstance(kart.getPokemonID()));
		} catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException | ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		this.kartKullan(kart);
	}

	// oyunu kullanıcı tarafından seçilen mod ile başlatan fonksiyon
	public void startGame(int oyunModu)
	{
		this.setOyunModu(oyunModu);

		this.oyuncular[1] = new BilgisayarOyuncusu();
		if (oyunModu == 0)
			this.oyuncular[0] = new InsanOyuncusu();
		else
		{
			this.oyuncular[0] = new BilgisayarOyuncusu();
			this.oyuncular[1].setOyuncuAdi("Bilgisayar 2");
		}

		frame.remove(gui_oyunModu);
		SwingUtilities.updateComponentTreeUI(frame);
		frame.add(this, BorderLayout.CENTER);
		this.setOpaque(false);
		this.setGameState(1);
	}

	// masadaki kullanılmamış kartlardan rastgele bir tane döndüren fonksiyon
	public Pokemon rastgeleKart()
	{
		if (this.kartSayisi() == 0)
			return null;

		int rnd = new Random().nextInt(this.kartSayisi());

		int count = 0;
		for (Pokemon kart : this.kartListesi)
		{
			if (kart == null || kart.kartKullanildiMi)
				continue;

			if (count == rnd)
				return kart;
			count++;
		}

		return null;
	}

	;

	// tüm oyunculara masadan 3 er kart veren fonksiyon
	public void kartDagit(int kactane)
	{
		if (this.gameState < 1)
		{
			System.out.println("Oyuncular hazir olmadan kartlar dagitilamaz.");
			return;
		}

		if (this.kartSayisi() < kactane)
		{
			System.out.println("Masadan dagitilacak kart sayisi yetersiz.");
			return;
		}

		// 'kactane' adet kart ver
		for (int adet = 0; adet < kactane; adet++)
		{
			for (Oyuncu oyuncu : this.oyuncular)
			{
				this.kartVer(oyuncu, rastgeleKart());
			}
		}

		System.out.println("Masa kartlari dagitti.");
		this.setGameState(2); // kartlar dağıtıldı oyuncuların oynaması bekleniyor
	}

	public int getGameState()
	{
		return gameState;
	}

	public void setGameState(int gameState)
	{
		if (this.gameState != gameState)
		{
			System.out.println("Oyun durumu '" + gameStates[this.gameState] + "' dan '" + gameStates[gameState] + "' olarak degistirildi.");
			this.gameState = gameState;
			frame.setTitle(String.format("Pokemon Kart Oyunu - %s - %s", oyunModlari[this.getOyunModu()], gameStates[gameState]));
		}

		if (gameState == 3)
			kapismaBaslangicTime = System.currentTimeMillis();
	}

	public int getOyunModu()
	{
		return this.oyunModu;
	}

	/*
		Oyun modunu [Kullanıcı vs Bilgisayar veya Bilgisayar vs Bilgisayar]
		belirlemeyi sağlayan fonksiyon.
	 */
	public void setOyunModu(int oyunModu)
	{
		System.out.println("Oyun modu '" + oyunModlari[oyunModu] + "' olarak belirlendi.");
		this.oyunModu = oyunModu;
	}
}
