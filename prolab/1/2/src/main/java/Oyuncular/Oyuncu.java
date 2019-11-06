package Oyuncular;

import Pokemonlar.*;

public abstract class Oyuncu {
    private int oyuncuID;
    private String oyuncuAdi;
    private int Skor;
    Pokemon[] kartListesi = new Pokemon[3];

    public Oyuncu()
    {

    }

    public Oyuncu(int oyuncuID, String oyuncuAdi, int Skor)
    {
        this.oyuncuID = oyuncuID;
        this.oyuncuAdi = oyuncuAdi;
        this.Skor = Skor;
    }

    public int kartSayisi()
    {
        int count = 0;
        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i]!=null)
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

    public boolean kartVarMi(String kartAdi)
    {
        if(kartSayisi()==0)
            return false;

        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i].getPokemonAdi()==kartAdi)
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
    }

    public void kartSil(Pokemon kart)
    {
        if(kartSayisi()==0)
            return;

        for (int i = 0; i < this.kartListesi.length; i++) {
            if(this.kartListesi[i].getPokemonAdi()==kart.getPokemonAdi())
            {
                this.kartListesi[i] = null;
                break;
            }
        }
    }

    public int SkorGoster()
    {
        return this.Skor;
    }

    public abstract void kartSec();

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
