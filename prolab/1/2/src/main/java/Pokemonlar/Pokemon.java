package Pokemonlar;

public class Pokemon
{
	private int pokemonID;
	private String pokemonAdi;
	private String pokemonTip;
	public boolean kartKullanildiMi;

	public Pokemon()
	{
		this.setPokemonAdi("Adsiz Pokemon");
		this.setPokemonTip("Tipsiz Pokemon"); // :(
		kartKullanildiMi = false;
	}

	public Pokemon(String pokemonAdi, String pokemonTip)
	{
		this.setPokemonAdi(pokemonAdi);
		this.setPokemonTip(pokemonTip);
		kartKullanildiMi = false;
	}

	// pokemon sınıflarında override edileceğinden
	// burada ne olduğunun önemi yok
	public int hasarPuaniGoster()
	{
		return 0;
	}

	public int getPokemonID()
	{
		return pokemonID;
	}

	public void setPokemonID(int pokemonID)
	{
		this.pokemonID = pokemonID;
	}

	public String getPokemonAdi()
	{
		return pokemonAdi;
	}

	public void setPokemonAdi(String pokemonAdi)
	{
		this.pokemonAdi = pokemonAdi;
	}

	public String getPokemonTip()
	{
		return pokemonTip;
	}

	public void setPokemonTip(String pokemonTip)
	{
		this.pokemonTip = pokemonTip;
	}

	public void kullan()
	{
		this.kartKullanildiMi = true;
	}
}