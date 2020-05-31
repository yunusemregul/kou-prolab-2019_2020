package com.yunusemregul.prolab23;

public class Movie
{

	public String name;
	public String score;
	public String type; // aksiyon, bilim kurgu
	public String kind; // film, dizi

	public Movie(String name, String score, String type, String kind)
	{
		this.name = name;
		this.score = score;
		this.type = type;
		this.kind = kind;
	}
}
