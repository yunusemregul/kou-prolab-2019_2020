#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/*
    sources:
    https://www.tutorialspoint.com/cprogramming/c_file_io.htm
    https://www.geeksforgeeks.org/strtok-strtok_r-functions-c-examples/
    https://www.tutorialspoint.com/c_standard_library/c_function_strchr.htm
*/

// şehirleri tutacak düğüm yapısı
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

// komşuları tutacak düğüm yapısı
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
struct sehirDugum* sehirAdinaDugumBul(struct sehirDugum *list, char sehiradi[40])
{

}

// belirtilen plaka koduna göre düğümü listede bulur ve döndürür
struct sehirDugum* plakaKodaDugumBul(struct sehirDugum *list, int plakaKod)
{

}

// şehir adından listede plaka kodu bulur
int plakaKodBul(struct sehirDugum *list, char sehirAdi[40])
{
    struct sehirDugum *sehir = list;

    while(sehir!=NULL)
    {
        if(strcmp(sehir->sehirAdi,&sehirAdi)==0)
            return sehir->plakaKod;

        sehir = sehir->next;
    }
}

// belirtilen listeye şehri plaka sırasına uygun olacak şekilde ekler
void sehirEkle(struct sehirDugum **list, struct sehirDugum sehir)
{
    // eklenecek yeni şehir için bellek ayırıyoruz
    struct sehirDugum *yenisehir = (struct sehirDugum*)malloc(sizeof(sehir));
    // girilen şehrin bilgilerini kopyalıyoruz
    memcpy(yenisehir,&sehir,sizeof(sehir));

    /*
        şehir için uygun yer bulunur:
            eğer liste boşsa başına eklenir
            eğer liste doluysa plaka koduna göre yer bulunup eklenir:
                listedeki her şehir için eğer eklenecek şehrin plakası listedeki şehrin plakasından küçükse önüne ekle
                listenin sonuna gelindiyse ekle
    */
    struct sehirDugum *temp = (*list);
    if(temp==NULL)
    {
        printf("add to beginning\n");
        (*list) = yenisehir;
        return;        
    }
    else
    {
        while(temp!=NULL)
        {
            if(yenisehir->plakaKod<temp->plakaKod)
            {
                yenisehir->next = temp;
                yenisehir->prev = temp->prev;
                if(temp->prev!=NULL)
                {
                    temp->prev->next = yenisehir;
                    temp->prev = yenisehir;
                }
                return;
            }
            else if(temp->next==NULL)
            {
                yenisehir->prev = temp;
                yenisehir->next = NULL;
                temp->next = yenisehir;
                return;
            }

            temp = temp->next;
        }
    }
}

// belirtilen listeden şehri plaka sırasını düzgün tutacak şekilde siler
void sehirSil(struct sehirDugum *list, struct sehirDugum sehir)
{

}

// belirtilen şehire plaka sırasına uygun olacak şekilde komşuluk ekler
void komsulukEkle(struct sehirDugum *dugum, int plakaKod)
{

}

// belirtilen şehirden plaka sırasına uygun olacak şekilde komşuluk siler
void komsulukSil(struct sehirDugum *dugum, int plakaKod)
{

}

// listedeki bir şehri listele
// karelerin çizilmesine gerek olmadığı söylendi ama hoşuma gidiyor
void sehirBilgiListele(struct sehirDugum *sehir)
{
    for (int i = 0; i < 16 + strlen(sehir->sehirAdi)+2; i++)
        printf("-");
    printf("\n");
    
    printf("| Plaka kodu:\t%d",sehir->plakaKod);
    for (int i = 0; i < strlen(sehir->sehirAdi)-1; i++)
        printf(" ");    
    printf("|\n");

    printf("| Sehir adi:\t%s |\n",sehir->sehirAdi);

    printf("| Bolge:\t%s",sehir->bolge);
    for (int i = 0; i < strlen(sehir->sehirAdi)-1; i++)
        printf(" ");    
    printf("|\n");

    for (int i = 0; i < 16 + strlen(sehir->sehirAdi)+2; i++)
        printf("-");
    printf("\n");

    if(sehir->next!=NULL)
    {
        printf("\t|\t▲\n");
        printf("\t▼\t|\n");
    }
}

