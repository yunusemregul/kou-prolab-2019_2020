#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*
    https://www.tutorialspoint.com/cprogramming/c_file_io.htm
*/

struct sehirDugum
{
    struct sehirDugum *prev;
    struct sehirDugum *next;

    int plakaKod;
    char sehirAdi[40];
    char bolge[2];

    struct komsuDugum *firstKomsu;
    int komsuSayisi;
};

struct komsuDugum
{
    struct komsuDugum *next;
    int plakaKod;
};

/*
    Uyarı:
        Düğümler plaka koduna göre ardışık sıralı olmalıdır. (Hem şehir düğümleri hem de komşu düğümler
için)
*/

// belirtilen şehir adına göre düğümü listede bulur ve döndürür
struct sehirDugum* sehirAdiDugumBul(struct sehirDugum *list, char sehiradi[40])
{

}

// belirtilen plaka koduna göre düğümü listede bulur ve döndürür
struct sehirDugum* plakaKodDugumBul(struct sehirDugum *list, int plakaKod)
{

}

// belirtilen listeye şehri plaka sırasına uygun olacak şekilde ekler
void sehirEkle(struct sehirDugum *list, struct sehirDugum sehir)
{

}

// belirtilen listeden şehri plaka sırasını düzgün tutacak şekilde siler
void sehirSil(struct sehirDugum *list, struct sehirDugum sehir)
{

}

// belirtilen şehire plaka sırasına uygun olacak şekilde komşuluk ekler
void komsulukEkle(struct sehirDugum *dugum, int plakaKod)
{

}

// belirtilen şehirden plaka sırasına uygun olacka şekilde komşuluk siler
void komsulukSil(struct sehirDugum *dugum, int plakaKod)
{

}

// belirtilen listede, girilen bölgedeki şehir bilgilerini (plaka kodu, şehir adı, komşu sayısı) listele
void bolgeyeGoreBilgiListele(struct sehirDugum *list, char bolge[2])
{

}

/*
    belirtilen listede, komşu sayısı kriterine uyan şehirleri göster

    kriter değeri açıklaması:
        -1 = girilen sayıdan az
        0 = tam girilen sayıda
        1 = girilen sayıdan fazla
*/
void komsuSayisinaGoreBilgiListele(struct sehirDugum *list, int sayi, int kriter)
{

}

/*
    - Belli bir sayı aralığında komşu sayısına sahip şehirlerden belirli ortak komşulara sahip olan şehirlerin
    listelenmesi (Örneğin: Komşu sayısı 3 ile 7 arasında olan illerden hem Ankara hem de Konya’ya komşu
    olan şehirler: Aksaray, Eskişehir)    

    ??
*/

int main(void)
{
    // şehirler dosyası
    FILE *fSehirler;
    fSehirler = fopen("sehirler_generated.txt","r");
    
    struct sehirDugum *list = NULL;

    // okunacak satırı ve içeriğini tutan değişkenler
    char line[256];
    char *read;

    // dosyanın sonuna kadar oku
    while ((read = fgets(line, 256, fSehirler)) != NULL)
    {
        
    }    

    // kullanıcı arayüzü program kapatılana kadar çalışsın
    while(1)
    {

    }
    
    fclose(fSehirler);
}