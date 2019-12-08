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

// belirtilen şehir adına göre şehri listede bulur ve döndürür
struct sehirDugum* sehirAdinaSehirBul(struct sehirDugum *list, char sehirAdi[40])
{
    struct sehirDugum *sehir = list;

    while(sehir!=NULL)
    {
        if(strcmp(sehir->sehirAdi,sehirAdi)==0)
            return sehir;
        
        sehir = sehir->next;
    }
    return NULL;
}

// belirtilen plaka koduna göre şehri listede bulur ve döndürür
struct sehirDugum* plakaKodaSehirBul(struct sehirDugum *list, int plakaKod)
{
    struct sehirDugum *sehir = list;

    while(sehir!=NULL)
    {
        if(sehir->plakaKod==plakaKod)
            return sehir;

        sehir = sehir->next;
    }

    return NULL;
}

// şehir adından listede plaka kodu bulur
int plakaKodBul(struct sehirDugum *list, char sehirAdi[40])
{
    struct sehirDugum *sehir = sehirAdinaSehirBul(list,sehirAdi); 
    if(sehir!=NULL)
        return sehir->plakaKod;
    else
        return -1;
}

// belirtilen listeye şehri plaka sırasına uygun olacak şekilde ekler
void sehirEkle(struct sehirDugum **list, struct sehirDugum sehir)
{
    // eklenecek yeni şehir için bellek ayırıyoruz
    struct sehirDugum *yenisehir = (struct sehirDugum*)malloc(sizeof(sehir));
    // girilen şehrin bilgilerini kopyalıyoruz
    memcpy(yenisehir,&sehir,sizeof(sehir));
    yenisehir->prev = NULL;
    yenisehir->next = NULL;

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
        // eğer listede hiç şehir yoksa başa ekle
        (*list) = yenisehir;
        return;        
    }
    else
    {
        // tüm şehirleri dolaş
        while(temp!=NULL)
        {
            // eğer yeni eklenen şehrin plakası şuanki şehrin plakasından ufaksa önüne ekle
            if(yenisehir->plakaKod<temp->plakaKod)
            {
                yenisehir->next = temp;
                // eğer önü listenin başlangıcı değilse
                if(temp->prev!=NULL)
                {
                    temp->prev->next = yenisehir;
                    yenisehir->prev = (temp->prev);
                }
                else
                // listenin başına ekle
                    (*list) = yenisehir;
                temp->prev = yenisehir;

                return;
            }
            // eğer listenin sonuna gelindiyse ve hala returnlenmediyse listenin sonuna ekle
            else if(temp->next==NULL)
            {
                yenisehir->prev = temp;
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
void komsulukEkle(struct sehirDugum *sehir, int plakaKod)
{
    struct komsuDugum *yenikomsu = (struct komsuDugum *)malloc(sizeof(struct komsuDugum));
    yenikomsu->plakaKod = plakaKod;
    yenikomsu->next = NULL;
    
    struct komsuDugum *temp = sehir->firstKomsu;
    struct komsuDugum *onceki = NULL;
    // eğer hiç komşu yoksa başa ekle
    if(temp==NULL)
    {
        sehir->firstKomsu = yenikomsu;
        sehir->komsuSayisi++;
        return;        
    }
    // komşu varsa
    else
    {
        // tüm komşuları dolaş
        while(temp!=NULL)
        {
            // eğer yeni komşunun plakası şuanki plakadan ufaksa önüne ekle
            if(yenikomsu->plakaKod<temp->plakaKod)
            {
                yenikomsu->next = temp;
                // eğer önü listenin başı değilse
                if(onceki!=NULL)
                    onceki->next = yenikomsu;
                // başıysa
                else
                    sehir->firstKomsu = yenikomsu;
                // komşu sayısını artır
                sehir->komsuSayisi++;
                return;
            }
            // eğer returnlenmeden listenin sonuna geldiysek sona ekle
            else if(temp->next==NULL)
            {
                temp->next = yenikomsu;

                sehir->komsuSayisi++;
                return;
            }

            onceki = temp;
            temp = temp->next;
        }
    }
}

// belirtilen şehirden plaka sırasına uygun olacak şekilde komşuluk siler
void komsulukSil(struct sehirDugum *sehir, int plakaKod)
{

}

// bir şehrin üzerine kayıtlı olan n indisli komşuyu döndürür
struct komsuDugum* nInciKomsu(struct sehirDugum *sehir, int n)
{
    int counter = 0;

    struct komsuDugum *komsu = sehir->firstKomsu;
    while(komsu!=NULL)
    {
        if(counter==n)
            return komsu;
        counter++;
        komsu = komsu->next;
    }

    return NULL;
}

// listedeki bir şehri listele
// karelerin çizilmesine gerek olmadığı söylendi ama hoşuma gidiyor
void sehirBilgiListele(struct sehirDugum *sehir)
{
    for (int i = 0; i < 16 + strlen(sehir->sehirAdi)+2; i++)
        printf("-");
    printf("  ");
    for (int i=0; i < sehir->komsuSayisi; i++)
        printf("  --------  ");
    printf("\n");
    
    printf("| Plaka kodu:\t%02d",sehir->plakaKod);
    for (int i = 0; i < strlen(sehir->sehirAdi)-1; i++)
        printf(" ");    
    printf("|");
    printf("    ");
    int counter = 0;
    // üst satır
    for (int i=0; i < sehir->komsuSayisi*8+(sehir->komsuSayisi-1)*4; i++)
    {
        if(i%12==0 || (counter+1)%8==7)
        {
            printf("|");
            counter = 0;
        }
        else
        {
            printf(" ");
            counter++;
        }
        
    }
    printf("\n");

    printf("| Sehir adi:\t%s |",sehir->sehirAdi);
    printf(" —►");
    printf(" ");
    counter = 0;
    // orta satır
    for (int i=0; i < sehir->komsuSayisi*8+(sehir->komsuSayisi-1)*4; i++)
    {
        if(i%12==0 || (counter+1)%8==7)
        {
            printf("|");
            counter = 0;
        }
        else
        {
            if(i%12>7 && counter==1)
            {
                printf("—► ");
                i+=2;
                counter+=3;
                continue;
            }

            if(counter==2)
            {
                printf("%02d",nInciKomsu(sehir,i/12)->plakaKod);
                i+=1;
                counter+=2;
                continue;
            }

            printf(" ");
            counter++;
        }
        
    }
    printf("\n");

    printf("| Bolge:\t%s",sehir->bolge);
    for (int i = 0; i < strlen(sehir->sehirAdi)-1; i++)
        printf(" ");    
    printf("|");
    printf("    ");
    counter = 0;
    // alt satır
    for (int i=0; i < sehir->komsuSayisi*8+(sehir->komsuSayisi-1)*4; i++)
    {
        if(i%12==0 || (counter+1)%8==7)
        {
            printf("|");
            counter = 0;
        }
        else
        {
            printf(" ");
            counter++;
        }
        
    }
    printf("\n");

    for (int i = 0; i < 16 + strlen(sehir->sehirAdi)+2; i++)
        printf("-");

    printf("  ");
    for (int i=0; i < sehir->komsuSayisi; i++)
        printf("  --------  ");
    printf("\n");

    if(sehir->next!=NULL)
    {
        printf("\t|");
        if(sehir->next->prev!=NULL)
            printf("\t▲\n");
        else
            printf("\n");
        
        printf("\t▼");
        if(sehir->next->prev!=NULL)
            printf("\t|\n");
        else
            printf("\n");
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
    if(fSehirler==NULL)
    {
        printf("Dosya acilirken hata!\n");
        return 0;
    }

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
        sehir.komsuSayisi = 0;

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
    }
    // dosyanın sonuna kadar oku
    // komşuları listeye ekle
    read = NULL;
    rewind(fSehirler);
    while ((read = fgets(line, 256, fSehirler)) != NULL)
    {
        // satır sonundaki alt satır işaretini karışıklık oluşturmaması için siliyoruz
        char *ret = strchr(line,'\n');
        if(ret!=NULL)
            *ret = '\0';

        // yazıyı virgüller ile parçalara bölüyoruz, her parçayı tutacak değişken
        char *part = strtok(line,",");
        // bölünmüş yazıdaki kaçıncı parçada olduğumuzu tutacak değişken
        int partNum = 0;

        // komşu eklediğimiz şehir
        struct sehirDugum *sehir;

        while(part!=NULL)
        {
            if(partNum==0)
                sehir = plakaKodaSehirBul(list, atoi(part));
            if(partNum>2)
            {
                if(plakaKodBul(list,part)==-1)
                {
                    printf("Sehir listesinde '%s' sehri olmamasina ragmen komsuluga eklenmeye calisildi. Plakasi bulunamadigi icin program calismayacak.",part);
                    return 0;
                }
                komsulukEkle(sehir, plakaKodBul(list, part));
            }
            part = strtok(NULL,",");
            partNum++;
        }
    }     
                    bilgiListele(list);

    /*// kullanıcı arayüzü program kapatılana kadar çalışsın
    while(1)
    {
        
    }*/
    
    // to do: free better
    free(list);
    fclose(fSehirler);
}