package Pokemonlar;

public class Meowth extends Pokemon {
    private int hasarPuani;
    boolean kartKullanildiMi = false;

    public Meowth()
    {
        super("Meowth","Normal");
        this.setHasarPuani(40);
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
