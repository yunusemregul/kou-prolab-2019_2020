#include <stdio.h>
#include <stdlib.h>
#include <dirent.h>
#include <string.h>
#include <sys/types.h>

/*
    sources:
        * find all files in a folder
            https://www.geeksforgeeks.org/c-program-list-files-sub-directories-directory/
        
        * find file extension
            https://stackoverflow.com/a/5309514/8993088
        
        * read file line by line
            https://stackoverflow.com/a/3501681/8993088
*/

/*
    TO LEARN:  
        strtok, strrchr, pointer ->, dirent, readdir
*/

/*
    TO DO:
        remove saving r g b to float array since they are not used. they are needed only when checking errors
        save error to Dosya struct as char array
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
    **hatalar yapıyoruz çünkü gelen *hatalar pointer değeri referans olduğu için
    ve referans üzerinde yapılan değişikler(realloc,memcpy) bu scope un dışına çıkamayacağı için
    pointerin pointeri ni kullanmak zorundayız.
*/
void LogHata(char **hatalar, const char *hata)
{
    // hatalar ın boyutunu yeni eklenecek hata boyutunca artırıyoruz
    *hatalar = (char *)realloc(*hatalar,strlen(*hatalar)*sizeof(char)+strlen(hata)*sizeof(char));
    // yeni gelen hatayı eski hataların sonuna ekliyoruz
    memcpy(*hatalar+strlen(*hatalar)*sizeof(char),hata,strlen(hata));
}

int main(void)
{
    // dosyaların bilgilerini içerecek struct
    // boyutu dinamik
    struct Dosya *Dosyalar;
    Dosyalar = (struct Dosya *)malloc(sizeof(struct Dosya));

    char *hatalar = NULL;
    hatalar = (char *)malloc(sizeof(char));
   
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
            strcpy(&Dosyalar[dosyaindex].Ad, de->d_name);

            // okunan dosyanın FILE pointeri
            FILE *file;
            file = fopen(de->d_name, "r");

            // 4 satırlık başlık bilgisini bulup oku
            for (int i = 0; i < 4; i++)
            {
                read = getline(&line, &len, file);

                // eğer başlık bilgisi yoksa
                if (read == -1)
                {
                    char hatabuffer[128];
                    sprintf(hatabuffer, "[%s] dosyasinin baslik bilgisi hatali. [%s] bulunamadi.\n", de->d_name, BASLIKTIPLERI[i]);
                    LogHata(&hatalar,hatabuffer);
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
                    LogHata(&hatalar,hatabuffer);
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
                        LogHata(&hatalar,hatabuffer);
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
                        LogHata(&hatalar,hatabuffer);
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
                        LogHata(&hatalar,hatabuffer);
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

            // ascii şeklinde oku
            if (Dosyalar[dosyaindex].Baslik.DATA == DATATIP_ASCII)
            {
                // satır satır okuyoruz
                while ((read = getline(&line, &len, file)) != -1)
                {
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
                                LogHata(&hatalar,hatabuffer);
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
                                LogHata(&hatalar,hatabuffer);
                                dosyaindex--;
                                goto end_reading;
                            }

                            // 6 lı 6 lı artan bir nokta arrayı olduğu için şuanki offseti bulmak için
                            // noktacount*6 yapıyoruz ve x y z r g b değerleri içinde i ekliyoruz
                            Dosyalar[dosyaindex].Noktalar[noktacount*6+i] = atof(token);    

                            token = strtok(NULL, ayrac);
                        }
                    }

                    noktacount++;
                }
                Dosyalar[dosyaindex].OkunanNokta = noktacount;
            }
            // binary şeklinde oku
            else if (Dosyalar[dosyaindex].Baslik.DATA == DATATIP_BINARY)
            {
            }

            end_reading:
            fclose(file);

            dosyaindex++;
        }
    }

    while(1)
    {
        int secim;
        printf("Secim yapiniz: ");
        scanf("%d",&secim);

        switch(secim)
        {
            case 1:
            {
                printf("%s\n",hatalar);
                break;
            }
        }
    }

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
            for(int j=0; j<(Dosyalar[i].Baslik.ALANLAR==ALANTIP_XYZ ? 3 : 6); j++)
            {
                printf("%.3f ",Dosyalar[i].Noktalar[i2*(Dosyalar[i].Baslik.ALANLAR==ALANTIP_XYZ ? 3 : 6)+j]);
            }
            printf("\n");
        }
    }*/

    for (int i = 0; i < dosyaindex; i++)
    {
        free(Dosyalar[i].Noktalar);
    }
    free(Dosyalar);

    closedir(dr);
    return 0;
}