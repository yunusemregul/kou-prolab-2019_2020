package com.yunusemregul.prolab23;

public class Movie
{

	public int id;
	public String name;
	public String score;
	public String type; // aksiyon, bilim kurgu
	public String kind; // film, dizi
	public int chapterCount;
	public int length; // her bölümün dakika uzunluğu

	public Movie(int id, String name, String score, String type, String kind, int chapterCount, int length)
	{
		this.id = id;
		this.name = name;
		this.score = score;
		this.type = type;
		this.kind = kind;
		this.chapterCount = chapterCount;
		this.length = length;
	}
}
