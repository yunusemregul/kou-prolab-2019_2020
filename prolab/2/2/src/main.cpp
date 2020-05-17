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
        
        printf("File size: %d bytes\n",strlen(test));
        vector<lz77_token> encoded = lz77_encode(test);
        printf("LZ77 encoded size: %d bytes\n",encoded.size()*2);

        if(f=fopen("encoded.bin","wb"))
        {
            fwrite(&encoded[0], sizeof(lz77_token), encoded.size(), f);
            fclose(f);
        }
        
        printf("\n\n");
    }
}