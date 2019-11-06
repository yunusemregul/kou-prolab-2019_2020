package Pokemonlar;

public class Bulbasaur extends Pokemon {
    private int hasarPuani;
    boolean kartKullanildiMi = false;

    public Bulbasaur()
    {
        super("Bulbasaur","Çim");
        this.setHasarPuani(50);
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
