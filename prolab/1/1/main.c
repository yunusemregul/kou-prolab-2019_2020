#include <stdio.h>
#include <stdlib.h>
#include <dirent.h>
#include <string.h>
#include <math.h>
#include <ctype.h>

struct DosyaBaslik
{
    int VERSION;
    int ALANLAR;
    int NOKTALAR;
    int DATA;
};

struct Dosya
{
    char Ad[128];
    struct DosyaBaslik Baslik;

    float *Noktalar;
    int OkunanNokta;
};

static char NOKTAVERILERI[6] = {
    'x', 'y', 'z', 'r', 'g', 'b'
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

static char ALANTIPLERI[2][12] = {
    "x y z",
    "x y z r g b"
};

static char DATATIPLERI[2][12] = {
    "ascii",
    "binary"
};

// fflush alternatifi
void clean_stdin(void)
{
    int c;
    do {
        c = getchar();
    } while (c != '\n' && c != EOF);
}

// bir karakterin uygun olup olmadigini döndüren fonksiyon
int isCharAllowed(int i)
{
    if(iscntrl((char)i)&&i!='\n')
        return 0;

    return 1;
}

// bir char arrayinin içinde uygunsuz karakter varmi diye kontrol eden fonksiyon
// uygunsuz karakter bulunca karakterin ascii degerini döndürür
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


// alan tipi veya data tipi okunabilecek alan/data tiplerinde var mi diye kontrol eden fonksiyon
int IsTipValid(char array[2][12], char *tip)
{
    for (int i = 0; i < 2; i++)
    {
        if (strcmp(tip, array[i]) == 0)
            return i; // return (bulunan tip)
    }

    return -1; // bulunmadi demektir
}


// dosyalardaki hatalari boyutu dinamik bir buffere ekleyen fonksiyon
void LogHata(char **hatalar, char *hata, int satir)
{
    // satir bilgisini koymak istemiyorsak, mesela binary dede satir olmadigi için
    // bu fonksiyona -1 gönderiyoruz
    if(satir!=-1)
    {
        if(strrchr(hata,'\n')!=NULL)
        {
            // son yeni satir karakterini silip satir ekliyoruz
            strcpy(strrchr(hata,'\n')," ");
            char buf[32];
            sprintf(buf,"[SATIR %d]\n",satir);
            strcat(hata,buf);
        }
    }

    // hatalar in boyutunu yeni eklenecek hata boyutunca artiriyoruz
    *hatalar = (char *)realloc(*hatalar,strlen(*hatalar)*sizeof(char)+strlen(hata)*sizeof(char)+1); // +1 bi sorunu çözüyor alt satira bozuk bytelar geliyor onu çözüyo
    if(*hatalar==NULL)
    {
        printf("'hatalar' icin realloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
        return;
    }
    
    // yeni gelen hatayi eski hatalarin sonuna ekliyoruz
    strcat(*hatalar,hata);
}

// output.nkt dosyasina kayit islemlerinden sorumlu fonksiyon
void Log(int secim, char *text, char dosya_adi[128])
{
    static int sonsecim = 0;

    FILE * output = fopen("output.nkt","a");

    if(output==NULL)
    {
        printf("output.nkt dosyasini olustururken hata.\n");
        return;
    }

    if(sonsecim!=secim)
    {
        fprintf(output,"SECIM %d\n",secim);
        sonsecim = secim;
    }

    if(dosya_adi!=NULL)
    {
        fprintf(output,"%s dosyasi\n",dosya_adi);
        printf("%s dosyasi\n",dosya_adi);
    }

    fprintf(output,"%s\n",text);

    printf("%s\n",text);

    fclose(output);
}

// sayisal nokta datasini verilen buffere string olarak yazan fonksiyon
void NoktaToString(float *nokta, int alantip, char buffer[128])
{
    memset(buffer,0,128);

    int alanboyut = (alantip == ALANTIP_XYZ ? 3 : 6);

    for (int i = 0; i < alanboyut; i++)
    {
        if (i < 3)
        {
            sprintf(buffer+strlen(buffer), "%.3f ", nokta[i]);
        }
        else
        {
            sprintf(buffer+strlen(buffer), "%d ", (int)nokta[i]);
        }
    }
}

// noktalar arasi mesafeyi hesaplayan fonksiyon
float NoktaMesafe(float a[3], float b[3])
{
    return sqrtf(powf(a[0]-b[0],2.0f)+powf(a[1]-b[1],2.0f)+powf(a[2]-b[2],2.0f));
}

void EnYakinEnUzak(struct Dosya dosya)
{
    // initial olarak 0. ve 1. noktayi en yakin olarak belirliyoruz
    // sonrasinda bu 2sinin mesafesinden kisa/uzun ise karsilastirilan herhangi 2 nokta
    // bunlari degistiricez basit en küçük/en büyük bulma mantigi
    int enyakin_noktalar[2];
    int enuzak_noktalar[2];

    enyakin_noktalar[0] = 0;
    enyakin_noktalar[1] = 1;

    // en yakinin tam tersi
    enuzak_noktalar[0] = 0;
    enuzak_noktalar[1] = 1;

    int alanboyut = (dosya.Baslik.ALANLAR == ALANTIP_XYZ ? 3 : 6);
    float *noktalar = dosya.Noktalar;

    float yakinlar = NoktaMesafe(&noktalar[enyakin_noktalar[0] * alanboyut], &noktalar[enyakin_noktalar[1] * alanboyut]);
    float uzaklar = NoktaMesafe(&noktalar[enuzak_noktalar[0] * alanboyut], &noktalar[enuzak_noktalar[1] * alanboyut]);

    for (int i = 0; i < dosya.OkunanNokta; i++)
    {
        for (int j = i + 1; j < dosya.OkunanNokta; j++)
        {
            float mesafe = NoktaMesafe(&noktalar[i * alanboyut], &noktalar[j * alanboyut]);

            if (mesafe < yakinlar)
            {
                enyakin_noktalar[0] = i;
                enyakin_noktalar[1] = j;

                yakinlar = NoktaMesafe(&noktalar[enyakin_noktalar[0] * alanboyut], &noktalar[enyakin_noktalar[1] * alanboyut]);
            }

            if (mesafe > uzaklar)
            {
                enuzak_noktalar[0] = i;
                enuzak_noktalar[1] = j;

                uzaklar = NoktaMesafe(&noktalar[enuzak_noktalar[0] * alanboyut], &noktalar[enuzak_noktalar[1] * alanboyut]);
            }
        }
    }

    // loglari goster, kaydet
    char buffer[128];
    NoktaToString(&noktalar[enyakin_noktalar[0] * alanboyut], dosya.Baslik.ALANLAR, buffer);
    Log(2, buffer, dosya.Ad);
    NoktaToString(&noktalar[enyakin_noktalar[1] * alanboyut], dosya.Baslik.ALANLAR, buffer);
    Log(2, buffer, NULL);

    NoktaToString(&noktalar[enuzak_noktalar[0] * alanboyut], dosya.Baslik.ALANLAR, buffer);
    Log(2, buffer, NULL);
    NoktaToString(&noktalar[enuzak_noktalar[1] * alanboyut], dosya.Baslik.ALANLAR, buffer);
    Log(2, buffer, NULL);
}

// dosyadaki noktalarin birbirine mesafesinin ortalamasini bulan fonksiyon
void Ortalama(struct Dosya dosya)
{
    int alanboyut = (dosya.Baslik.ALANLAR == ALANTIP_XYZ ? 3 : 6);
    float *noktalar = dosya.Noktalar;

    float ort = 0;
    int counter = 1;

    for (int i = 0; i < dosya.OkunanNokta; i++)
    {
        for (int j = i + 1; j < dosya.OkunanNokta; j++)
        {
            float mesafe = NoktaMesafe(&noktalar[i * alanboyut], &noktalar[j * alanboyut]);

            // iterative mean, iteratif ortalama algoritmasi
            ort = ort + (mesafe-ort)/counter;
            counter++;
        }
    }

    char buffer[32];
    sprintf(buffer, "%f", ort);
    Log(5, buffer, dosya.Ad);
}


//içine bütün noktalari alan en küçük küpün köse noktalarini bulan fonksiyon
void Kup(struct Dosya dosya)
{
    int alanboyut = (dosya.Baslik.ALANLAR==ALANTIP_XYZ ? 3 : 6);
    float *noktalar = dosya.Noktalar;

    // bunlar en küçük ve en büyük indisleri tutuyorlar
    // index 0 = en küçük
    // index 1 = en büyük
    float x[2] = {noktalar[0], noktalar[0]};
    float y[2] = {noktalar[1], noktalar[1]};
    float z[2] = {noktalar[2], noktalar[2]};

    // en küçük x, en büyük x.. değerlerini bul
    for(int i=0; i<dosya.OkunanNokta; i++)
    {
        float *nokta = &noktalar[i*alanboyut];

        // x
        if(nokta[0]<x[0])
            x[0] = nokta[0];
        if(nokta[0]>x[1])
            x[1] = nokta[0];

        // y
        if(nokta[1]<y[0])
            y[0] = nokta[1];
        if(nokta[1]>y[1])
            y[1] = nokta[1];

        // z
        if(nokta[2]<z[0])
            z[0] = nokta[2];
        if(nokta[2]>z[1])
            z[1] = nokta[2];
    }

    float xfark = x[1]-x[0];
    float yfark = y[1]-y[0];
    float zfark = z[1]-z[0];

    float xyz_enbuyukfark = xfark;
    int xyz_enbuyukfark_indis = 0; // 0 : x, 1 : y, 2 : z

    if(yfark>xyz_enbuyukfark)
        xyz_enbuyukfark_indis = 1;

    if(zfark>xyz_enbuyukfark)
        xyz_enbuyukfark_indis = 2;

    for(int i=0; i<3; i++)
    {
        if(i==xyz_enbuyukfark_indis)
            continue;

        if(i==0)
        {
            x[0] = x[0]-(xyz_enbuyukfark-xfark)/2;
            x[1] = x[1]+(xyz_enbuyukfark-xfark)/2;
        }
        if(i==1)
        {
            y[0] = y[0]-(xyz_enbuyukfark-yfark)/2;
            y[1] = y[1]+(xyz_enbuyukfark-yfark)/2;
        }
        if(i==2)
        {
            z[0] = z[0]-(xyz_enbuyukfark-zfark)/2;
            z[1] = z[1]+(xyz_enbuyukfark-zfark)/2;
        }
    }

    char buffer[64];
    sprintf(buffer,"%f %f %f",x[0],y[0],z[0]);
    Log(3,buffer,dosya.Ad);
    sprintf(buffer,"%f %f %f",x[0],y[0],z[1]);
    Log(3,buffer,NULL);
    sprintf(buffer,"%f %f %f",x[0],y[1],z[0]);
    Log(3,buffer,NULL);
    sprintf(buffer,"%f %f %f",x[0],y[1],z[1]);
    Log(3,buffer,NULL);
    sprintf(buffer,"%f %f %f",x[1],y[0],z[0]);
    Log(3,buffer,NULL);
    sprintf(buffer,"%f %f %f",x[1],y[0],z[1]);
    Log(3,buffer,NULL);
    sprintf(buffer,"%f %f %f",x[1],y[1],z[0]);
    Log(3,buffer,NULL);
    sprintf(buffer,"%f %f %f",x[1],y[1],z[1]);
    Log(3,buffer,NULL);
}

/*
    merkez x,y,z ve yariçap r bilgisi kullanicidan alinan kürenin içinde kalan
    noktalari bulan fonksiyon
*/
void Kure(struct Dosya dosya, float *xyz, float r)
{
    int alanboyut = (dosya.Baslik.ALANLAR==ALANTIP_XYZ ? 3 : 6);
    float *noktalar = dosya.Noktalar;

    int counter = 0;
    for(int i=0; i<dosya.OkunanNokta; i++)
    {
        if(NoktaMesafe(&noktalar[i*alanboyut],xyz)<r)
        {
            if(counter==0)
            {
                char buffer[128];
                sprintf(buffer,"ALANLAR %s\nNOKTALAR %d\nDATA %s",ALANTIPLERI[dosya.Baslik.ALANLAR],dosya.OkunanNokta,DATATIPLERI[dosya.Baslik.DATA]);
                Log(4,buffer,dosya.Ad);                
            }

            char buffer[128];
            NoktaToString(&noktalar[i*alanboyut],dosya.Baslik.ALANLAR,buffer);
            Log(4,buffer,NULL);
            counter++;
        }
    }
}

int main(void)
{
    // dosyalarin bilgilerini içerecek struct
    // boyutu dinamik
    struct Dosya *Dosyalar;
    // initial olarak rastgele bir boyut atiyoruz
    Dosyalar = (struct Dosya *)malloc(sizeof(struct Dosya));

    if(Dosyalar==NULL)
    {
        printf("'Dosyalar' icin malloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
        return 0;
    }

    // hatalari içerecek pointer
    char *hatalar = NULL;
    // initial olarak rastgele bir boyut atiyoruz
    hatalar = (char *)malloc(sizeof(char));
    memset(hatalar,0,strlen(hatalar));

    if(hatalar==NULL)
    {
        printf("'hatalar' icin malloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
        return 0;
    }

    // ayni klasordeki dosyalari yani directory leri tutacak pointer
    struct dirent *dosya_entry;

    char line[100];
    char *read;

    // ayni klasoru ac
    DIR *dir = opendir(".");

    if (dir == NULL)
    {
        printf("Programin calistigi klasor yolu acilamadi.");
        return 0;
    }

    // dosya sayisini tutan degiskenler
    int dosyaindex = 0;

    // tum dosyalar icin
    while ((dosya_entry = readdir(dir)) != NULL)
    {
        // dosya uzantisi
        char *file_extension = strrchr(dosya_entry->d_name, '.');

        if(strcmp(dosya_entry->d_name,"output.nkt")==0)
            continue;

        // dosya uzantisi .nkt mi diye kontrol ediyoruz
        if (file_extension != NULL && strcmp(file_extension, ".nkt") == 0)
        {
            // yeni bir dosya kaydina basladigimiz için dosyalar arrayinin boyutunu 1 arttiriyoruz
            Dosyalar = (struct Dosya *)realloc(Dosyalar, (dosyaindex + 1) * sizeof(struct Dosya));
            if(Dosyalar==NULL)
            {
                printf("'Dosyalar' icin realloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
                return 0;
            }

            strcpy(Dosyalar[dosyaindex].Ad, dosya_entry->d_name);

            // okunan dosyanin FILE pointeri
            FILE *file;
            file = fopen(dosya_entry->d_name, "r");
            if(file==NULL)
            {
                printf("'%s' dosyasi 'r' modunda acilamadi.\n",dosya_entry->d_name);
                return 0;
            }

            int satir = 0;
            int offset = 0;
            int noktacount = 0;

            // 4 satirlik baslik bilgisini bulup oku
            for (int i = 0; i < 4; i++)
            {
                read = fgets(line, 100, file);
                offset = offset + strlen(line);
                satir++;
                // eger baslik bilgisi yoksa
                if (read == NULL)
                {
                    char hatabuffer[400];
                    sprintf(hatabuffer, "[%s] dosyasinin baslik bilgisi hatali. [%s] bulunamadi.\n", dosya_entry->d_name, BASLIKTIPLERI[i]);
                    LogHata(&hatalar,hatabuffer,satir);
                    dosyaindex--;
                    goto end_reading;
                }
                // eger baslik yorum satiriysa
                // bosver burdan baslama okumaya
                else if (line[0] == '#')
                {
                    i--;
                    continue;
                }

                line[strlen(line)-1] = '\0';
                char ayrac[2] = " ";
                char *parca = strtok(line,ayrac);

                // baslik bilgisi beklenen siradan farkliysa
                if (strcmp(parca, BASLIKTIPLERI[i]) != 0)
                {
                    char hatabuffer[512];
                    sprintf(hatabuffer, "[%s] dosyasinin baslik bilgisi hatali. [%s] beklenirken [%s] okundu.\n", dosya_entry->d_name, BASLIKTIPLERI[i], line);
                    LogHata(&hatalar,hatabuffer,satir);
                    dosyaindex--;
                    goto end_reading;
                }
                else if(charControl(line)!=-1)
                {
                    char hatabuffer[400];
                    sprintf(hatabuffer, "[%s] dosyasinda uygun olmayan bir karakter okundu. Karakter kodu: [%d]\n", dosya_entry->d_name, charControl(line));
                    LogHata(&hatalar,hatabuffer,satir);
                    dosyaindex--;
                    goto end_reading;
                }

                // versiyon oku
                if (strcmp(parca, "VERSION") == 0)
                {
                    char * versiyonbilgisi = line + 8;

                    if(!isdigit(versiyonbilgisi[0]))
                    {
                        char hatabuffer[400];
                        sprintf(hatabuffer, "[%s] dosyasi icin VERSION bilgisi [%s] gecersiz. Sayi bekleniyordu.\n", dosya_entry->d_name, versiyonbilgisi);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }

                    int ver = atoi(versiyonbilgisi); // 'VERSION ' kismini geçip devamini okuyoruz
                    Dosyalar[dosyaindex].Baslik.VERSION = ver; // VERSION dan sonrasini int e çevir

                    if (ver != 1)
                    {
                        char hatabuffer[400];
                        sprintf(hatabuffer, "[%s] dosyasi icin VERSION bilgisi [%d] gecersiz. Sadece VERSION 1 dosyalar okunabilir.\n", dosya_entry->d_name, ver);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }
                }

                // alanlar oku
                if (strcmp(parca, "ALANLAR") == 0)
                {
                    char *alanbilgisi = line + 8; // 'ALANLAR ' kismini geçip devamini okuyoruz

                    int tip = IsTipValid(ALANTIPLERI, alanbilgisi);

                    if (tip == -1)
                    {
                        char hatabuffer[400];
                        sprintf(hatabuffer, "[%s] dosyasi icin ALAN bilgisi [%s] gecersiz.\n", dosya_entry->d_name, alanbilgisi);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }

                    Dosyalar[dosyaindex].Baslik.ALANLAR = tip;
                }

                // noktalar oku
                if (strcmp(parca, "NOKTALAR") == 0)
                {
                    char *noktalarbilgisi = line + 9; // 'NOKTALAR ' uzunlugu 9, baslangic+9 a gidiyoruz 'NOKTALAR ' i gecmek icin

                    if(!isdigit(noktalarbilgisi[0]))
                    {
                        char hatabuffer[400];
                        sprintf(hatabuffer, "[%s] dosyasi icin NOKTALAR bilgisi [%s] gecersiz. Sayi bekleniyordu.\n", dosya_entry->d_name, noktalarbilgisi);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }

                    int count = atoi(noktalarbilgisi);
                    Dosyalar[dosyaindex].Baslik.NOKTALAR = count;
                }

                // data oku
                if (strcmp(parca, "DATA")==0)
                {
                    char *databilgisi = line + 5; // noktalar dakinin benzeri

                    int tip = IsTipValid(DATATIPLERI, databilgisi);

                    if (tip == -1)
                    {
                        char hatabuffer[400];
                        sprintf(hatabuffer,"[%s] dosyasi icin DATA bilgisi [%s] gecersiz.\n", dosya_entry->d_name, databilgisi);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }

                    Dosyalar[dosyaindex].Baslik.DATA = tip;
                }
            }

            // baslik okumayi bitirdik, nokta okumaya basliyoruz

            // dosyanin noktalarini tutacak array için initial memory atamasi yapiyoruz
            Dosyalar[dosyaindex].Noktalar = (float*)malloc(sizeof(float));
            if(Dosyalar[dosyaindex].Noktalar==NULL)
            {
                printf("'Dosyalar[dosyaindex].Noktalar' icin malloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
                return 0;
            }

            // ascii seklinde oku
            if (Dosyalar[dosyaindex].Baslik.DATA == DATATIP_ASCII)
            {
                // satir satir okuyoruz
                while ((read = fgets(line, 100, file)) != NULL)
                {
                    satir++;
                    // eger satir yorum satiriysa bosver
                    if (line[0] == '#')
                        continue;

                    // noktalari okurken parçalar halinde okuyacagiz, parça ayraci ve suanki parçanin adresini tutan token
                    char ayrac[2] = " ";
                    char *token;

                    // ALAN tip XYZ oku
                    if (Dosyalar[dosyaindex].Baslik.ALANLAR == ALANTIP_XYZ)
                    {
                        // yeni nokta kaydi yapacagimiz için boyutu ona göre arttiriyoruz
                        Dosyalar[dosyaindex].Noktalar = (float*)realloc(Dosyalar[dosyaindex].Noktalar,sizeof(float)*3*(noktacount+1));
                        if(Dosyalar[dosyaindex].Noktalar==NULL)
                        {
                            printf("'Dosyalar[dosyaindex].Noktalar' icin realloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
                            return 0;
                        }

                        // arka arkaya 3 tane float okuyacagiz
                        // her floatin bitis noktasi space ile ayrildigi için
                        // strtok ile space ye göre ayirarak islem yapacagiz

                        token = strtok(line, ayrac);

                        // 3 adet float verisi okuyoruz
                        for (int i = 0; i < 3; i++)
                        {
                            if (token == NULL || (token!=NULL && !isdigit(token[0])))
                            {
                                char hatabuffer[400];
                                sprintf(hatabuffer,"[%s] isimli dosyada [%d] numarali noktada [%c] bilgisi bulunamadi.\n", dosya_entry->d_name, noktacount + 1, toupper(NOKTAVERILERI[i]));
                                LogHata(&hatalar,hatabuffer,satir);
                                dosyaindex--;
                                goto end_reading;
                            }

                            // 3 lü 3 lü artan bir nokta arrayi oldugu için suanki offseti bulmak için
                            // noktacount*3 yapiyoruz ve x y z degerleri içinde i ekliyoruz
                            Dosyalar[dosyaindex].Noktalar[noktacount*3+i] = atof(token);

                            token = strtok(NULL, ayrac);
                        }
                    }
                    //  ALAN tip XYZRGB oku
                    else if (Dosyalar[dosyaindex].Baslik.ALANLAR == ALANTIP_XYZRGB)
                    {
                        // yeni nokta kaydi yapacagimiz için boyutu ona göre arttiriyoruz
                        Dosyalar[dosyaindex].Noktalar = (float*)realloc(Dosyalar[dosyaindex].Noktalar,sizeof(float)*6*(noktacount+1));
                        if(Dosyalar[dosyaindex].Noktalar==NULL)
                        {
                            printf("'Dosyalar[dosyaindex].Noktalar' icin realloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
                            return 0;
                        }

                        // arka arkaya 6 tane float okuyacagiz (rgb leride kolay saklanmasi icin float okuyorum)
                        // her floatin bitis noktasi space ile ayrildigi için
                        // strtok ile space ye göre ayirarak islem yapacagiz

                        token = strtok(line, ayrac);

                        // 6 adet float verisi okuyoruz
                        for (int i = 0; i < 6; i++)
                        {
                            if (token == NULL || (token!=NULL && !isdigit(token[0])))
                            {
                                char hatabuffer[400];
                                sprintf(hatabuffer,"[%s] isimli dosyada [%d] numarali noktada [%c] bilgisi bulunamadi.\n", dosya_entry->d_name, noktacount + 1, toupper(NOKTAVERILERI[i]));
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
                    char hatabuffer[400];
                    sprintf(hatabuffer,"[%s] isimli dosyadan okunan nokta sayisi [%d] dosya basligindaki nokta sayisiyla [%d] uyusmuyor.\n", dosya_entry->d_name, noktacount, Dosyalar[dosyaindex].Baslik.NOKTALAR);
                    LogHata(&hatalar,hatabuffer,-1);
                    dosyaindex--;
                    goto end_reading;
                }
                Dosyalar[dosyaindex].OkunanNokta = noktacount;

            }
            // binary seklinde oku
            else if (Dosyalar[dosyaindex].Baslik.DATA == DATATIP_BINARY)
            {
                // dosyayi kapatip binary olarak tekrar ac
                fclose(file);
                file = fopen(dosya_entry->d_name,"rb");

                if(file==NULL)
                {
                    printf("'%s' dosyasi 'rb' modunda acilamadi.\n",dosya_entry->d_name);
                    return 0;
                }

                // dosyadaki binary datanin basladigi yere yani basligin bittigi yere gidiyoruz
                fseek(file,offset,SEEK_SET);

                float xyz[3];

                int rgb[3];

                // dosyadan veri okunabildigi surece oku
                while(fread((void *)(&xyz),sizeof(xyz),1,file))
                {
                    int alanboyut = (Dosyalar[dosyaindex].Baslik.ALANLAR==ALANTIP_XYZ ? 3 : 6);
                    Dosyalar[dosyaindex].Noktalar = (float*)realloc(Dosyalar[dosyaindex].Noktalar,sizeof(float)*alanboyut*(noktacount+1));

                    // ALAN tip XYZ oku
                    if (Dosyalar[dosyaindex].Baslik.ALANLAR == ALANTIP_XYZ)
                    {
                        // geçici olarak xyz ye kaydettigimizi asil yerine tasiyoruz
                        memcpy(&Dosyalar[dosyaindex].Noktalar[noktacount*3],xyz,sizeof(xyz));
                    }
                    //  ALAN tip XYZRGB oku
                    else if (Dosyalar[dosyaindex].Baslik.ALANLAR == ALANTIP_XYZRGB)
                    {
                        // rgb için 3 int daha okuyoruz
                        memcpy(&Dosyalar[dosyaindex].Noktalar[noktacount*6],xyz,sizeof(xyz));
                        fread((void *)&rgb,sizeof(rgb),1,file);

                        // birlikte saklamak daha kolay oldugu için int rgb yi de float a cast ediyoruz ve xyz ile birlikte sakliyoruz
                        for(int i=0; i<3; i++)
                        {
                            if(rgb[i]>255 || rgb[i]<0)
                            {
                                char hatabuffer[400];
                                sprintf(hatabuffer,"[%s] isimli dosyada [%d] numarali noktanin [%c] bilgisinde hata. Degeri 255 den buyuk, 0 dan kucuk olamaz.\n", dosya_entry->d_name, noktacount+1, toupper(NOKTAVERILERI[3+i]));
                                LogHata(&hatalar,hatabuffer,-1);
                                dosyaindex--;
                                goto end_reading;
                            }
                            Dosyalar[dosyaindex].Noktalar[noktacount*6+3+i] = (float)rgb[i];
                        }
                    }

                    noktacount++;
                }

                // dosyadaki nokta sayisini basliktakiyle kontrol ediyoruz
                if(noktacount!=Dosyalar[dosyaindex].Baslik.NOKTALAR)
                {
                    char hatabuffer[400];
                    sprintf(hatabuffer,"[%s] isimli dosyadan okunan nokta sayisi [%d] dosya basligindaki nokta sayisiyla [%d] uyusmuyor.\n", dosya_entry->d_name, noktacount, Dosyalar[dosyaindex].Baslik.NOKTALAR);
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

    int secim = 0;
    // input
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
                    char buffer[256];
                    if(dosyaindex==0)
                    {
                        char current_directory[128];
                        getcwd(current_directory, 128);

                        sprintf(buffer,"Programin calistigi dizinde [%s] hic .nkt dosyasi bulunamadi.", current_directory);
                    }
                    else
                    {
                        sprintf(buffer,"Tum dosyalar [%d dosya] uyumludur.",dosyaindex);
                    }
                    Log(1,buffer,NULL);
                }
                else
                {
                    char buffer[256];
                    sprintf(buffer,"%s",hatalar);

                    if(dosyaindex>0)
                    {
                        char alterbuffer[64];
                        sprintf(alterbuffer,"Geriye kalan [%d] dosya uyumludur.",dosyaindex);
                        strcat(buffer,alterbuffer);
                    }
                    Log(1,buffer,NULL);
                }
                break;
            }
            case 2:
            {
                if(dosyaindex==0)
                {
                    printf("Hic dosya yok ya da tum dosyalar hatali.\n");
                    break;
                }
                for(int i=0; i<dosyaindex; i++)
                {
                    EnYakinEnUzak(Dosyalar[i]);
                }
                break;
            }
            case 3:
            {
                if(dosyaindex==0)
                {
                    printf("Hic dosya yok ya da tum dosyalar hatali.\n");
                    break;
                }
                for(int i=0; i<dosyaindex; i++)
                {
                    Kup(Dosyalar[i]);
                }
                break;
            }
            case 4:
            {
                if(dosyaindex==0)
                {
                    printf("Hic dosya yok ya da tum dosyalar hatali.\n");
                    break;
                }
                float cxyz[3], cr;

                printf("Kurenin X koordinatini girin: ");
                scanf("%f",&cxyz[0]);
                clean_stdin();
                printf("Kurenin Y koordinatini girin: ");
                scanf("%f",&cxyz[1]);
                clean_stdin();
                printf("Kurenin Z koordinatini girin: ");
                scanf("%f",&cxyz[2]);
                clean_stdin();

                printf("Kurenin yaricap bilgisini girin: ");
                scanf("%f",&cr);
                clean_stdin();

                char buffer[128];
                sprintf(buffer, "cx=%f cy=%f cz=%f cr=%f",cxyz[0],cxyz[1],cxyz[2],cr);
                Log(4, buffer, NULL);

                for(int i=0; i<dosyaindex; i++)
                {
                    Kure(Dosyalar[i],cxyz,cr);
                }
                break;
            }
            case 5:
            {
                if(dosyaindex==0)
                {
                    printf("Hic dosya yok ya da tum dosyalar hatali.\n");
                    break;
                }

                for(int i=0; i<dosyaindex; i++)
                {
                    Ortalama(Dosyalar[i]);
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

    // kendimiz allocate ettigimiz tüm bellegi free liyoruz
    for (int i = 0; i < dosyaindex; i++)
    {
        free(Dosyalar[i].Noktalar);
    }
    free(Dosyalar);
    free(hatalar);

    closedir(dir);

    return 0;
}
