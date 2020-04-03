package Pokemonlar;

public class Charmander extends Pokemon
{
	private int hasarPuani;
	boolean kartKullanildiMi;

	public Charmander()
	{
		super("Charmander", "Ateş");
		this.setHasarPuani(60);
	}

	public Charmander(int pokemonID)
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
