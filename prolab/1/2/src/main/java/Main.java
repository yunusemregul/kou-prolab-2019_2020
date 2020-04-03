import Pokemonlar.*;

import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException, InterruptedException
	{
		// masada kullanılacak kartların listesi
		Pokemon[] kartListesi = new Pokemon[]{
				new Pikachu(),
				new Bulbasaur(),
				new Charmander(),
				new Squirtle(),
				new Zubat(),
				new Psyduck(),
				new Snorlax(),
				new Butterfree(),
				new Jigglypuff(),
				new Meowth()
		};

		// gui ve oyuna ait temel fonksiyonları içeren classtan
		// oluşturuyoruz
		Masa masa = new Masa(kartListesi);

		// program kapatılana kadar masayı yönetecek olan döngü
		while (true)
		{
			// oyuncular hazırsa (oyun modu seçildi ise) ve kartlar dağıtılmadıysa
			if (masa.getGameState() == 1)
			{
				// 3 adet kart dağıt
				masa.kartDagit(3);
			}

			// eğer oyun bilgisayar vs bilgisayar modunda ise ve oyuncuların kart seçmesi/kart oynaması bekleniyorsa
			if (masa.getOyunModu() == 1 && masa.getGameState() == 2)
			{
				// eğer oyuncuların yeteri kadar kartı varsa
				if (masa.oyuncular[0].kartSayisi() > 0)
				{
					Thread.sleep(1000);
					masa.kapisanKartlar[0] = masa.oyuncular[0].kartSec(null);
					masa.kapisanKartlar[1] = masa.oyuncular[1].kartSec(null);
					masa.setGameState(3);
				}
				// oyuncuların kartı yoksa biraz bekleyip masa kart dağıtsın 1 er tane
				else
				{
					Thread.sleep(500);
					masa.kartDagit(1);
				}
			}

			// eğer masa kapışma durumundaysa
			if (masa.getGameState() == 3)
			{
				// eğer kapışan kartlar boş değilse, kapıştır sonuca göre puan ekle
				if (masa.kapisanKartlar[0] != null && masa.kapisanKartlar[1] != null)
				{
					Thread.sleep(1500);
					// eğer kartların hasarı birbirine eşit değilse
					if (masa.kapisanKartlar[0].hasarPuaniGoster() != masa.kapisanKartlar[1].hasarPuaniGoster())
					{
						if (masa.kapisanKartlar[0].hasarPuaniGoster() > masa.kapisanKartlar[1].hasarPuaniGoster())
							masa.oyuncular[0].addSkor(5);
						else
							masa.oyuncular[1].addSkor(5);
					}
					// eşitse
					else
						System.out.println("Kart kapismasi berabere. Kimse skor kazanamadi!");

					// kapışan kartları yok ediyoruz
					masa.kapisanKartlar[0] = null;
					masa.kapisanKartlar[1] = null;

					// masada veya oyuncularda kart kaldıysa kapışmaya devam etsinler, kalmadıysa oyun bitsin
					if (masa.kartSayisi() > 0 || masa.oyuncular[0].kartSayisi() > 0)
						masa.setGameState(2); // destelerini oynamaya devam etsinler
					else
					{
						masa.setGameState(4); // oyun bitsin

						if (masa.oyuncular[0].getSkor() == masa.oyuncular[1].getSkor())
							System.out.println("Iki oyuncunun da skoru " + masa.oyuncular[0].getSkor() + ". Oyun berabere bitti!");
						else if (masa.oyuncular[0].getSkor() > masa.oyuncular[1].getSkor())
							masa.kazanan = masa.oyuncular[0];
						else
							masa.kazanan = masa.oyuncular[1];

						if (masa.kazanan != null)
							System.out.println(masa.kazanan.getOyuncuAdi() + " oyunu " + masa.kazanan.getSkor() + " skor ile kazandi!");
					}
				}
			}

			if (masa.getGameState() == 4)
				Thread.sleep(1000);

			Thread.sleep(100);
		}
	}
}