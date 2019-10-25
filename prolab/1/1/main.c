#include <stdio.h>
#include <stdlib.h>
#include <dirent.h>
#include <string.h>
#include <sys/types.h>
#include <math.h>
#include <ctype.h>

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
       delete // debug *
*/

/*
    TO LEARN:  
        strtok, strrchr, pointer ->, dirent, readdir
*/

void clean_stdin(void)
{
    int c;
    do {
        c = getchar();
    } while (c != '\n' && c != EOF);
}

// bir karakterin uygun olup olmadığını döndüren fonksiyon
int isCharAllowed(int i)
{
    // \n hariç kontrol karakterlerine izin vermiyoruz \r koyup sapıttırıyolar kodu
    if(iscntrl((char)i)&&i!='\n')
        return 0;

    return 1;
}

// bir char arrayının içinde uygunsuz karakter varmı diye kontrol eden fonksiyon
// uygunsuz karakter bulunca karakterin ascii değerini döndürür
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

    float *Noktalar;
    int OkunanNokta;
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
*/
int IsTipValid(int size, int innersize, char array[size][innersize], char *tip)
{
    for (int i = 0; i < size; i++)
    {
        if (strcmp(tip, array[i]) == 0)
            return i; // return (bulunan tip)
    }

    return -1; // bulunmadı demektir
}

/*
    dosyalardaki hataları boyutu dinamik bir buffere ekleyen fonksiyon

    **hatalar yapıyoruz çünkü gelen *hatalar pointer değeri referans olduğu için
    ve referans üzerinde yapılan değişiklikler (realloc,strcat) bu scope un dışına çıkamayacağı için
    pointerin pointeri ni kullanmak zorundayız.
*/
void LogHata(char **hatalar, char *hata, int satir)
{
    // satır bilgisini koymak istemiyorsak, mesela binary de satır olmadığı için
    // bu fonksiyona -1 gönderiyoruz
    if(satir!=-1)
    {
        if(strrchr(hata,'\n')!=NULL)
        {
            // son yeni satır karakterini silip satır ekliyoruz
            strcpy(strrchr(hata,'\n')," ");
            char buf[32];
            sprintf(buf,"[SATIR %d]",satir);
            strcat(hata,buf);            
        }
    }
    
    // hatalar ın boyutunu yeni eklenecek hata boyutunca artırıyoruz
    *hatalar = (char *)realloc(*hatalar,strlen(*hatalar)*sizeof(char)+strlen(hata)*sizeof(char)+1); // +1 bi sorunu çözüyor alt satıra bozuk bytelar geliyor onu çözüyo
    if(*hatalar==NULL)
    {
        printf("'hatalar' icin realloc yapilirken hata olustu. Bellek yetersiz olabilir.\n");
        return;
    }  
    // yeni gelen hatayı eski hataların sonuna ekliyoruz
    strcat(*hatalar,hata);
    //printf("hatalar: %x\n",hatalar);
}

/*
    output.nkt dosyasına kaydeder
*/
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
        fprintf(output,"%s dosyası\n",dosya_adi);
        printf("%s dosyası\n",dosya_adi);
    }

    fprintf(output,"%s\n",text);

    printf("%s\n",text);

    fclose(output);
}

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

// noktalar arası mesafeyi hesaplayan fonksiyon
float NoktaMesafe(float a[3], float b[3])
{
    return sqrtf(powf(a[0]-b[0],2.0f)+powf(a[1]-b[1],2.0f)+powf(a[2]-b[2],2.0f));
}

