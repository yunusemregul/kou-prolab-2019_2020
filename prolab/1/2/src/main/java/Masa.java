import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import Pokemonlar.*;
import Oyuncular.*;

public class Masa extends JFrame{
    /*
            oyunModu
                -1: Yok
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
    private static String gameStates[] = {"Oyun Baslamadi","Oyuncular Hazir","Oyun Basladi"};
    private volatile int gameState;
    private Pokemon[] kartListesi;
    private JPanel gui_oyunModu;

    private Oyuncu[] oyuncular = new Oyuncu[2];

    public Masa()
    {
        super("Pokemon Kart Oyunu");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension windowSize = new Dimension(1024,768);
        this.setSize(windowSize);
        this.setMaximumSize(windowSize);
        this.setPreferredSize(windowSize);
        this.setMinimumSize(windowSize);
        this.setResizable(false);

        this.getContentPane().setBackground(new Color(33,33,33));

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        // oyun seçim menüsü
        gui_oyunModu = new JPanel();
        gui_oyunModu.setOpaque(false);

        JLabel OM = new JLabel("Oyun modu seçiniz:");
        OM.setForeground(new Color(255,255,255));

        JButton KB = new JButton("Kullanıcı vs Bilgisayar");
        KB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                startGame(0);
            }
        });
        JButton BB = new JButton("Bilgisayar vs Bilgisayar");
        BB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                startGame(1);
            }
        });
        gui_oyunModu.add(OM);
        gui_oyunModu.add(KB);
        gui_oyunModu.add(BB);

        gui_oyunModu.setLayout(new BoxLayout(gui_oyunModu,BoxLayout.Y_AXIS));

        this.add(gui_oyunModu);

        this.setVisible(true);

        this.setGameState(0);
    }

    public Masa(Pokemon[] kartListesi)
    {
        this();
        this.kartListesi = kartListesi;

        System.out.println("Masa "+this.kartListesi.length+" kart ile olusturuldu.");
    }


    //@Override
    public void paint(Graphics g)
    {
        super.paint(g);

        if(this.getGameState()>=1)
        {
            g.setFont(new Font("TimesRoman", Font.PLAIN, 16));
            g.setColor(Color.white);  // Here
            g.drawString("Masa Kartları", 25, 100);

            for (int i = 0; i < this.kartListesi.length; i++) {
                if(this.kartListesi[i]!=null && !this.kartListesi[i].kartKullanildiMi)
                {
                    // kartı çiz
                }
            }
        }
    }

    public int kartSayisi()
    {
        int count = 0;
        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i]!=null && !this.kartListesi[i].kartKullanildiMi)
                count++;
        }
        return count;
    }

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

    public void kartVer(Oyuncu ply, Pokemon kart)
    {
        if(!this.kartVarMi(kart))
            return;

        System.out.println(kart.getPokemonAdi()+" to "+ply.getOyuncuAdi());
        ply.kartEkle(kart);
        this.kartKullan(kart);
    }

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

    public void kartDagit()
    {
        if(this.gameState<1)
        {
            System.out.println("Oyuncular hazir olmadan kartlar dagitilamaz.");
            return;
        }

        // 3 adet kart ver
        for(int adet=0;adet<3;adet++)
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
