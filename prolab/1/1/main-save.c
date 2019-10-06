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
            https://www.stackoverflow.com/questions/5309471/getting-file-extension-in-c
            http://www.cplusplus.com/reference/cstring/strrchr/
        
        * read file line by line
            https://stackoverflow.com/questions/3501338/c-read-file-line-by-line
*/

// nokta
struct Nokta
{
    float x;
    float y;
    float z;

    float r;
    float g;
    float b;
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
    struct DosyaBaslik Baslik;
    struct Nokta *Noktalar;
};

static char BASLIKTIPLERI[4][12] = {
    "VERSION",
    "ALANLAR",
    "NOKTALAR",
    "DATA"
};

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

/*static char ALANTIPLERI[2][16] = {
    "x y z",
    "x y z r g b"
};*/

/*static char DATATIPLERI[2][8] = {
    "ascii",
    "binary"
};*/

int IsTipValid(int size, int innersize, char * array, char * tip)
{
    for (int i=0; i<size; i++)
    {
        if(strcmp(tip,array+1*innersize)==0)
            return 1;
    }

    return 0;
}

int main(void)
{
    // dosyaların bilgilerini içerecek struct
    // boyutu dinamik
    struct Dosya *Dosyalar;
    Dosyalar = (struct Dosya *)malloc(sizeof(struct Dosya));


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

        // kontrol edilecek dosya uzantısı
        char ext[5] = ".nkt";

        // dosya uzantısı .nkt mı diye kontrol ediyoruz
        if (file_extension != NULL && strcmp(file_extension, ext) == 0)
        {
            // yeni bir dosya kaydına başladığımız için dosyalar arrayının boyutunu 1 arttırıyoruz
            Dosyalar = (struct Dosya *)realloc(Dosyalar, (dosyaindex + 1) * sizeof(struct Dosya));

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
                    printf("[%s] dosyasinin baslik bilgisi hatali. [%s] bulunamadi.\n", de->d_name, BASLIKTIPLERI[i]);
                    dosyaindex--;
                    break;
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
                    printf("[%s] dosyasinin baslik bilgisi hatali. [%s] beklenirken [%s] okundu.\n", de->d_name, BASLIKTIPLERI[i], line);
                    dosyaindex--;
                    break;
                }

                // versiyon oku
                if (strcmp(line, "VERSION") == ' ')
                {
                    int ver = atoi(line + 8);
                    Dosyalar[dosyaindex].Baslik.VERSION = ver; // VERSION dan sonrasını int e çevir

                    if (ver != 1)
                    {
                        printf("[%s] dosyasi icin VERSION bilgisi [%d] gecersiz. Sadece VERSION 1 dosyalar okunabilir.\n", de->d_name, ver);
                        dosyaindex--;
                        break;
                    }
                }

                // alanlar oku
                if (strcmp(line, "ALANLAR") == ' ')
                {
                    //strcpy(&Dosyalar[dosyaindex].Baslik.ALANLAR, line + 8);
                    //printf("%X\t%X\n",line,alan);

                    /*if(!IsTipValid(2,16,&ALANTIPLERI,line + 8))
                    {
                        printf("[%s] dosyasi icin ALAN bilgisi [%s] gecersiz.\n", de->d_name, line+8);
                        dosyaindex--;
                        break;                        
                    }*/
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
                    //strcpy(&Dosyalar[dosyaindex].Baslik.DATA, line + 4);
                   // char *databilgisi = line+9;
                    //printf("%s",databilgisi);
                }
            }

            int noktacount = 0;
            // başlık okumayı bitirdik, data okumaya başlıyoruz
            while ((read = getline(&line, &len, file)) != -1)
            {
                if (line[0] == '#')
                    continue;

                noktacount++;
            }

            if(de->d_name,Dosyalar[dosyaindex].Baslik.NOKTALAR!=noktacount)
            {
                printf("[%s] dosyasindan okunan nokta sayisi [%d], okunmasi beklenen nokta sayisina [%d] esit degil.\n",de->d_name,noktacount,Dosyalar[dosyaindex].Baslik.NOKTALAR);
            }

            fclose(file);

            dosyaindex++;
        }
    }

   /* for (int i = 0; i < dosyaindex; i++)
    {
        printf("DOSYA %d:\n", i + 1);
        printf("\tVERSION: %d", Dosyalar[i].Baslik.VERSION);
        printf("\tALANLAR: %s", Dosyalar[i].Baslik.ALANLAR);
        printf("\tNOKTALAR: %d", Dosyalar[i].Baslik.NOKTALAR);
        printf("\tDATA: %s", Dosyalar[i].Baslik.DATA);
    }*/

    free(Dosyalar);
    closedir(dr);
    return 0;
}