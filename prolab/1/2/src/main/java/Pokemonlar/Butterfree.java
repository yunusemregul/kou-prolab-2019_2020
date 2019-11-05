package Pokemonlar;

public class Butterfree extends Pokemon {
    private int hasarPuani = 10;
    boolean kartKullanildiMi = false;

    public Butterfree()
    {
        super("Butterfree","Hava");
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
