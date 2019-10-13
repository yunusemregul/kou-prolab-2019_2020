#include <stdio.h>
#include <stdlib.h>
#include <dirent.h>
#include <string.h>
#include <sys/types.h>
#include <math.h>

/*
    sources:
        * find all files in a folder
            https://www.geeksforgeeks.org/c-program-list-files-sub-directories-directory/
        
        * find file extension
            https://stackoverflow.com/a/5309514/8993088
        
        * read file line by line
            https://stackoverflow.com/a/3501681/8993088

        * fflush alternative [void clean_stdin(void)]
            https://stackoverflow.com/a/17319153/8993088
*/

/*
    TO DO:
        en kısa noktaları bulurken karşılaştırmada daha hızlı bi algoritma gerek
*/

/*
    TO LEARN:  
        strtok, strrchr, pointer ->, dirent, readdir
*/

// nokta
/*struct Nokta
{
    float x;
    float y;
    float z;

    int r;
    int g;
    int b;
};*/

void clean_stdin(void)
{
    int c;
    do {
        c = getchar();
    } while (c != '\n' && c != EOF);
}

int isCharAllowed(int i)
{
    // kontrol karakterlerine izin vermiyoruz
    if(iscntrl((char)i)&&i!='\n')
        return 0;

    return 1;
}

// -1 döndürmediyse sorun var demektir
int charControl(char * text)
{
    for(int i=0; i<strlen(text);i++)
    {
        if(!isCharAllowed(text[i]))
        {
            return text[i];
        }
    }

    return -1;
}

static char NOKTAVERILERI[6] = {
    'x', 'y', 'z', 'r', 'g', 'b'
};

struct DosyaBaslik
{
    int VERSION;
    int ALANLAR;
    //char ALANLAR[16]; // x y z || x y z r g b
    int NOKTALAR;
    int DATA;
    //char DATA[8]; // ascii || binary
};

struct Dosya
{
    char Ad[128];
    struct DosyaBaslik Baslik;

    //struct Nokta *Noktalar;
    float *Noktalar;
    int OkunanNokta;
};

static char BASLIKTIPLERI[4][12] = {
    "VERSION",
    "ALANLAR",
    "NOKTALAR",
    "DATA"};

enum ALANTIPLERI
{
    ALANTIP_XYZ,
    ALANTIP_XYZRGB
};

enum DATATIPLERI
{
    DATATIP_ASCII,
    DATATIP_BINARY
};

static char ALANTIPLERI[2][16] = {
    "x y z",
    "x y z r g b"
};

static char DATATIPLERI[2][8] = {
    "ascii",
    "binary"
};

/*
    alan tipi veya data tipi okunabilecek alan/data tiplerinde var mı diye kontrol eden fonksiyon

    size :: ALANTIPLERI[->2<-][16]
    innersize :: ALANTIPLERI[2][->16<-]
*/
int IsTipValid(int size, int innersize, char *array, char *tip)
{
    for (int i = 0; i < size; i++)
    {
        if (strcmp(tip, array + i * innersize) == 0)
            return i; // return (bulunan tip)
    }

    return -1;
}

