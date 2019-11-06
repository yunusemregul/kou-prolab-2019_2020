package Pokemonlar;

public class Psyduck extends Pokemon {
    private int hasarPuani;
    boolean kartKullanildiMi = false;

    public Psyduck()
    {
        super("Psyduck","Su");
        this.setHasarPuani(20);
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
