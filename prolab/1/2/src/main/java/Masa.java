import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private Pokemon[] kartListesi;
    private JPanel oyunModuPanel;

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

        this.getContentPane().setBackground(new Color(33,33,33));

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        // oyun seçim menüsü
        oyunModuPanel = new JPanel();
        oyunModuPanel.setOpaque(false);

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
        oyunModuPanel.add(OM);
        oyunModuPanel.add(KB);
        oyunModuPanel.add(BB);

        oyunModuPanel.setLayout(new BoxLayout(oyunModuPanel,BoxLayout.Y_AXIS));

        this.add(oyunModuPanel);

        this.setVisible(true);
    }

    public Masa(Pokemon[] kartListesi)
    {
        this();
        this.kartListesi = kartListesi;

        System.out.println("Masa "+this.kartListesi.length+" kart ile olusturuldu.");
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
        {
            this.oyuncular[0] = new InsanOyuncusu();
            this.oyuncular[1] = new InsanOyuncusu();
        }
        else
        {
            this.oyuncular[0] = new BilgisayarOyuncusu();
            this.oyuncular[1] = new BilgisayarOyuncusu();
        }

        this.remove(oyunModuPanel);
        SwingUtilities.updateComponentTreeUI(this);
    }

    public void kartDagit()
    {
        for (int i = 0; i < this.oyuncular.length; i++) {

        }

        System.out.println("Masa kartlari dagitti.");
    }
}
