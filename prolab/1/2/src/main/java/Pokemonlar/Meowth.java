package Pokemonlar;

public class Meowth extends Pokemon {
    private int hasarPuani = 40;
    boolean kartKullanildiMi = false;

    public Meowth()
    {
        super("Meowth","Normal");
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
