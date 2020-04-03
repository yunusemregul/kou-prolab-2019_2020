package Pokemonlar;

public class Pikachu extends Pokemon
{
	private int hasarPuani;
	boolean kartKullanildiMi;

	public Pikachu()
	{
		super("Pikachu", "Elektrik");
		this.setHasarPuani(40);
	}

	public Pikachu(int pokemonID)
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
