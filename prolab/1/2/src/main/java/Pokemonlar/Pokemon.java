package Pokemonlar;

public class Pokemon {
    private int pokemonID;
    private String pokemonAdi;
    private String pokemonTip;
    public boolean kartKullanildiMi = false;

    public Pokemon()
    {
        this.pokemonAdi = "Adsiz Pokemon";
        this.pokemonTip = "Tipsiz Pokemon";
    }

    public Pokemon(String pokemonAdi, String pokemonTip)
    {
        this.pokemonAdi = pokemonAdi;
        this.pokemonTip = pokemonTip;
    }

    public int hasarPuaniGoster()
    {
        return -1;
    }

    public int getPokemonID() {
        return pokemonID;
    }

    public void setPokemonID(int pokemonID) {
        this.pokemonID = pokemonID;
    }

    public String getPokemonAdi() {
        return pokemonAdi;
    }

    public void setPokemonAdi(String pokemonAdi) {
        this.pokemonAdi = pokemonAdi;
    }

    public String getPokemonTip() {
        return pokemonTip;
    }

    public void setPokemonTip(String pokemonTip) {
        this.pokemonTip = pokemonTip;
    }
}