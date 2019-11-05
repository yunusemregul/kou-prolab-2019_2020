package Pokemonlar;

public class Zubat extends Pokemon {
    private int hasarPuani = 50;
    boolean kartKullanildiMi = false;

    public Zubat()
    {
        super("Zubat","Hava");
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
