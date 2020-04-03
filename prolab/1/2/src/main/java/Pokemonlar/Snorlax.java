package Pokemonlar;

public class Snorlax extends Pokemon
{
	private int hasarPuani;
	boolean kartKullanildiMi;

	public Snorlax()
	{
		super("Snorlax", "Normal");
		this.setHasarPuani(30);
	}

	public Snorlax(int pokemonID)
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
