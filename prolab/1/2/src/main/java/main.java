import Oyuncular.*;
import Pokemonlar.*;

import javax.swing.*;

public class main {
    public static void main(String[] args) {
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

        Masa masa = new Masa(kartListesi);
    }
}