void EnYakinEnUzak(float noktalar[], int alantip, int count, int *enyakinlar_buffer, int *enuzaklar_buffer)
{
    // initial olarak 0. ve 1. noktayı en yakın olarak belirliyoruz
    // sonrasında bu 2sinin mesafesinden kısa/uzun ise karşılaştırılan herhangi 2 nokta
    // bunları değiştiricez basit en küçük/en büyük bulma mantığı
    enyakinlar_buffer[0] = 0;
    enyakinlar_buffer[1] = 1;

    // en yakının tam tersi
    enuzaklar_buffer[0] = 0;
    enuzaklar_buffer[1] = 1;

    int alanboyut = (alantip==ALANTIP_XYZ ? 3 : 6);

    float yakinlar = NoktaMesafe(&noktalar[enyakinlar_buffer[0]*alanboyut],&noktalar[enyakinlar_buffer[1]*alanboyut]);
    float uzaklar = NoktaMesafe(&noktalar[enuzaklar_buffer[0]*alanboyut],&noktalar[enuzaklar_buffer[1]*alanboyut]);

    for(int i=0; i<count; i++)
    {
        for(int j=i+1; j<count; j++)
        {
            float mesafe = NoktaMesafe(&noktalar[i*alanboyut],&noktalar[j*alanboyut]);

            if(mesafe<yakinlar)
            {
                enyakinlar_buffer[0] = i;
                enyakinlar_buffer[1] = j;

                yakinlar = NoktaMesafe(&noktalar[enyakinlar_buffer[0]*alanboyut],&noktalar[enyakinlar_buffer[1]*alanboyut]);
            }      

            if(mesafe>uzaklar)
            {
                enuzaklar_buffer[0] = i;
                enuzaklar_buffer[1] = j;

                uzaklar = NoktaMesafe(&noktalar[enuzaklar_buffer[0]*alanboyut],&noktalar[enuzaklar_buffer[1]*alanboyut]);
            }      
        }
    }
}

float Ortalama(float noktalar[], int alantip, int count)
{
    int alanboyut = (alantip==ALANTIP_XYZ ? 3 : 6);

    float ort = 0;
    int counter = 0;

    for(int i=0; i<count; i++)
    {
        for(int j=i+1; j<count; j++)
        {
            float mesafe = NoktaMesafe(&noktalar[i*alanboyut],&noktalar[j*alanboyut]);

            ort = ort + mesafe;
            counter++;
        }
    }
    ort = ort/counter;

    return ort;
}

