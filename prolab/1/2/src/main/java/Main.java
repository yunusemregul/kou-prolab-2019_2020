import Oyuncular.*;
import Pokemonlar.*;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException,InterruptedException {
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

        boolean kartlarDagitildiMi = false;

        // program kapatılana kadar masayı yönetecek olan döngü
        while(true)
        {
            // oyuncular hazırsa (oyun modu seçildi ise) ve kartlar dağıtılmadıysa
            if(masa.getGameState()==1 && !kartlarDagitildiMi)
            {
                // 3 adet kart dağıt
                masa.kartDagit(3);
                kartlarDagitildiMi = true;
            }

            Thread.sleep(100);
        }
    }
}