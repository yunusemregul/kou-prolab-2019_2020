package Oyuncular;

import Pokemonlar.Pokemon;

public class InsanOyuncusu extends Oyuncu
{
	public InsanOyuncusu()
	{
		super(0, "Kullanıcı", 0);
	}

	public InsanOyuncusu(int oyuncuID, String oyuncuAdi, int Skor)
	{
		super(oyuncuID, oyuncuAdi, Skor);
	}

	@Override
	public Pokemon kartSec(Pokemon kart)
	{
		this.kartKullan(kart);

		return kart;
	}
}