// listedeki herşeyi listele güzel türkçem
void bilgiListele(struct sehirDugum *list)
{
    struct sehirDugum *sehir = list;

    while(sehir!=NULL)
    {
        sehirBilgiListele(sehir);

        sehir = sehir->next;
    }
}

// belirtilen listede, girilen bölgedeki şehir bilgilerini (plaka kodu, şehir adı, komşu sayısı) listele
void bolgeyeGoreBilgiListele(struct sehirDugum *list, char bolge[2])
{

}


// belirtilen listede, belirtilen min komşu sayısı ile max komşu sayısı kriterlerine uyan şehirleri göster
void komsuSayisinaGoreBilgiListele(struct sehirDugum *list, int minsayi, int maxsayi)
{

}

/*
    Bonus:
    - Belirli bir sayı aralığında komşu sayısına sahip şehirlerden belirli ortak komşulara sahip olan şehirlerin
    listelenmesi (Örneğin: Komşu sayısı 3 ile 7 arasında olan illerden hem Ankara hem de Konya’ya komşu
    olan şehirler: Aksaray, Eskişehir)    

    Kaç tane şehir girileceği belirli olmadığı için şehirleri bu fonksiyona aralarına virgül konmuş şekilde gönderip
    ayırıp ona göre değerlendirme yapacağım.
*/
void komsuSayisiVeKomsuIsmineGoreBilgiListele(struct sehirDugum *list, int minsayi, int maxsayi, char * komsuSehirler)
{

}

/*
    şehirlerin okunacağı dosya açılır
    bir şehir listesi tanımlanır
    önce bütün şehirler listeye eklenir:
        şehirler dosyası satır satır okunur
        her satır virgüller ile parçalara ayrılır
        komşuların belirtildiği kısma kadar olan parçalar için bir şehir oluşturulup listeye eklenir
    ardından komşular listeye eklenir:
    (önce şehir sonra komşuları yapmamın sebebi komşuların isminin verilmiş olması plakasını bulabilmek için tüm şehirleri dolaşmak gerekiyor-
    tüm şehirler de önce listeye eklenmeden dolaşılamaz)
        şehirler dosyası satır satır okunur
        her satır virgüller ile parçalara ayrılır
        komşuların ekleneceği şehir düğümü parçalara ayrılmış yazıdaki 1. parçadaki plaka koduna göre bulunur
        komşuların belirtildiği kısımdan sonrası için her komşunun plaka kodu bulunur ve ilgili şehir düğümüne eklenir.
*/

int main(void)
{
    // şehirler dosyası
    FILE *fSehirler;
    fSehirler = fopen("sehirler_generated.txt","r");
    
    // ana liste
    struct sehirDugum *list = NULL;

    // okunacak satırı tutan değişken
    char *read;
    // okunacak satırın içeriğini tutan değişken
    char line[256];

    // dosyanın sonuna kadar oku
    // şehirleri listeye ekle
    while ((read = fgets(line, 256, fSehirler)) != NULL)
    {
        // satır sonundaki alt satır işaretini karışıklık oluşturmaması için siliyoruz
        char *ret = strchr(line,'\n');
        if(ret!=NULL)
            *ret = '\0';

        // listeye yeni eklenecek şehir
        struct sehirDugum sehir;

        // yazıyı virgüller ile parçalara bölüyoruz, her parçayı tutacak değişken
        char *part = strtok(line,",");
        // bölünmüş yazıdaki kaçıncı parçada olduğumuzu tutacak değişken
        int partNum = 0;

        while(part!=NULL)
        {
            if(partNum==0)
                sehir.plakaKod = atoi(part);
            else if(partNum==1)
                strcpy(sehir.sehirAdi,part);
            else if(partNum==2)
                strcpy(sehir.bolge,part);

            part = strtok(NULL,",");
            partNum++;
        }
        sehirEkle(&list,sehir);
        bilgiListele(list);
    }

    // kullanıcı arayüzü program kapatılana kadar çalışsın
    while(1)
    {
        
    }
    
    fclose(fSehirler);
}