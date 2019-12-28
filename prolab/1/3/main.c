#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

/*
    sources:
    https://www.tutorialspoint.com/cprogramming/c_file_io.htm
    https://www.geeksforgeeks.org/strtok-strtok_r-functions-c-examples/
    https://www.tutorialspoint.com/c_standard_library/c_function_strchr.htm
*/

// normalde program tek bir listeye göre çalışsa da olur ama keyfi olarak birden çok listeye uyumlu yaptım

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

// fflush alternatifi
void clean_stdin(void)
{
    int c;
    do {
        c = getchar();
    } while (c != '\n' && c != EOF);
}

// yazının sonundaki alt satır işaretini siler
void clean_newline(char *str)
{
    if(str[strlen(str)-1]=='\n')
        str[strlen(str)-1] = 0;
}

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
    {
        printf("Sehir listesinde '%s' sehri olmamasina ragmen komsuluga eklenmeye calisildi. Plakasi bulunamadigi icin program calismayacak.\n",sehirAdi);
        exit(2);
    }
}

bool komsuyaSahipMi(struct sehirDugum *sehir, int plakaKod)
{
    // şehrin komşuları gezilir
    struct komsuDugum *komsu = sehir->firstKomsu;
    while (komsu != NULL)
    {
        if (komsu->plakaKod==plakaKod)
        {
            return true;
        }
        komsu = komsu->next;
    }
    return false;
}

// belirtilen şehirden plaka sırasına uygun olacak şekilde komşuluk siler
void komsulukSil(struct sehirDugum *sehir, int plakaKod)
{
    /*
        silinecek şehir bulunur
        silinir
    */
    if(!komsuyaSahipMi(sehir, plakaKod))
    {
        printf("'%s' isimli sehirden olmayan komsu '%d' silinmeye calisildi.\n",sehir->sehirAdi,plakaKod);
        return;
    }

    struct komsuDugum *temp = sehir->firstKomsu;
    struct komsuDugum *onceki = NULL;

    while(temp!=NULL)
    {
        if(temp->plakaKod==plakaKod)
        {
            if(temp==(sehir->firstKomsu))
                sehir->firstKomsu = temp->next;
            else if(temp->next==NULL)
                onceki->next = NULL;
            else
                onceki->next = temp->next;

            sehir->komsuSayisi--;
            free(temp);
            return;
        }

        onceki = temp;
        temp = temp->next;
    }
}

// belirtilen şehire plaka sırasına uygun olacak şekilde komşuluk ekler
void komsulukEkle(struct sehirDugum *list, struct sehirDugum *sehir, int plakaKod)
{
    // komşuya sahipse bir daha eklememek için
    if(komsuyaSahipMi(sehir,plakaKod))
    {
        printf("'%s' sehrinde olan '%d' komsusu tekrar eklenmeye calisildi.\n",sehir->sehirAdi,plakaKod);
        return;
    }

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


// belirtilen listeye şehri plaka sırasına uygun olacak şekilde ekler
void sehirEkle(struct sehirDugum **list, struct sehirDugum sehir)
{
    if(sehirAdinaSehirBul(*list, sehir.sehirAdi)!=NULL)
    {
        printf("Listede olan '%s' sehri tekrar eklenmeye calisildi.\n",sehir.sehirAdi);
        return;
    }
    if(plakaKodaSehirBul(*list, sehir.plakaKod)!=NULL)
    {
        printf("Listede olan bir plaka '%d' ile sehir eklenmeye calisildi.\n",sehir.plakaKod);
        return;
    }

    // eklenecek yeni şehir için bellek ayırıyoruz
    struct sehirDugum *yenisehir = (struct sehirDugum*)malloc(sizeof(sehir));
    // girilen şehrin bilgilerini kopyalıyoruz
    memcpy(yenisehir,&sehir,sizeof(sehir));
    yenisehir->prev = NULL;
    yenisehir->next = NULL;
    yenisehir->firstKomsu = NULL;

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
                yenisehir->next = NULL;
                return;
            }

            temp = temp->next;
        }
    }
}