/*
    dosyalardaki hataları boyutu dinamik bir buffere ekleyen fonksiyon

    **hatalar yapıyoruz çünkü gelen *hatalar pointer değeri referans olduğu için
    ve referans üzerinde yapılan değişikler(realloc,memcpy) bu scope un dışına çıkamayacağı için
    pointerin pointeri ni kullanmak zorundayız.
*/
void LogHata(char **hatalar, const char *hata, int satir)
{
    // satır bilgisini koymak istemiyorsak, mesela binary de satır olmadığı için
    // bu fonksiyona -1 gönderiyoruz
    if(satir!=-1)
    {
        // son yeni satır karakterini silip satır ekliyoruz
        strcpy(strrchr(hata,'\n')," ");
        char buf[32];
        sprintf(buf,"[SATIR %d]\n",satir);
        strcat(hata,buf);
    }
    
    // hatalar ın boyutunu yeni eklenecek hata boyutunca artırıyoruz
    *hatalar = (char *)realloc(*hatalar,strlen(*hatalar)*sizeof(char)+strlen(hata)*sizeof(char));
    if(hatalar==NULL)
    {
        printf("'hatalar' icin realloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
        return;
    }  
    // yeni gelen hatayı eski hataların sonuna ekliyoruz
    memcpy(*hatalar+strlen(*hatalar)*sizeof(char),hata,strlen(hata));
}

// noktalar arası mesafeyi hesaplayan fonksiyon
float NoktaMesafe(float a[3], float b[3])
{
    return sqrtf(powf(a[0]-b[0],2.0f)+powf(a[1]-b[1],2.0f)+powf(a[2]-b[2],2.0f));
}

/*
    noktalar içinde birbirine en yakın olanları bulan fonksiyon
*/
void EnYakinNoktalarHesapla(float noktalar[], int alantip, int count, int *to_write)
{
    // initial olarak 0. ve 1. noktayı en yakın olarak belirliyoruz
    // sonrasında bu 2sinin mesafesinden kısa ise karşılaştırılan herhangi 2 nokta
    // bunları değiştiricez basit en küçük bulma mantığı
    to_write[0] = 0;
    to_write[1] = 1;

    int alanboyut = (alantip==ALANTIP_XYZ ? 3 : 6);

    // tüm noktaları birbiriyle karşılaştırıyoruz
    // daha hızlı bi algoritma gerek
    for(int i=0; i<count; i++)
    {
        for(int j=i+1; j<=count-1; j++)
        {
            printf("%d %d\n",i,j);
            if(NoktaMesafe(&noktalar[i*alanboyut],&noktalar[j*alanboyut])<NoktaMesafe(&noktalar[to_write[0]*alanboyut],&noktalar[to_write[1]*alanboyut]))
            {
                to_write[0] = i;
                to_write[1] = j;
            }      
        }
    }
}

/*
    noktalar içinde birbirine en uzak olanları bulan fonksiyon
*/
void EnUzakNoktalarHesapla(float noktalar[], int alantip, int count, int *to_write)
{
    // en yakını bulmanın benzeri ama şimdilik yapmak istemiyorum en yakın-daki
    // karşılaştırma algoritması çok yavaş olduğu için hoşuma gitmedi
}

/*
    içine bütün noktaları alan en küçük küpün köşe noktalarını bulan fonksiyon

    mantığım:
        henüz yok

*/
void TumNoktalariIcerenEnKucukKupKoseleri(float noktalar[], int alantip, int count, int *to_write)
{
    int alanboyut = (alantip==ALANTIP_XYZ ? 3 : 6);

    for(int i=0; i<count; i++)
    {

    }
}

int main(void)
{
    // dosyaların bilgilerini içerecek struct
    // boyutu dinamik
    struct Dosya *Dosyalar;
    // initial olarak rastgele bir boyut atıyoruz
    Dosyalar = (struct Dosya *)malloc(sizeof(struct Dosya));

    if(Dosyalar==NULL)
    {
        printf("'Dosyalar' icin malloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
        return 0;
    }

    // hataları içerecek pointer
    char *hatalar = NULL;
    // initial olarak rastgele bir boyut atıyoruz
    hatalar = (char *)malloc(sizeof(char));

    if(hatalar==NULL)
    {
        printf("'hatalar' icin malloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
        return 0;
    }
   
    struct dirent *de; // Pointer for directory entry

    char *line = NULL;
    ssize_t read;
    size_t len = 0;

    // opendir() returns a pointer of DIR type.
    DIR *dr = opendir(".");

    if (dr == NULL) // opendir returns NULL if couldn't open directory
    {
        printf("Could not open current directory");
        return 0;
    }

    // loopdaki anlık dosyanın indexini tutuyor
    int dosyaindex = 0;

    // for every file
    while ((de = readdir(dr)) != NULL)
    {
        // dosya uzantısı
        char *file_extension = strrchr(de->d_name, '.');

        // dosya uzantısı .nkt mı diye kontrol ediyoruz
        if (file_extension != NULL && strcmp(file_extension, ".nkt") == 0)
        {
            // yeni bir dosya kaydına başladığımız için dosyalar arrayının boyutunu 1 arttırıyoruz
            Dosyalar = (struct Dosya *)realloc(Dosyalar, (dosyaindex + 1) * sizeof(struct Dosya));
            if(Dosyalar==NULL)
            {
                printf("'Dosyalar' icin realloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
                return 0;
            }  

            strcpy(&Dosyalar[dosyaindex].Ad, de->d_name);

            // okunan dosyanın FILE pointeri
            FILE *file;
            file = fopen(de->d_name, "r");
            if(file==NULL)
            {
                printf("'%s' dosyasi 'r' modunda acilamadi.\n",de->d_name);
                return 0;
            }

            int satir = 0;
            int offset = 0;

            // 4 satırlık başlık bilgisini bulup oku
            for (int i = 0; i < 4; i++)
            {
                read = getline(&line, &len, file);
                offset = offset + strlen(line);
                satir++;

                // eğer başlık bilgisi yoksa
                if (read == -1)
                {
                    char hatabuffer[128];
                    sprintf(hatabuffer, "[%s] dosyasinin baslik bilgisi hatali. [%s] bulunamadi.\n", de->d_name, BASLIKTIPLERI[i]);
                    LogHata(&hatalar,hatabuffer,satir);
                    dosyaindex--;
                    goto end_reading;
                }
                // eğer başlık yorum satırıysa
                // boşver burdan başlama okumaya
                else if (line[0] == '#')
                {
                    i--;
                    continue;
                }
                // başlık bilgisi beklenen sıradan farklıysa
                else if (strcmp(line, BASLIKTIPLERI[i]) != ' ')
                {
                    char hatabuffer[128];
                    sprintf(hatabuffer, "[%s] dosyasinin baslik bilgisi hatali. [%s] beklenirken [%s] okundu.\n", de->d_name, BASLIKTIPLERI[i], line);
                    LogHata(&hatalar,hatabuffer,satir);
                    dosyaindex--;
                    goto end_reading;
                }
                else if(charControl(line)!=-1)
                {
                    char hatabuffer[128];
                    sprintf(hatabuffer, "[%s] dosyasinda uygun olmayan bir karakter okundu. Karakter kodu: [%d]\n", de->d_name, charControl(line));
                    LogHata(&hatalar,hatabuffer,satir);
                    dosyaindex--;
                    goto end_reading;                    
                }

                // versiyon oku
                if (strcmp(line, "VERSION") == ' ')
                {
                    int ver = atoi(line + 8);
                    Dosyalar[dosyaindex].Baslik.VERSION = ver; // VERSION dan sonrasını int e çevir

                    if (ver != 1)
                    {
                        char hatabuffer[128];
                        sprintf(hatabuffer, "[%s] dosyasi icin VERSION bilgisi [%d] gecersiz. Sadece VERSION 1 dosyalar okunabilir.\n", de->d_name, ver);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }
                }

                // alanlar oku
                if (strcmp(line, "ALANLAR") == ' ')
                {
                    char *alanbilgisi = line + 8;
                    alanbilgisi[strlen(alanbilgisi) - 1] = '\0';

                    int tip = IsTipValid(2, 16, &ALANTIPLERI, alanbilgisi);

                    if (tip == -1)
                    {
                        char hatabuffer[128];             
                        sprintf(hatabuffer, "[%s] dosyasi icin ALAN bilgisi [%s] gecersiz.\n", de->d_name, alanbilgisi);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }

                    Dosyalar[dosyaindex].Baslik.ALANLAR = tip;
                }

                // noktalar oku
                if (strcmp(line, "NOKTALAR") == ' ')
                {
                    int count = atoi(line + 9);
                    Dosyalar[dosyaindex].Baslik.NOKTALAR = count; // VERSION dan sonrasını int e çevir
                }

                // data oku
                if (strcmp(line, "DATA") == ' ')
                {
                    char *databilgisi = line + 5;
                    databilgisi[strlen(databilgisi) - 1] = '\0';

                    int tip = IsTipValid(2, 8, &DATATIPLERI, databilgisi);

                    if (tip == -1)
                    {
                        char hatabuffer[128];
                        sprintf(hatabuffer,"[%s] dosyasi icin DATA bilgisi [%s] gecersiz.\n", de->d_name, databilgisi);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }

                    Dosyalar[dosyaindex].Baslik.DATA = tip;
                }
            }

            // başlık okumayı bitirdik, nokta okumaya başlıyoruz
                
            // en son nokta indisini dolayısıyla nokta sayısını tutacak değişken
            int noktacount = 0;

            // dosyanın noktalarını tutacak array için initial memory ataması yapıyoruz
            Dosyalar[dosyaindex].Noktalar = (float*)malloc(sizeof(float));
            if(Dosyalar[dosyaindex].Noktalar==NULL)
            {
                printf("'Dosyalar[dosyaindex].Noktalar' icin malloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
                return 0;
            }            

            // ascii şeklinde oku
            if (Dosyalar[dosyaindex].Baslik.DATA == DATATIP_ASCII)
            {
                // satır satır okuyoruz
                while ((read = getline(&line, &len, file)) != -1)
                {
                    satir++;
                    // eğer satır yorum satırıysa boşver
                    if (line[0] == '#')
                        continue;
                   
                    // noktaları okurken parçalar halinde okuyacağız, parça ayracı ve şuanki parçanın adresini tutan token
                    char ayrac[2] = " ";
                    char *token;

                    // ALAN tip XYZ oku
                    if (Dosyalar[dosyaindex].Baslik.ALANLAR == ALANTIP_XYZ)
                    {
                        // yeni nokta kaydı yapacağımız için boyutu ona göre arttırıyoruz
                        Dosyalar[dosyaindex].Noktalar = (float*)realloc(Dosyalar[dosyaindex].Noktalar,sizeof(float)*3*(noktacount+1));
                        if(Dosyalar[dosyaindex].Noktalar==NULL)
                        {
                            printf("'Dosyalar[dosyaindex].Noktalar' icin realloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
                            return 0;
                        }   

                        // arka arkaya 3 tane float okuyacağız
                        // her floatın bitiş noktası space ile ayrıldığı için
                        // strtok ile space ye göre ayırarak işlem yapacağız

                        token = strtok(line, ayrac);

                        // 3 adet float verisi okuyoruz
                        for (int i = 0; i < 3; i++)
                        {
                            if (token == NULL)
                            {
                                char hatabuffer[128];
                                sprintf(hatabuffer,"[%s] isimli dosyada [%d] numarali noktada [%c] bilgisi bulunamadi.\n", de->d_name, noktacount + 1, toupper(NOKTAVERILERI[i]));
                                LogHata(&hatalar,hatabuffer,satir);
                                dosyaindex--;
                                goto end_reading;
                            }

                            // 3 lü 3 lü artan bir nokta arrayı olduğu için şuanki offseti bulmak için
                            // noktacount*3 yapıyoruz ve x y z değerleri içinde i ekliyoruz
                            Dosyalar[dosyaindex].Noktalar[noktacount*3+i] = atof(token);    

                            token = strtok(NULL, ayrac);
                        }
                    }
                    //  ALAN tip XYZRGB oku
                    else if (Dosyalar[dosyaindex].Baslik.ALANLAR == ALANTIP_XYZRGB)
                    {
                        // yeni nokta kaydı yapacağımız için boyutu ona göre arttırıyoruz
                        Dosyalar[dosyaindex].Noktalar = (float*)realloc(Dosyalar[dosyaindex].Noktalar,sizeof(float)*6*(noktacount+1));
                        if(Dosyalar[dosyaindex].Noktalar==NULL)
                        {
                            printf("'Dosyalar[dosyaindex].Noktalar' icin realloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
                            return 0;
                        }  

                        // arka arkaya 6 tane float okuyacağız
                        // her floatın bitiş noktası space ile ayrıldığı için
                        // strtok ile space ye göre ayırarak işlem yapacağız

                        token = strtok(line, ayrac);

                        // 6 adet float verisi okuyoruz
                        for (int i = 0; i < 6; i++)
                        {
                            if (token == NULL)
                            {
                                char hatabuffer[128];
                                sprintf(hatabuffer,"[%s] isimli dosyada [%d] numarali noktada [%c] bilgisi bulunamadi.\n", de->d_name, noktacount + 1, toupper(NOKTAVERILERI[i]));
                                LogHata(&hatalar,hatabuffer,satir);
                                dosyaindex--;
                                goto end_reading;
                            }

                            Dosyalar[dosyaindex].Noktalar[noktacount*6+i] = atof(token);

                            token = strtok(NULL, ayrac);
                        }
                    }

                    noktacount++;
                }
                
                if(noktacount!=Dosyalar[dosyaindex].Baslik.NOKTALAR)
                {
                    char hatabuffer[128];
                    sprintf(hatabuffer,"[%s] isimli dosyadan okunan nokta sayisi [%d] dosya basligindaki nokta sayisiyla [%d] uyusmuyor.\n", de->d_name, noktacount, Dosyalar[dosyaindex].Baslik.NOKTALAR);
                    LogHata(&hatalar,hatabuffer,satir);
                    dosyaindex--;
                    goto end_reading;
                }
                Dosyalar[dosyaindex].OkunanNokta = noktacount;

            }
            // binary şeklinde oku
            else if (Dosyalar[dosyaindex].Baslik.DATA == DATATIP_BINARY)
            {
                fclose(file);
                file = fopen(de->d_name,"rb"); // sorun çözümü rb miş
                if(file==NULL)
                {
                    printf("'%s' dosyasi 'rb' modunda acilamadi.\n",de->d_name);
                    return 0;
                }

                fseek(file,offset,SEEK_SET);
            
                // niye float değil?
                double xyz[3];

                while(fread((void *)(&xyz),sizeof(xyz),1,file)!=NULL)
                {
                    Dosyalar[dosyaindex].Noktalar = (float*)realloc(Dosyalar[dosyaindex].Noktalar,sizeof(float)*3*(noktacount+1));

                    // ALAN tip XYZ oku
                    if (Dosyalar[dosyaindex].Baslik.ALANLAR == ALANTIP_XYZ)
                    {
                        // double arrayı float arraya cast ediyoruz

                        for(int i=0; i<3; i++)
                        {
                            Dosyalar[dosyaindex].Noktalar[noktacount*3+i] = (float)xyz[i];
                        }
                    }
                    //  ALAN tip XYZRGB oku
                    else if (Dosyalar[dosyaindex].Baslik.ALANLAR == ALANTIP_XYZRGB)
                    {
                        // rgb için 3 int daha oku?              
                    }
                    
                    noktacount++;
                }

                if(noktacount!=Dosyalar[dosyaindex].Baslik.NOKTALAR)
                {
                    char hatabuffer[128];
                    sprintf(hatabuffer,"[%s] isimli dosyadan okunan nokta sayisi [%d] dosya basligindaki nokta sayisiyla [%d] uyusmuyor.\n", de->d_name, noktacount, Dosyalar[dosyaindex].Baslik.NOKTALAR);
                    LogHata(&hatalar,hatabuffer,-1);
                    dosyaindex--;
                    goto end_reading;
                }
                Dosyalar[dosyaindex].OkunanNokta = noktacount;
            }

            end_reading:
            fclose(file);

            dosyaindex++;
        }
    }

    int secim = 1;
    // input
    while(1)
    {
        printf("Secim yapiniz: ");
        scanf(" %d",&secim);
        clean_stdin();

        switch(secim)
        {
            case 0:
            {
                printf("Secenekler:\n");
                printf("\t-1) Cikis\n");
                printf("\t0) Secenekler\n");
                printf("\t1) Dosya Kontrolu\n");
                printf("\t2) En Yakin/Uzak Noktalar\n");
                printf("\t3) Kup\n");
                printf("\t4) Kure\n");
                printf("\t5) Nokta Uzakliklari\n");
                break;
            }
            case -1:
            {
                printf("Cikis yapiliyor..\n");
                goto end;
                break;
            }
            case 1:
            {
                if(strlen(hatalar)==0)
                {
                    printf("Tum dosyalar uyumludur.\n");
                }
                else
                {
                    printf("%s\n",hatalar);
                }
                break;
            }
            case 2:
            {
                for(int i=0; i<dosyaindex; i++)
                {
                    // indis tutuyorlar 2 şer nokta indisi
                    int enyakin_noktalar[2];
                    int enuzak_noktalar[2];

                    EnYakinNoktalarHesapla(Dosyalar[i].Noktalar,Dosyalar[i].Baslik.ALANLAR,Dosyalar[i].OkunanNokta, &enyakin_noktalar);
                    printf("%d %d\n",enyakin_noktalar[0],enyakin_noktalar[1]);

                    EnUzakNoktalarHesapla(Dosyalar[i].Noktalar,Dosyalar[i].Baslik.ALANLAR,Dosyalar[i].OkunanNokta, &enuzak_noktalar);
                }
                break;
            }

            default:
            {
                printf("Secim gecersiz. Secenekleri gormek icin 0 girebilirsiniz.\n");
                break;
            }
        }
        printf("\n"); // estetik
    }
    
    end: 
    // for debugging purposes
    /*for (int i = 0; i < dosyaindex; i++)
    {
        printf("DOSYA [%d] [%s]:\n", i + 1, Dosyalar[i].Ad);
        printf("\tVERSION: %d", Dosyalar[i].Baslik.VERSION);
        printf("\tALANLAR: %s\n", ALANTIPLERI[Dosyalar[i].Baslik.ALANLAR]);
        printf("\tNOKTALAR: %d", Dosyalar[i].Baslik.NOKTALAR);
        printf("\tDATA: %s\n", DATATIPLERI[Dosyalar[i].Baslik.DATA]);
        printf("\tOKUNAN NOKTALAR: %d\n", Dosyalar[i].OkunanNokta);

        if (Dosyalar[i].OkunanNokta != Dosyalar[i].Baslik.NOKTALAR)
        {
            printf("\n\tOkunan nokta sayisi [%d] basliktaki nokta sayisiyla [%d] uyusmuyor.\n", Dosyalar[i].OkunanNokta, Dosyalar[i].Baslik.NOKTALAR);
        }

        for(int i2=0;i2<Dosyalar[i].OkunanNokta;i2++)
        {
            printf("%d - ",i2);

            int boyut = Dosyalar[i].Baslik.ALANLAR==ALANTIP_XYZ ? 3 : 6;
            for(int j=0; j<boyut; j++)
            {
                if(j<3)
                {
                    printf("%.3f ",Dosyalar[i].Noktalar[i2*boyut+j]);
                }
                else
                {
                    printf("%d ",(int)Dosyalar[i].Noktalar[i2*boyut+j]);
                }
                
            }
            printf("\n");
        }
    }*/
       
    // kendimiz allocate ettiğimiz tüm belleği free liyoruz
    for (int i = 0; i < dosyaindex; i++)
    {
        free(Dosyalar[i].Noktalar);
    }
    free(Dosyalar);
    free(hatalar);

    closedir(dr);
    return 0;
}