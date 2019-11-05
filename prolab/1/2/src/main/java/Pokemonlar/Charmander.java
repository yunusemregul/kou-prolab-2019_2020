package Pokemonlar;

public class Charmander extends Pokemon {
    private int hasarPuani = 60;
    boolean kartKullanildiMi = false;

    public Charmander()
    {
        super("Charmander","Ateş");
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
