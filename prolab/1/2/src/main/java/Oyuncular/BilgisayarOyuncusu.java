package Oyuncular;

import Pokemonlar.Pokemon;

import java.util.Random;

public class BilgisayarOyuncusu extends Oyuncu
{
	public BilgisayarOyuncusu()
	{
		super(1, "Bilgisayar", 0);
	}

	public BilgisayarOyuncusu(int oyuncuID, String oyuncuAdi, int Skor)
	{
		super(oyuncuID, oyuncuAdi, Skor);
	}

	@Override
	public Pokemon kartSec(Pokemon kart)
	{
        /*
            eğer seçilecek kart belliyse? kartı kullan
            değilse devam edip rastgele bi kart seç

            ilerde geliştirilebilmesi için eklediğim gereksiz bi özellik
         */
		if (kart != null)
		{
			this.kartKullan(kart);
			return kart;
		}

        /*
            bilgisayar oyuncusu sahip olduğu kartlardan rastgele bir kartı
            seçerek kullanır
         */
		int rnd = new Random().nextInt(this.kartSayisi());

		int count = 0;
		for (int i = 0; i < this.kartListesi.length; i++)
		{
			if (this.kartListesi[i] == null || this.kartListesi[i].kartKullanildiMi)
				continue;

			if (count == rnd)
			{
				this.kartKullan(this.kartListesi[i]);
				return this.kartListesi[i];
			}
			count++;
		}

		return null;
	}
}
