#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(void)
{
    char input[120];
    printf("input: ");
    fgets(input, 120, stdin);

    for (int i = 0; i < strlen(input); i++)
    {
        if(input[i]=='\n')
            input[i] = 0;
    }

    printf("input: '%s'\n",input);

    for (int i = 0; i < strlen(input); i++)
    {
        int offset = 0;
        int length = 0;
        char ch = input[i];
        for (int j = i-1; j >= 0; j--)
        {
            for (int k = j; input[k]==input[i+(k-j)]; k++)
            {
                offset = j-i;
                length = k-j+1;
                ch = input[i+(k-j)+1];
            }
        }
        i += length;
        printf("(%d,%d,'%c'),",-offset,length,ch);
    }
}