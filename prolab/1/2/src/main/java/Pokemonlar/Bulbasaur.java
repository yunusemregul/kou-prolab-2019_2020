package Pokemonlar;

public class Bulbasaur extends Pokemon {
    private int hasarPuani = 50;
    boolean kartKullanildiMi = false;

    public Bulbasaur()
    {
        super("Bulbasaur","Çim");
    }

    public int hasarPuaniGoster()
    {
        return this.hasarPuani;
    }

    public int getHasarPuani() {
        return hasarPuani;
    }

    public void setHasarPuani(int hasarPuani) {
        this.hasarPuani = hasarPuani;
    }
}
