package Oyuncular;

import Pokemonlar.*;

public abstract class Oyuncu {
    private int oyuncuID;
    private String oyuncuAdi;
    private int Skor;
    public Pokemon[] kartListesi = new Pokemon[5];

    public Oyuncu()
    {
        this.setOyuncuID(-1);
        this.setOyuncuAdi("Bilinmeyen");
        this.setSkor(0);
    }

    public Oyuncu(int oyuncuID, String oyuncuAdi, int Skor)
    {
        super();
        this.setOyuncuID(oyuncuID);
        this.setOyuncuAdi(oyuncuAdi);
        this.setSkor(Skor);
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
        if(kartSayisi()==0)
            return false;

        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i].getPokemonAdi()==kart.getPokemonAdi())
            {
                return !this.kartListesi[i].kartKullanildiMi;
            }
        }

        return false;
    }

    public void kartEkle(Pokemon kart)
    {
        if(kartSayisi()==3)
            return;
        if(kartVarMi(kart))
            return;
        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i]==null)
            {
                this.kartListesi[i] = kart;
                break;
            }
        }

        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i]!=null)
            {
                System.out.println(this.kartListesi[i].getPokemonAdi()+" "+this.kartListesi[i].kartKullanildiMi);
            }
        }
        System.out.println(kartSayisi());

    }

    /*
        Proje zorunluluğu:
        Boolean veri tipinde kartKullanildiMi bilgisi tutulmalıdır.
        Kullanılan kartların oyunda bir daha kullanılmasını engellemek için bu veri tipinden yararlanılacaktır.
     */
    public void kartKullan(Pokemon kart)
    {
        if(kartSayisi()==0)
            return;
        if(!kartVarMi(kart))
            return;
        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i].getPokemonAdi()==kart.getPokemonAdi())
            {
                this.kartListesi[i].kullan();
                break;
            }
        }
    }

    public int SkorGoster()
    {
        return this.Skor;
    }

    public abstract Pokemon kartSec();

    public int getOyuncuID() {
        return oyuncuID;
    }

    public void setOyuncuID(int oyuncuID) {
        this.oyuncuID = oyuncuID;
    }

    public String getOyuncuAdi() {
        return oyuncuAdi;
    }

    public void setOyuncuAdi(String oyuncuAdi) {
        this.oyuncuAdi = oyuncuAdi;
    }

    public int getSkor() {
        return Skor;
    }

    public void setSkor(int skor) {
        Skor = skor;
    }
}