// belirtilen listeden şehri plaka sırasını düzgün tutacak şekilde siler
// şehri silince tüm şehirlerden silinen şehre komşuluğuda siler
void sehirSil(struct sehirDugum **list, int plakaKod)
{
    /*
        silinecek şehir bulunur
        silinir
    */
    struct sehirDugum *temp = (*list);

    while(temp!=NULL)
    {
        if(temp->plakaKod==plakaKod)
        {
            if(temp==(*list))
            {
                temp->next->prev = NULL;
                (*list) = temp->next;
            }
            else if(temp->next==NULL)
            {
                temp->prev->next = NULL;
            }
            else
            {
                temp->prev->next = temp->next;
                temp->next->prev = temp->prev;
            }

            // tüm şehirlerin tüm komşularını gezip sildiğimiz şehre komşuysa komşuluğu sil
            // innerTemp = tüm şehirleri bidaha gezerkenki geçici değişken
            struct sehirDugum *innerTemp = (*list);
            while(innerTemp!=NULL)
            {
                if(innerTemp->komsuSayisi>0)
                {
                    struct komsuDugum *komsu = innerTemp->firstKomsu;
                    while(komsu!=NULL)
                    {
                        if(komsu->plakaKod==plakaKod)
                            komsulukSil(innerTemp,plakaKod);

                        komsu = komsu->next;
                    }
                }
                innerTemp = innerTemp->next;
            }

            free(temp);
            return;
        }

        temp = temp->next;
    }
}

// listedeki bir şehri listele
// karelerin çizilmesine gerek olmadığı söylendi ama hoşuma gidiyor
void sehirBilgi(struct sehirDugum *list, struct sehirDugum *sehir, bool withKomsu)
{
    for (int i = 0; i < 16 + strlen(sehir->sehirAdi)+2; i++)
        printf("-");
    printf("  ");
    printf("\n");
    
    printf("| Plaka kodu:\t%02d",sehir->plakaKod);
    for (int i = 0; i < strlen(sehir->sehirAdi)-1; i++)
        printf(" ");    
    printf("|");
    // ÜST SATIR SAĞ
    if(sehir->komsuSayisi>0 && withKomsu)
    {
        struct komsuDugum *onceki = NULL;
        struct komsuDugum *temp = sehir->firstKomsu;
        printf("    ");
        while (temp!=NULL)
        {
            if(onceki!=NULL)
            {
                for(int i=0; i<strlen(plakaKodaSehirBul(list,onceki->plakaKod)->sehirAdi)-2;i++)
                    printf(" ");
            }
            printf("    %02d",temp->plakaKod);
            onceki = temp;
            temp = temp->next;
        }
    }
    printf("\n");

    printf("| Sehir adi:\t%s |",sehir->sehirAdi);

    // ORTA SATIR SAĞ
    if(sehir->komsuSayisi>0 && withKomsu)
    {
        printf("   —► ");

        struct komsuDugum *temp = sehir->firstKomsu;
        while (temp!=NULL)
        {
            if(temp!=sehir->firstKomsu)
                printf("  ");
            printf("  %s",plakaKodaSehirBul(list, temp->plakaKod)->sehirAdi);
            temp = temp->next;
        }
    }
    printf("\n");

    // ALT SATIR
    printf("| Bolge:\t%s",sehir->bolge);
    for (int i = 0; i < strlen(sehir->sehirAdi)-1; i++)
        printf(" ");    
    printf("|");
    // ALT SATIR SAĞ
    if(sehir->komsuSayisi>0 && withKomsu)
    {
        struct komsuDugum *onceki = NULL;
        struct komsuDugum *temp = sehir->firstKomsu;
        printf("    ");
        while (temp!=NULL)
        {
            if(onceki!=NULL)
            {
                for(int i=0; i<strlen(plakaKodaSehirBul(list,onceki->plakaKod)->sehirAdi)-2;i++)
                    printf(" ");
            }
            printf("    %s",plakaKodaSehirBul(list,temp->plakaKod)->bolge);
            onceki = temp;
            temp = temp->next;
        }
        printf("\n");

        printf("| Komsu sayisi:\t%d",sehir->komsuSayisi);
        for (int i = 0; i < strlen(sehir->sehirAdi); i++)
            printf(" ");    
        printf("|");

        // sağ
        onceki = NULL;
        temp = sehir->firstKomsu;      
        printf("    ");
        while (temp!=NULL)
        {
            if(onceki!=NULL)
            {
                for(int i=0; i<strlen(plakaKodaSehirBul(list,onceki->plakaKod)->sehirAdi)-2;i++)
                    printf(" ");
            }
            if(temp!=sehir->firstKomsu)
                printf(" ");
            printf("    %d",plakaKodaSehirBul(list,temp->plakaKod)->komsuSayisi);
            onceki = temp;
            temp = temp->next;
        }  
    }
    printf("\n");
   // en alt
    for (int i = 0; i < 16 + strlen(sehir->sehirAdi)+2; i++)
        printf("-");
    printf("\n");
}

