package Pokemonlar;

public class Pikachu extends Pokemon {
    private int hasarPuani = 40;
    boolean kartKullanildiMi = false;

    public Pikachu()
    {
        super("Pikachu","Elektrik");
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
