package Pokemonlar;

public class Squirtle extends Pokemon {
    private int hasarPuani = 30;
    boolean kartKullanildiMi = false;

    public Squirtle()
    {
        super("Squirtle","Su");
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