// listedeki her şeyi listele
void bilgiListele(struct sehirDugum *list)
{
    struct sehirDugum *sehir = list;

    while(sehir!=NULL)
    {
        sehirBilgi(list, sehir, true);

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

        sehir = sehir->next;
    }
}

// belirtilen listede, girilen bölgedeki şehir bilgilerini (plaka kodu, şehir adı, komşu sayısı) listele
void bolgeyeGoreBilgiListele(struct sehirDugum *list, char bolge[2])
{
    struct sehirDugum *sehir = list;

    while(sehir!=NULL)
    {
        if(strcmp(sehir->bolge,bolge)==0)
            sehirBilgi(list, sehir, true);

        sehir = sehir->next;
    }
}


// belirtilen listede, belirtilen min komşu sayısı ile max komşu sayısı kriterlerine uyan şehirleri göster
void komsuSayisinaGoreBilgiListele(struct sehirDugum *list, int minsayi, int maxsayi)
{
    struct sehirDugum *sehir = list;

    while(sehir!=NULL)
    {
        if(sehir->komsuSayisi>=minsayi && sehir->komsuSayisi<=maxsayi)
            sehirBilgi(list, sehir, true);     

        sehir = sehir->next;
    }
}

/*
    Bonus:
    - Belirli bir sayı aralığında komşu sayısına sahip şehirlerden belirli ortak komşulara sahip olan şehirlerin
    listelenmesi (Örneğin: Komşu sayısı 3 ile 7 arasında olan illerden hem Ankara hem de Konya’ya komşu
    olan şehirler: Aksaray, Eskişehir)    

    Kaç tane şehir girileceği belirli olmadığı için şehirleri bu fonksiyona aralarına virgül konmuş şekilde gönderip
    ayırıp ona göre değerlendirme yapacağım.
*/
void komsuSayisiVeKomsuIsmineGoreBilgiListele(struct sehirDugum *list, int minsayi, int maxsayi, char komsuSehirler[128])
{
    struct sehirDugum *sehir = list;

    // tüm şehirler gezilir
    while (sehir!=NULL)
    {
        // eğer şehrin komşu sayısı tutuyorsa
        if (sehir->komsuSayisi>=minsayi && sehir->komsuSayisi<=maxsayi)
        {
            bool sehirTumOrtakKomsularaSahipMi = true;
            // aranan ortak komşu yazısı gezilir, her ortak komşu için şehrin komşuları gezilip olup olmadığı sorgulanır
            char komsuSehirlerTemp[128];
            strcpy(komsuSehirlerTemp,komsuSehirler);
            char *parca = strtok(komsuSehirlerTemp, ",");
            while (parca!=NULL)
            {
                bool komsuyaSahipMi = false;
                // şehrin komşuları gezilir
                struct komsuDugum *komsu = sehir->firstKomsu;
                while (komsu!=NULL)
                {
                    if (strcmp(plakaKodaSehirBul(list, komsu->plakaKod)->sehirAdi, parca) == 0)
                    {
                        komsuyaSahipMi = true;
                        break;
                    }
                    komsu = komsu->next;
                }
                if (komsuyaSahipMi==false)
                {
                    sehirTumOrtakKomsularaSahipMi = false;
                    break;
                }
                parca = strtok(NULL, ",");
            }
            if (sehirTumOrtakKomsularaSahipMi==true)
                sehirBilgi(list, sehir, true);
        }

        sehir = sehir->next;
    }
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

        if(part==NULL)
            continue;
        
        // bölünmüş yazıdaki kaçıncı parçada olduğumuzu tutacak değişken
        int partNum = 0;

        // komşu eklediğimiz şehir
        struct sehirDugum *sehir;

        while(part!=NULL)
        {
            if(partNum==0)
                sehir = plakaKodaSehirBul(list, atoi(part));
            if(partNum>2)
                komsulukEkle(list, sehir, plakaKodBul(list, part));
            part = strtok(NULL,",");
            partNum++;
        }
    }     
    // kullanıcı arayüzü program kapatılana kadar çalışsın
    int secim = 0;
    while(1)
    {
        printf("Secim yapiniz: ");
        scanf(" %d",&secim);
        clean_stdin(); // stdin bufferini temizliyoruz

        switch(secim)
        {
            case 0:
            {
                printf("Secenekler:\n");
                printf("\t-1) Cikis\n");
                printf("\t 0) Secenekler\n");
                printf("\t 1) Bilgileri Listele\n");
                printf("\t 2) Sehir Ekle\n");
                printf("\t 3) Komsu Ekle\n");
                printf("\t 4) Sehir Sil\n");
                printf("\t 5) Komsu Sil\n");
                /*
                    6-7:
                        - Herhangi bir şehir ismi veya plaka kodu ile aratıldığında şehir bilgileri 
                        (plaka no, şehir adı, bölgesi, komşu sayısı) ve komşu şehirlerinin bilgileri (plaka no, şehir adı ve bölgesi) gösterilmelidir. 
                        Listede olmayan bir şehir için arama yapıldığında “şehir listede yok, eklemek ister misiniz?” gibi bir seçenek sunulmalıdır. (+15p)
                */
                printf("\t 6) Isim ile Sehir Ara\n");
                printf("\t 7) Plaka ile Sehir Ara\n");

                /*
                    8:
                        - Kullanıcı herhangi bir bölgede bulunan şehirlerin bilgilerini (plaka kodu, şehir adı, komşu sayısı) listeleyebilmelidir. (+10p)
                */
                printf("\t 8) Bolge ile Sehir Ara\n");

                /*
                    9:
                        - Belli bir komşu sayısı kriterine uyan şehirler bulunabilmeli ve gösterilmelidir. (Örneğin: 3’ ten fazla komşusu olan illerin listesi) (+10p)
                        min-max verilerek yapılacak
                */
               printf("\t 9) Komsu Sayisi ile Sehir Ara\n");

               /*
                    10:
                        Bonus
                        - Belli bir sayı aralığında komşu sayısına sahip şehirlerden belirli ortak komşulara sahip olan şehirlerin listelenmesi 
                        (Örneğin: Komşu sayısı 3 ile 7 arasında olan illerden hem Ankara hem de Konya’ya komşu olan şehirler: Aksaray, Eskişehir) (+10p)
                */
                printf("\t\t*BONUS*\n");
                printf("\t10) Komsu Sayisi ve Ortak Komsu ile Sehir Ara\n");
                break;
            }
            // programı kapat
            case -1:
            {
                printf("Kullanilan bellek temizleniyor..\n");
                goto end;
                break; // ?
            }
            // bilgi listele
            case 1:
            {
                bilgiListele(list);
                break;
            }
            // şehir ekle
            case 2:
            {
                menu_sehirekle:
                {
                    struct sehirDugum sehir;
                    printf("Eklenecek sehir plakasi girin: ");
                    scanf(" %d",&sehir.plakaKod);
                    clean_stdin();

                    if(plakaKodaSehirBul(list,sehir.plakaKod)!=NULL)
                        break;

                    printf("Sehir adi girin: ");
                    fgets(sehir.sehirAdi, 40, stdin);
                    clean_newline(sehir.sehirAdi);

                    printf("Sehir bolgesi girin (kisaltma): ");
                    fgets(sehir.bolge,3,stdin);
                    clean_stdin();

                    sehirEkle(&list,sehir);
                    printf("Eklenen sehir:\n");
                    sehirBilgi(list,&sehir, false);
                }
                break;
            }
            // komşu ekle
            case 3:
            {
                struct sehirDugum *sehir;
                int sehirPlakaKod;
                int komsuPlakaKod;
                
                printf("Hangi sehre komsu eklenecekse plakasini girin: ");
                scanf(" %d",&sehirPlakaKod);
                sehir = plakaKodaSehirBul(list,sehirPlakaKod);

                if(sehir==NULL)
                {
                    printf("Sehir bulunamadi.\n");
                    break;
                }

                printf("Komsunun plakasini girin: ");
                scanf(" %d",&komsuPlakaKod);

                if(plakaKodaSehirBul(list, komsuPlakaKod)==NULL)
                {
                    printf("Eklenmek istenen komsu listede bulunamadi.\n");
                    break;
                }

                komsulukEkle(list, sehir, komsuPlakaKod);
                printf("Eklenen komsu:\n");
                sehirBilgi(list,plakaKodaSehirBul(list, komsuPlakaKod), false);
                printf("Son durum:\n");
                sehirBilgi(list, sehir, true);
                break;
            }
            // şehir sil
            case 4:
            {
                int plakaKod;
                printf("Silinecek sehrin plakasini girin: ");
                scanf(" %d",&plakaKod);

                if(plakaKodaSehirBul(list,plakaKod)==NULL)
                {
                    printf("Sehir bulunamadi.\n");
                    break;
                }

                sehirSil(&list, plakaKod);
                printf("Son durum:\n");
                bilgiListele(list);
                break;
            }
            // komşu sil
            case 5:
            {
                // komşusu silinecek şehir
                struct sehirDugum *sehir;
                int sehirPlakaKod;
                // silinecek komşu
                int plakaKod;
                printf("Komsusu silinecek sehrin plakasini girin: ");
                scanf(" %d",&sehirPlakaKod);
                sehir = plakaKodaSehirBul(list,sehirPlakaKod);

                if(sehir==NULL)
                {
                    printf("Sehir bulunamadi.\n");
                    break;
                }

                printf("Silinecek komsunun plakasini girin: ");
                scanf(" %d",&plakaKod);

                if(komsuyaSahipMi(sehir, plakaKod))
                {
                    printf("Silinecek komsu: \n");
                    sehirBilgi(list, plakaKodaSehirBul(list,plakaKod), false);
                }
                komsulukSil(sehir,plakaKod);
                
                printf("Son durum: \n");
                sehirBilgi(list,sehir,true);
                break;
            }
            // isim ile şehir ara
            case 6:
            {
                char sehirAdi[40];
                printf("Aranan sehrin ismini girin: "),
                fgets(sehirAdi,40,stdin);
                clean_newline(sehirAdi);
                if(sehirAdinaSehirBul(list,sehirAdi)==NULL)
                {
                    char eh;
                    printf("Aradiginiz sehir bulunamadi eklemek ister misiniz? (e/h)\n");
                    eh = getchar();

                    if(eh=='e' || eh=='E')
                        goto menu_sehirekle;
                }
                else
                    sehirBilgi(list,sehirAdinaSehirBul(list,sehirAdi),true);
                break;
            }
            // plaka ile şehir ara
            case 7:
            {
                int plakaKod;
                printf("Aranan sehrin plakasini girin: "),
                scanf(" %d",&plakaKod);
                if(plakaKodaSehirBul(list,plakaKod)==NULL)
                {
                    char eh;
                    printf("Aradiginiz sehir bulunamadi eklemek ister misiniz? (e/h)\n");
                    eh = getchar();

                    if(eh=='e' || eh=='E')
                        goto menu_sehirekle;
                }
                else
                    sehirBilgi(list,plakaKodaSehirBul(list,plakaKod),true);
                break;
            }
            // bölgeye göre şehir listele
            case 8:
            {
                char bolge[2];
                printf("Bolge kisaltmasini girin: ");
                fgets(bolge,3,stdin);
                bolgeyeGoreBilgiListele(list,bolge);
                break;
            }
            // komşu sayısına göre şehir listele
            case 9:
            {
                int min, max;
                printf("Minimum komsu sayisini girin: ");
                scanf(" %d",&min);
                printf("Maksimum komsu sayisini girin: ");
                scanf(" %d",&max);                
                komsuSayisinaGoreBilgiListele(list, min, max);
                break;
            }
            // belli bir sayı aralığında komşu sayısına sahip şehirlerden belirli ortak komşulara sahip olan şehirlerin listelenmesi 
            case 10:
            {
                int min, max;
                printf("Minimum komsu sayisini girin: ");
                scanf(" %d",&min);
                printf("Maksimum komsu sayisini girin: ");
                scanf(" %d",&max);                
                clean_stdin();

                char ortakKomsular[128];
                printf("Ortak komsulari aralarinda virgul ile giriniz (Ornek: Adana,Bursa,Denizli): ");
                fgets(ortakKomsular,128,stdin);
                clean_newline(ortakKomsular);
                komsuSayisiVeKomsuIsmineGoreBilgiListele(list, min, max, ortakKomsular);
                break;
            }
            default:
            {
                printf("Secim uygun degil. Tum secenekleri gormek icin 0 girebilirsiniz.\n");
                break;
            }
        }
    }
    
    end:
    {
        struct sehirDugum *head = list;
        while(head!=NULL)
        {
            struct sehirDugum *temp = head;
            struct komsuDugum *headkomsu = temp->firstKomsu;
            while(headkomsu!=NULL)
            {
                struct komsuDugum *tempkomsu = headkomsu;
                headkomsu = headkomsu->next;
                free(tempkomsu);
            }
            head = head->next;
            free(temp);
        }
        fclose(fSehirler);
    }

    printf("Kullanilan bellek temizlendi, program sonlandirildi.\n");
}