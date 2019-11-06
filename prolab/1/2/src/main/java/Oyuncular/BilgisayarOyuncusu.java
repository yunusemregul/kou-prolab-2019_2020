package Oyuncular;

import Pokemonlar.*;

public class BilgisayarOyuncusu extends Oyuncu {
    @Override
    public Pokemon kartSec()
    {
        if(this.kartSayisi()==0)
        {
            System.out.println("Bilgisayarin secebilecegi kart kalmadi ancak kart istendi.");
            return null;
        }
        for (int i = 0; i < this.kartListesi.length; i++) {
            if(!this.kartListesi[i].kartKullanildiMi)
            {

            }
        }

        return null;
    }

    public BilgisayarOyuncusu()
    {
        super(1,"Bilgisayar",0);
    }

    public BilgisayarOyuncusu(int oyuncuID, String oyuncuAdi, int Skor)
    {
        super(oyuncuID, oyuncuAdi, Skor);
    }
}
