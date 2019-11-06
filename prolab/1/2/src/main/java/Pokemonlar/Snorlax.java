package Pokemonlar;

public class Snorlax extends Pokemon {
    private int hasarPuani;
    boolean kartKullanildiMi = false;

    public Snorlax()
    {
        super("Snorlax","Normal");
        this.setHasarPuani(30);
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
