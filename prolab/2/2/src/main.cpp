#include "includes.h"

// https://ysar.net/algoritma/lz77.html
char *file_read(FILE *f)
{
    char *content;
    fseek(f, 0, SEEK_END);
    int size = ftell(f);
    content = (char*)malloc(size);
    fseek(f, 0, SEEK_SET);
    fread(content, 1, size, f);
    return content;
}

int main(void)
{
    FILE *f;
    
    if(f=fopen("metin.txt","r"))
    {    
        char *test = file_read(f);
        
        printf("Dosya boyutu: %d byte\n",strlen(test));
        vector<lz77_token> lz77_encoded = lz77_encode(test);
        
        int lz77_encoded_size;

        if(f=fopen("lz77_encoded.bin","wb+"))
        {
            lz77_encoded_size = lz77_write(lz77_encoded, f);
            fclose(f);
        }
        else
        {
            return 1;
        }
        

        printf("LZ77 encoded boyut: %d byte\n",lz77_encoded_size);

        vector<lzss_token> lzss_encoded = lzss_encode(test);

        int lzss_encoded_size;

        if(f=fopen("lzss_encoded.bin","wb+"))
        {
            lzss_encoded_size = lzss_write(lzss_encoded, f);
            fclose(f);
        }
        else
        {
            return 1;
        }
        

        printf("LZSS encoded boyut: %d byte\n",lzss_encoded_size);
        printf("\n\n");
    }
    else
    {
        printf("metin.txt dosyasi acilamadi!\n");
        return 1;
    }
}