import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import Pokemonlar.*;
import Oyuncular.*;

public class Masa extends JPanel{
    /*
            oyunModu
                -1: Yok/Belirlenmedi
                0: Kullanıcı vs Bilgisayar
                1: Bilgisayar vs Bilgisayar
         */
    private int oyunModu = -1;

    /*
            gameState
                0: Oyun başlamadı
                1: Oyuncular hazır
                2: Oyun başladı
                3: Kartlar Dağıtılıyor
         */
    // oyun durumunu tutuyor
    private volatile int gameState;
    // oyun durumunun yazılı anlamını tutuyor
    // loglarda vs kullanmak    için
    private static String[] gameStates = {"Oyun Baslamadi","Oyuncular Hazir","Oyun Basladi"};

    // masa envanterindeki kartları tutuyor
    private Pokemon[] kartListesi;

    // ana framemiz
    private JFrame frame;

    // gui_oyunModu paneline erişim için burada tanımladım
    private JPanel gui_oyunModu;

    // masadaki oyuncuları tutuyor
    // 0. oyuncu = kullanıcı veya bilgisayar
    // 1. oyuncu = bilgisayar
    private Oyuncu[] oyuncular = new Oyuncu[2];

    // açılmamış kart imajını tutuyor
    private Image img_kart;
    // pokemonlara ait imajları tutuyor
    private Image[] img_pokemonlar;

    // çizimde gerekli imajları hazırlayan fonksiyon
    private void LoadImages() throws IOException
    {
        img_kart = ImageIO.read(getClass().getResource("kart.png"));
        img_kart = img_kart.getScaledInstance(369/2,512/2, Image.SCALE_SMOOTH);

        // kartlistesindeki pokemonların imajlarını hazırlıyoruz
        for (int i = 0; i < this.kartListesi.length; i++) {
            // pokemon adını lowercase yaparak resource klasöründeki png adına ulaşıyoruz
            String name = this.kartListesi[i].getPokemonAdi().toLowerCase()+".png";
            img_pokemonlar[i] = ImageIO.read(getClass().getResource(name));
            img_pokemonlar[i] = img_pokemonlar[i].getScaledInstance(369/2,512/2, Image.SCALE_SMOOTH);
        }
    }

