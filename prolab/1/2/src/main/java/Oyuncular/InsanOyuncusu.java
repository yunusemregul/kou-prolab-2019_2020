package Oyuncular;

import Pokemonlar.*;

public class InsanOyuncusu extends Oyuncu {
    @Override
    public Pokemon kartSec()
    {


        return null;
    }

    public InsanOyuncusu()
    {
        super(0,"Kullanıcı",0);
    }

    public InsanOyuncusu(int oyuncuID, String oyuncuAdi, int Skor)
    {
        super(oyuncuID, oyuncuAdi, Skor);
    }
}
