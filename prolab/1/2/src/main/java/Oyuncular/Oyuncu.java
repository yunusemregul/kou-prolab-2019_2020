package Oyuncular;

public abstract class Oyuncu {
    private int oyuncuID;
    private String oyuncuAdi;
    private int Skor;

    public Oyuncu()
    {

    }

    public Oyuncu(int oyuncuID, String oyuncuAdi, int Skor)
    {
        this.oyuncuID = oyuncuID;
        this.oyuncuAdi = oyuncuAdi;
        this.Skor = Skor;
    }

    public int SkorGoster()
    {
        return this.Skor;
    }

    public abstract void kartSec();

    public int getOyuncuID() {
        return oyuncuID;
    }

    public void setOyuncuID(int oyuncuID) {
        this.oyuncuID = oyuncuID;
    }

    public String getOyuncuAdi() {
        return oyuncuAdi;
    }

    public void setOyuncuAdi(String oyuncuAdi) {
        this.oyuncuAdi = oyuncuAdi;
    }

    public int getSkor() {
        return Skor;
    }

    public void setSkor(int skor) {
        Skor = skor;
    }
}