    // constructor
    public Masa() throws IOException {
        // başlık
        frame = new JFrame("Pokemon Kart Oyunu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // bu kısmın tümü ana pencereyi boyutlandırma ile alakalı
        Dimension windowSize = new Dimension(1024,768);
        frame.setSize(windowSize);
        frame.setMaximumSize(windowSize);
        frame.setPreferredSize(windowSize);
        frame.setMinimumSize(windowSize);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        // pencerenin arkaplan rengi
        frame.getContentPane().setBackground(new Color(33,33,33));

        // pencereyi ekranın ortasına koyan kısım
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2-frame.getSize().width/2, dim.height/2-frame.getSize().height/2);

        // oyun seçim paneli
        gui_oyunModu = new JPanel();
        gui_oyunModu.setOpaque(false);

        // kullanıcıya oyun modu seçimi sunan öğeler
        JLabel OM = new JLabel("Oyun modu seçiniz:");
        OM.setForeground(new Color(255,255,255));

        JButton KB = new JButton("Kullanıcı vs Bilgisayar");
        KB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // kullanıcı vs bilgisayar seçimi yapıldığında oyunu
                // ilgili mod ile başlat
                startGame(0);
            }
        });
        JButton BB = new JButton("Bilgisayar vs Bilgisayar");
        BB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // bilg. vs bilg. seçilirse oyunu
                // ilgili mod ile başlat
                startGame(1);
            }
        });

        // oluşturulan gui öğelerini gui_oyunmodu paneline ekliyoruz
        gui_oyunModu.add(OM);
        gui_oyunModu.add(KB);
        gui_oyunModu.add(BB);

        gui_oyunModu.setLayout(new BoxLayout(gui_oyunModu,BoxLayout.Y_AXIS));

        // gui_oyunmodu panelini ana pencereye ekliyoruz
        frame.add(gui_oyunModu);

        // penceremiz üzerinde yaptığımız ekleme işlemleri bitince görünür hale getiriyoruz
        frame.setVisible(true);

        // tıklamalar için mouse listener
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point mouse = new Point(e.getX(),e.getY());

                if(getOyunModu()==0)
                {
                    int count = 0;
                    for (int i = 0; i < kartListesi.length; i++) {
                        if(kartListesi[i]!=null && !kartListesi[i].kartKullanildiMi)
                        {
                            // 370/kartsayısı kısmı kart sayısı çoğaldığında kartlar arasındaki boşluğu
                            // daraltmak için
                            // 370 de deneyerek bulduğum özel ayar
                            int x = 25;
                            int y = 73+count*(370/(kartSayisi()));

                            if(getOyunModu()==0 && (mouse.x>x && mouse.x<x+(369/2) && mouse.y>y && mouse.y<y+(370/(kartSayisi()))))
                            {
                                System.out.println("masadan "+count+". kart ("+kartListesi[i].getPokemonAdi()+") alinmaya calisildi");
                                kartVer(oyuncular[0],kartListesi[i]);
                            }
                            count++;
                        }
                    }

                    count = 0;
                    for (int i = 0; i < oyuncular[0].kartListesi.length; i++) {
                        if(oyuncular[0].kartListesi[i]!=null && !oyuncular[0].kartListesi[i].kartKullanildiMi) {
                            int x = 280 + count * 369 / 2 + 10 * count;
                            int y = 468;

                            if (getOyunModu() == 0 && (mouse.x > x && mouse.x < x + (369 / 2) && mouse.y > y)) {
                                oyuncular[0].kartKullan(oyuncular[0].kartListesi[i]);
                                System.out.println("hebe "+oyuncular[0].kartListesi[i]);
                            }
                            count++;
                        }
                    }
                }
            }
        });

        // oyunu başlamamış olarak belirliyoruz
        this.setGameState(0);
    }

    public Masa(Pokemon[] kartListesi) throws IOException {
        this();

        this.kartListesi = kartListesi;
        for (int i = 0; i < this.kartListesi.length; i++) {
            this.kartListesi[i].setPokemonID(i); // kart kullanıcıya verildiğinde
            // masadaki indexine ulaşamayacığımız için
            // id değerine masadaki indexini kaydediyoruz
            // ki imajına ulaşabilelim
        }

        System.out.println("Masa "+this.kartListesi.length+" kart ile olusturuldu.");

        img_pokemonlar = new Image[this.kartListesi.length];
        // çizimde gerekli imajları yükle
        this.LoadImages();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g; // ekstra fonksiyonlar için
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // estetik için

        Point mouse = MouseInfo.getPointerInfo().getLocation();
        if(frame!=null)
        {
            SwingUtilities.convertPointFromScreen(mouse,frame);
        }

        if(this.getGameState()>=1)
        {
            g2.setFont(new Font("TimesRoman", Font.PLAIN, 16));
            g2.setColor(Color.white);
            g2.drawString("Masa Kartları ", 57, 67);

            // masaya ait kartları çiz
            int count = 0;
            for (int i = 0; i < this.kartListesi.length; i++) {
                if(this.kartListesi[i]!=null && !this.kartListesi[i].kartKullanildiMi)
                {
                    // 370/kartsayısı kısmı kart sayısı çoğaldığında kartlar arasındaki boşluğu
                    // daraltmak için
                    // 370 de deneyerek bulduğum özel ayar
                    int x = 25;
                    int y = 73+count*(370/(this.kartSayisi()));

                    if(this.getOyunModu()==0 && (mouse.x>x && mouse.x<x+(369/2) && mouse.y>y && mouse.y<y+(370/(this.kartSayisi()))))
                    {
                        g2.setColor(Color.red);
                        g2.fillRect(x-4,y-4,369/2+8,512/2+8);
                    }
                    g2.drawImage(img_pokemonlar[this.kartListesi[i].getPokemonID()],x,y,null);
                    count++;
                }
            }

            // Oyuncu isimlerini çiz
            g2.setColor(Color.white);
            g2.drawString(this.oyuncular[0].getOyuncuAdi(), 530, 458);
            g2.drawString(this.oyuncular[1].getOyuncuAdi(), 530, 298);

            // kartlar üzerindeki sarı şerit ve hasar puanını gösteren kısım
            // ile ilgili ayarlamalar
            int sy = 145; // şeritin kart'a relatif y konumu
            int tx = 50; // yazının kart'a relatif x konumu
            int ty = 35; // yazının şerit'e relatif y konumu
            int sh = 60; // şeritin uzunluğu

            // 0. oyuncu kartlarını çiz (alt)
            count = 0;
            for (int i=0; i<this.oyuncular[0].kartListesi.length; i++)
            {
                if(this.oyuncular[0].kartListesi[i]!=null && !this.oyuncular[0].kartListesi[i].kartKullanildiMi)
                {
                    int x = 280+count*369/2+10*count;
                    int y = 468;

                    if(this.getOyunModu()==0 && (mouse.x>x && mouse.x<x+(369/2) && mouse.y>y))
                    {
                        g2.setColor(Color.red);
                        g2.fillRect(x-4,y-4,369/2+8,512/2+8);
                    }
                    g2.drawImage(img_pokemonlar[this.oyuncular[0].kartListesi[i].getPokemonID()],x,y,null);
                    g2.setColor(new Color(255, 224, 105));
                    g2.fillRect(x,y+sy,369/2,sh);
                    g2.setColor(Color.black);
                    g2.drawString("Hasar: "+ Integer.toString(this.oyuncular[0].kartListesi[i].hasarPuaniGoster()),x+tx,y+sy+ty);
                    count++;
                }
            }

            // 1. oyuncu kartlarını çiz (üst)
            count=0;
            for (int i=0; i<this.oyuncular[1].kartListesi.length; i++)
            {
                if(this.oyuncular[1].kartListesi[i]!=null && !this.oyuncular[1].kartListesi[i].kartKullanildiMi)
                {
                    int x = 280+count*369/2+10*count;
                    int y = 18;
                    g2.drawImage(img_pokemonlar[this.oyuncular[1].kartListesi[i].getPokemonID()],x,y,null);
                    g2.setColor(new Color(255, 224, 105));
                    g2.fillRect(x,y+sy,369/2,sh);
                    g2.setColor(Color.black);
                    g2.drawString("Hasar: "+ Integer.toString(this.oyuncular[1].kartListesi[i].hasarPuaniGoster()),x+tx,y+sy+ty);
                    count++;
                }
            }
        }

        repaint();
    }

    // masada kalan kullanılmamış kart sayısını döndüren fonksiyon
    public int kartSayisi()
    {
        int count = 0;
        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i]!=null && !this.kartListesi[i].kartKullanildiMi)
                count++;
        }
        return count;
    }

    // masada belirli bir kartın varlığını ve kullanılıp kullanılmadığını döndüren fonksiyon
    public boolean kartVarMi(Pokemon kart)
    {
        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i].getPokemonAdi()==kart.getPokemonAdi())
            {
                return !this.kartListesi[i].kartKullanildiMi;
            }
        }

        return false;
    }

    // masadaki belirlenen kartı kullanılmış hale getiren fonksiyon
    // kartın tekrar kullanılmasını engellemek için
    private void kartKullan(Pokemon kart)
    {
        if(!this.kartVarMi(kart))
            return;

        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i].getPokemonAdi()==kart.getPokemonAdi())
            {
                this.kartListesi[i].kartKullanildiMi = true;
            }
        }
    }

    // belirli bir oyuncuya masadan kart veren fonksiyon, masa envanterinden kartı azaltıyor
    public void kartVer(Oyuncu ply, Pokemon kart)
    {
        if(!this.kartVarMi(kart))
            return;

        System.out.println(kart.getPokemonAdi()+" to "+ply.getOyuncuAdi());
        /*
            TODO:
            kartın kopyasının oluşturulması gerek, masadaki kartı oyuncuya verince
            problemli oluyor, şuan oyunculara kart gitmeme sebebi kartKullanildiMi degeri
            masada true yapıldığı için oyuncudada true olduğundan
         */
        // şimdilik berbat çözüm
        switch (kart.getPokemonAdi())
        {
            case "Bulbasaur":
            {
                ply.kartEkle(new Bulbasaur(kart.getPokemonID()));
                break;
            }
            case "Butterfree":
            {
                ply.kartEkle(new Butterfree(kart.getPokemonID()));
                break;
            }
            case "Charmander":
            {
                ply.kartEkle(new Charmander(kart.getPokemonID()));
                break;
            }
            case "Jigglypuff":
            {
                ply.kartEkle(new Jigglypuff(kart.getPokemonID()));
                break;
            }
            case "Meowth":
            {
                ply.kartEkle(new Meowth(kart.getPokemonID()));
                break;
            }
            case "Pikachu":
            {
                ply.kartEkle(new Pikachu(kart.getPokemonID()));
                break;
            }
            case "Psyduck":
            {
                ply.kartEkle(new Psyduck(kart.getPokemonID()));
                break;
            }
            case "Snorlax":
            {
                ply.kartEkle(new Snorlax(kart.getPokemonID()));
                break;
            }
            case "Squirtle":
            {
                ply.kartEkle(new Squirtle(kart.getPokemonID()));
                break;
            }
            case "Zubat":
            {
                ply.kartEkle(new Zubat(kart.getPokemonID()));
                break;
            }
        }
        this.kartKullan(kart);
    }

    // oyunu kullanıcı tarafından seçilen mod ile başlatan fonksiyon
    public void startGame(int oyunModu)
    {
        this.setOyunModu(oyunModu);
        String tipStr = (oyunModu==0 ? "Kullanıcı vs Bilgisayar" : "Bilgisayar vs Bilgisayar");
        System.out.println("Oyun '"+tipStr+"' tipinde baslatildi.");
        frame.setTitle(frame.getTitle()+" - "+tipStr);

        if(oyunModu==0)
            this.oyuncular[0] = new InsanOyuncusu();
        else
            this.oyuncular[0] = new BilgisayarOyuncusu();

        this.oyuncular[1] = new BilgisayarOyuncusu();

        frame.remove(gui_oyunModu);
        SwingUtilities.updateComponentTreeUI(frame);
        frame.add(this,BorderLayout.CENTER);
        this.setOpaque(false);
        this.setGameState(1);
    }

    // masadaki kullanılmamış kartlardan rastgele bir tane döndüren fonksiyon
    public Pokemon rastgeleKart()
    {
        int rnd = new Random().nextInt(this.kartSayisi());

        int count = 0;
        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i].kartKullanildiMi)
            {
                continue;
            }

            if(count==rnd)
                return this.kartListesi[i];
            count++;
        }

        return null;
    };

    // tüm oyunculara masadan 3 er kart veren fonksiyon
    public void kartDagit(int kactane)
    {
        if(this.gameState<1)
        {
            System.out.println("Oyuncular hazir olmadan kartlar dagitilamaz.");
            return;
        }

        // 3 adet kart ver
        for(int adet=0;adet<kactane;adet++)
        {
            for (int i = 0; i < this.oyuncular.length; i++) {
                this.kartVer(this.oyuncular[i],rastgeleKart());
            }
        }

        System.out.println("Masa kartlari dagitti.");
        this.setGameState(2);
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        if(this.gameState!=gameState)
        {
            System.out.println("Oyun durumu '" + this.gameStates[this.gameState] + "' dan '" + this.gameStates[gameState] + "' olarak degistirildi.");
            this.gameState = gameState;
        }
    }

    public int getOyunModu() {
        return oyunModu;
    }

    public void setOyunModu(int oyunModu) {
        this.oyunModu = oyunModu;
    }
}
