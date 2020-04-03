package Pokemonlar;

public class Squirtle extends Pokemon
{
	private int hasarPuani;
	boolean kartKullanildiMi;

	public Squirtle()
	{
		super("Squirtle", "Su");
		this.setHasarPuani(30);
	}

	public Squirtle(int pokemonID)
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
