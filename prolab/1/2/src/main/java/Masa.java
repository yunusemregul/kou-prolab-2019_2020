import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import Pokemonlar.*;
import Oyuncular.*;

public class Masa extends JFrame{
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
         */
    // oyun durumunu tutuyor
    private volatile int gameState;
    // oyun durumunun yazılı anlamını tutuyor
    // loglarda vs kullanmak için
    private static String[] gameStates = {"Oyun Baslamadi","Oyuncular Hazir","Oyun Basladi"};

    // masa envanterindeki kartları tutuyor
    private Pokemon[] kartListesi;

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
        super("Pokemon Kart Oyunu");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // bu kısmın tümü ana pencereyi boyutlandırma ile alakalı
        Dimension windowSize = new Dimension(1024,768);
        this.setSize(windowSize);
        this.setMaximumSize(windowSize);
        this.setPreferredSize(windowSize);
        this.setMinimumSize(windowSize);
        this.setResizable(false);

        // pencerenin arkaplan rengi
        this.getContentPane().setBackground(new Color(33,33,33));

        // pencereyi ekranın ortasına koyan kısım
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

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
        this.add(gui_oyunModu);

        // penceremiz üzerinde yaptığımız ekleme işlemleri bitince görünür hale getiriyoruz
        this.setVisible(true);

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

    //@Override
    public void paint(Graphics g)
    {
        super.paint(g);

        if(this.getGameState()>=1)
        {
            g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
            g.setColor(Color.white);
            g.drawString("Masa Kartları", 57, 94);

            // masa kartlarını çiz
            int count = 0;
            for (int i = 0; i < this.kartListesi.length; i++) {
                if(this.kartListesi[i]!=null && !this.kartListesi[i].kartKullanildiMi)
                {
                    g.drawImage(img_kart,25,100+count*(25*18/this.kartSayisi()),null);
                    count++;
                }
            }

            // Oyuncu isimlerini çiz
            g.drawString(this.oyuncular[0].getOyuncuAdi(), 530, 485);
            g.drawString(this.oyuncular[1].getOyuncuAdi(), 530, 325);

            // kartlar üzerindeki sarı şerit ve hasar puanını gösteren kısım
            // ile ilgili ayarlamalar
            int sx = 0; // şeritin kart'a relatif x konumu;
            int sy = 145; // şeritin kart'a relatif y konumu
            int tx = 50; // yazının kart'a relatif x konumu
            int ty = 20; // yazının şerit'e relatif y konumu

            // 0. oyuncu kartlarını çiz (alt)
            count = 0;
            for (int i=0; i<this.oyuncular[0].kartListesi.length; i++)
            {
                if(this.oyuncular[0].kartListesi[i]!=null)
                {
                    int x = 280+count*369/2+10*count;
                    int y = 495;
                    g.drawImage(img_pokemonlar[this.oyuncular[0].kartListesi[i].getPokemonID()],x,y,null);
                    g.setColor(new Color(255, 224, 105));
                    g.fillRect(x,y+sy,369/2,30);
                    g.setColor(Color.black);
                    g.drawString("Hasar: "+ Integer.toString(this.oyuncular[0].kartListesi[i].hasarPuaniGoster()),x+tx,y+sy+ty);
                    count++;
                }
            }

            // 1. oyuncu kartlarını çiz (üst)
            count = 0;
            for (int i=0; i<this.oyuncular[1].kartListesi.length; i++)
            {
                if(this.oyuncular[1].kartListesi[i]!=null)
                {
                    int x = 280+count*369/2+10*count;
                    int y = 45;
                    g.drawImage(img_pokemonlar[this.oyuncular[1].kartListesi[i].getPokemonID()],x,y,null);
                    g.setColor(new Color(255, 224, 105));
                    g.fillRect(x,y+sy,369/2,30);
                    g.setColor(Color.black);
                    g.drawString("Hasar: "+ Integer.toString(this.oyuncular[1].kartListesi[i].hasarPuaniGoster()),x+tx,y+sy+ty);
                    count++;
                }
            }
        }
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
        ply.kartEkle(kart);
        this.kartKullan(kart);
        repaint();
    }

    // oyunu kullanıcı tarafından seçilen mod ile başlatan fonksiyon
    public void startGame(int oyunModu)
    {
        this.oyunModu = oyunModu;
        String tipStr = (oyunModu==0 ? "Kullanıcı vs Bilgisayar" : "Bilgisayar vs Bilgisayar");
        System.out.println("Oyun '"+tipStr+"' tipinde baslatildi.");
        this.setTitle(this.getTitle()+" - "+tipStr);

        if(oyunModu==0)
            this.oyuncular[0] = new InsanOyuncusu();
        else
            this.oyuncular[0] = new BilgisayarOyuncusu();

        this.oyuncular[1] = new BilgisayarOyuncusu();

        this.remove(gui_oyunModu);
        SwingUtilities.updateComponentTreeUI(this);
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