/*
    içine bütün noktaları alan en küçük küpün köşe noktalarını bulan fonksiyon
*/
void Kup(float noktalar[], int alantip, int count)
{
    int alanboyut = (alantip==ALANTIP_XYZ ? 3 : 6);

    // bunlar en küçük ve en büyük indisleri tutuyorlar
    // index 0 = en küçük
    // index 1 = en büyük
    float x[2] = {noktalar[0], noktalar[0]}; 
    float y[2] = {noktalar[1], noktalar[1]};
    float z[2] = {noktalar[2], noktalar[2]};

    for(int i=0; i<count; i++)
    {
        float *nokta = &noktalar[i*alanboyut];
        
        // en küçük en büyükleri kontrol edip
        // daha küçük ya da daha büyükse atama yapıyor
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
    
    // debug printf("%f %f %f %f\n",xyz_enbuyukfark,xfark,yfark,zfark);
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

    printf("%f %f %f\n",x[0],y[0],z[0]);
    printf("%f %f %f\n",x[0],y[0],z[1]);
    printf("%f %f %f\n",x[0],y[1],z[0]);
    printf("%f %f %f\n",x[0],y[1],z[1]);
    printf("%f %f %f\n",x[1],y[0],z[0]);
    printf("%f %f %f\n",x[1],y[0],z[1]);
    printf("%f %f %f\n",x[1],y[1],z[0]);
    printf("%f %f %f\n",x[1],y[1],z[1]);
}

/*
    merkez x,y,z ve yarıçap r bilgisi kullanıcıdan alınan kürenin içinde kalan
    noktaları bulan fonksiyon

    return olarak kaç tane bulduğunu döndürüyor

    (*to_write)[] yapıyoruz çünkü [] operatörünün önceliği * operatörünün önceliğinden fazla o yüzden hatalı oluyor
*/
int Kure(float noktalar[], int alantip, int count, float *xyz, float r, int **to_write)
{
    int alanboyut = (alantip==ALANTIP_XYZ ? 3 : 6);

    int counter = 0;
    for(int i=0; i<count; i++)
    {
        // debug printf("%d. nokta mesafesi : %f, icerde olmasi icin: %f\n",i,NoktaMesafe(&noktalar[i*alanboyut],xyz),NoktaMesafe(&noktalar[i*alanboyut],xyz)-r);
        if(NoktaMesafe(&noktalar[i*alanboyut],xyz)<r)
        {
            // debug printf("%d icerde!\n",i);
            *to_write = realloc(*to_write,sizeof(int)*(counter+1));

            (*to_write)[counter] = i;
            
            // debug printf("towr %d = %d | %d\n",counter,(*to_write)[counter],sizeof(int)*(counter+1));
            counter++;
        }
    }

    return counter;
}

// main
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

        if(strcmp(de->d_name,"output.nkt")==0)
            continue;

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

            strcpy(Dosyalar[dosyaindex].Ad, de->d_name);

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
                    char hatabuffer[400];
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
                
                line[strlen(line)-1] = '\0';
                char ayrac[2] = " ";                
                char *parca = strtok(line,ayrac);                
                
                // başlık bilgisi beklenen sıradan farklıysa
                if (strcmp(parca, BASLIKTIPLERI[i]) != 0)
                {
                    char hatabuffer[400];
                    sprintf(hatabuffer, "[%s] dosyasinin baslik bilgisi hatali. [%s] beklenirken [%s] okundu.\n", de->d_name, BASLIKTIPLERI[i], line);
                    LogHata(&hatalar,hatabuffer,satir);
                    dosyaindex--;
                    goto end_reading;
                }
                else if(charControl(line)!=-1)
                {
                    char hatabuffer[400];
                    sprintf(hatabuffer, "[%s] dosyasinda uygun olmayan bir karakter okundu. Karakter kodu: [%d]\n", de->d_name, charControl(line));
                    LogHata(&hatalar,hatabuffer,satir);
                    dosyaindex--;
                    goto end_reading;                    
                }

                // debug printf("satir: %s\n",line);

                // versiyon oku
                if (strcmp(parca, "VERSION") == 0)
                {
                    char * versiyonbilgisi = line + 8;

                    if(!isdigit(versiyonbilgisi[0]))
                    {
                        char hatabuffer[400];
                        sprintf(hatabuffer, "[%s] dosyasi icin VERSION bilgisi [%s] gecersiz. Sayi bekleniyordu.\n", de->d_name, versiyonbilgisi);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }

                    int ver = atoi(versiyonbilgisi); // 'VERSION ' kısmını geçip devamını okuyoruz
                    Dosyalar[dosyaindex].Baslik.VERSION = ver; // VERSION dan sonrasını int e çevir

                    if (ver != 1)
                    {
                        char hatabuffer[400];
                        sprintf(hatabuffer, "[%s] dosyasi icin VERSION bilgisi [%d] gecersiz. Sadece VERSION 1 dosyalar okunabilir.\n", de->d_name, ver);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }
                }

                // alanlar oku
                if (strcmp(parca, "ALANLAR") == 0)
                {
                    char *alanbilgisi = line + 8; // 'ALANLAR ' kısmını geçip devamını okuyoruz

                    int tip = IsTipValid(2, 16, ALANTIPLERI, alanbilgisi);

                    if (tip == -1)
                    {
                        char hatabuffer[400];             
                        sprintf(hatabuffer, "[%s] dosyasi icin ALAN bilgisi [%s] gecersiz.\n", de->d_name, alanbilgisi);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }

                    Dosyalar[dosyaindex].Baslik.ALANLAR = tip;
                }

                // noktalar oku
                if (strcmp(parca, "NOKTALAR") == 0)
                {
                    char *noktalarbilgisi = line + 9;

                    if(!isdigit(noktalarbilgisi[0]))
                    {
                        char hatabuffer[400];
                        sprintf(hatabuffer, "[%s] dosyasi icin NOKTALAR bilgisi [%s] gecersiz. Sayi bekleniyordu.\n", de->d_name, noktalarbilgisi);
                        LogHata(&hatalar,hatabuffer,satir);
                        dosyaindex--;
                        goto end_reading;
                    }    

                    int count = atoi(noktalarbilgisi);
                    Dosyalar[dosyaindex].Baslik.NOKTALAR = count; // NOKTALAR dan sonrasını int e çevir
                    
                    // debug printf("hellooooo %d\n",count);
                }

                // debug printf("%d\n",Dosyalar[dosyaindex].Baslik.NOKTALAR);

                // data oku
                if (strcmp(parca, "DATA")==0)
                {
                    char *databilgisi = line + 5;

                    int tip = IsTipValid(2, 8, DATATIPLERI, databilgisi);

                    if (tip == -1)
                    {
                        char hatabuffer[400];
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
                            if (token == NULL || (token!=NULL && !isdigit(token[0])))
                            {
                                char hatabuffer[400];
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
                            if (token == NULL || (token!=NULL && !isdigit(token[0])))
                            {
                                char hatabuffer[400];
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
                    char hatabuffer[400];
                    sprintf(hatabuffer,"[%s] isimli dosyadan okunan nokta sayisi [%d] dosya basligindaki nokta sayisiyla [%d] uyusmuyor.\n", de->d_name, noktacount, Dosyalar[dosyaindex].Baslik.NOKTALAR);
                    LogHata(&hatalar,hatabuffer,-1);
                    dosyaindex--;
                    goto end_reading;
                }
                Dosyalar[dosyaindex].OkunanNokta = noktacount;

            }
            // binary şeklinde oku
            else if (Dosyalar[dosyaindex].Baslik.DATA == DATATIP_BINARY)
            {
                // bu 2 satırsız da çalışıyor ama çözemedim
                // https://stackoverflow.com/a/2174928/8993088
                fclose(file);
                file = fopen(de->d_name,"rb");

                if(file==NULL)
                {
                    printf("'%s' dosyasi 'rb' modunda acilamadi.\n",de->d_name);
                    return 0;
                }

                fseek(file,offset,SEEK_SET);
            
                // güncellemeyle floata çevirdiler ;)
                float xyz[3];

                int rgb[3];

                while(fread((void *)(&xyz),sizeof(xyz),1,file))
                {
                    int alanboyut = (Dosyalar[dosyaindex].Baslik.ALANLAR==ALANTIP_XYZ ? 3 : 6);
                    Dosyalar[dosyaindex].Noktalar = (float*)realloc(Dosyalar[dosyaindex].Noktalar,sizeof(float)*alanboyut*(noktacount+1));

                    // ALAN tip XYZ oku
                    if (Dosyalar[dosyaindex].Baslik.ALANLAR == ALANTIP_XYZ)
                    {
                        // geçici olarak xyz ye kaydettiğimizi asıl yerine taşıyoruz
                        memcpy(&Dosyalar[dosyaindex].Noktalar[noktacount*3],xyz,sizeof(xyz));
                    }
                    //  ALAN tip XYZRGB oku
                    else if (Dosyalar[dosyaindex].Baslik.ALANLAR == ALANTIP_XYZRGB)
                    {
                        // rgb için 3 int daha oku?
                        memcpy(&Dosyalar[dosyaindex].Noktalar[noktacount*6],xyz,sizeof(xyz));
                        fread((void *)&rgb,sizeof(rgb),1,file);

                        // birlikte saklamak daha kolay olduğu için int rgb yi de float a cast ediyoruz ve xyz ile birlikte saklıyoruz
                        for(int i=0; i<3; i++)
                        {
                            Dosyalar[dosyaindex].Noktalar[noktacount*6+3+i] = (float)rgb[i];
                        }
                    }
                    
                    noktacount++;
                }

                if(noktacount!=Dosyalar[dosyaindex].Baslik.NOKTALAR)
                {
                    char hatabuffer[400];
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
                    char buffer[256];
                    sprintf(buffer,"Tum dosyalar [%d dosya] uyumludur.",dosyaindex);
                    Log(1,buffer,NULL);
                }
                else
                {
                    char buffer[256];
                    sprintf(buffer,"%s",hatalar);
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
                    // indis tutuyorlar 2 şer nokta indisi
                    int enyakin_noktalar[2];
                    int enuzak_noktalar[2];

                    // debug
                    //int sure = 0;
                    //sure = time(NULL);
                    EnYakinEnUzak(Dosyalar[i].Noktalar,Dosyalar[i].Baslik.ALANLAR,Dosyalar[i].OkunanNokta, &enyakin_noktalar, &enuzak_noktalar);
                    // EnYakinNoktalar(Dosyalar[i].Noktalar,Dosyalar[i].Baslik.ALANLAR,Dosyalar[i].OkunanNokta, &enyakin_noktalar);
                    // debug
                    //printf("kisalar %d %d\n",enyakin_noktalar[0],enyakin_noktalar[1]);

                    //EnUzakNoktalar(Dosyalar[i].Noktalar,Dosyalar[i].Baslik.ALANLAR,Dosyalar[i].OkunanNokta, &enuzak_noktalar);
                    //printf("uzunlar %d %d\n",enuzak_noktalar[0],enuzak_noktalar[1]);
                    //printf("%d nokta icin aldigi zaman: %d\n",Dosyalar[i].OkunanNokta,(int)time(NULL)-sure);

                    int alanboyut = (Dosyalar[i].Baslik.ALANLAR==ALANTIP_XYZ ? 3 : 6);

                    char buffer[128];
                    NoktaToString(&Dosyalar[i].Noktalar[enyakin_noktalar[0]*alanboyut], Dosyalar[i].Baslik.ALANLAR, buffer);
                    Log(2, buffer, Dosyalar[i].Ad);
                    NoktaToString(&Dosyalar[i].Noktalar[enyakin_noktalar[1]*alanboyut], Dosyalar[i].Baslik.ALANLAR, buffer);
                    Log(2, buffer, NULL);

                    NoktaToString(&Dosyalar[i].Noktalar[enuzak_noktalar[0]*alanboyut], Dosyalar[i].Baslik.ALANLAR, buffer);
                    Log(2, buffer, NULL);
                    NoktaToString(&Dosyalar[i].Noktalar[enuzak_noktalar[1]*alanboyut], Dosyalar[i].Baslik.ALANLAR, buffer);
                    Log(2, buffer, NULL);
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
                    Kup(Dosyalar[i].Noktalar,Dosyalar[i].Baslik.ALANLAR,Dosyalar[i].OkunanNokta);
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
                    int *noktalar = NULL;
                    noktalar = malloc(sizeof(int));

                    int adet = Kure(Dosyalar[i].Noktalar,Dosyalar[i].Baslik.ALANLAR,Dosyalar[i].OkunanNokta,cxyz,cr,&noktalar);
                    int alanboyut = (Dosyalar[i].Baslik.ALANLAR==ALANTIP_XYZ ? 3 : 6);

                    for(int j=0; j<adet; j++)
                    {
                        NoktaToString(&Dosyalar[i].Noktalar[noktalar[j]*alanboyut],Dosyalar[i].Baslik.ALANLAR,buffer);
                        Log(4,buffer,j==0 ? Dosyalar[i].Ad : NULL);
                    }

                    /* debug
                    printf("adet: %d\n",adet);
                    for(int i=0; i<adet; i++)
                    {
                        printf("%d = %d\n",i+1,noktalar[i]);
                    }
                    */

                    free(noktalar);
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
                    char buffer[32];
                    sprintf(buffer, "%f", Ortalama(Dosyalar[i].Noktalar,Dosyalar[i].Baslik.ALANLAR,Dosyalar[i].OkunanNokta));

                    Log(5,buffer,Dosyalar[i].Ad);
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
    
    // debug
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