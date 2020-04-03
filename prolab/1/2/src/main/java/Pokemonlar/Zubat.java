package Pokemonlar;

public class Zubat extends Pokemon
{
	private int hasarPuani;
	boolean kartKullanildiMi;

	public Zubat()
	{
		super("Zubat", "Hava");
		this.setHasarPuani(50);
	}

	public Zubat(int pokemonID)
	{
		this();
		this.setPokemonID(pokemonID);
	}

	public int hasarPuaniGoster()
	{
		return this.hasarPuani;
	}

	public int getHasarPuani()
	{
		return hasarPuani;
	}

	public void setHasarPuani(int hasarPuani)
	{
		this.hasarPuani = hasarPuani;
	}
}
