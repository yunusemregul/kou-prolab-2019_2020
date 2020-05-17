#include "includes.h"

#define OFFSET_BITS 5
#define LENGTH_BITS 3

#define OFFSET_MASK ((1 << (OFFSET_BITS)) - 1) << LENGTH_BITS
#define LENGTH_MASK ((1 << (LENGTH_BITS)) - 1)

class token
{
    public:
        uint8_t offset_length;
        char c;

        token()
        {
            offset_length = 0;
            c = 0;
        }

        void set_offset(int offset)
        {
            offset = (offset << LENGTH_BITS);
            offset = (offset & OFFSET_MASK); // safety
            offset_length = (offset_length & LENGTH_MASK); // clean the offset value
            offset_length = (offset_length | offset); // assign it
        }

        int get_offset()
        {
            return ((offset_length & OFFSET_MASK) >> LENGTH_BITS);
        }

        void set_length(int length)
        {
            length = (length & LENGTH_MASK); // safety
            offset_length = (offset_length & OFFSET_MASK); // clean the length value
            offset_length = (offset_length | length); // assign it
        }

        int get_length()
        {
            return (offset_length & LENGTH_MASK);
        }

        // for debugging purposes
        void print()
        {
            printf("(%d,%d,'%c')",get_offset(), get_length(), c);
        }
};

vector<token> encode(char* input)
{
    char *lookahead, *search;

    vector<token> encoded;

    for(lookahead = input; lookahead < (input + strlen(input)); lookahead++)
    {
        token* to_insert = new token();

        int max_length = 0;
        int offset = 0;

        for(search = lookahead-1; search>=input; search--)
        {
            if((lookahead-search)>=(OFFSET_MASK>>LENGTH_BITS))
                break;

            int length = 0;
            for(char* match=search; *match==*(lookahead+(match-search)); match++)
            {
                if((match-search)>=LENGTH_MASK)
                    break;
                if((lookahead+length+1)>=(input + strlen(input)))
                    break;
                length++;
            }
            if(length>max_length)
            {
                max_length = length;
                offset = (lookahead-search);
            }
        }

        to_insert->set_length(max_length);
        to_insert->set_offset(offset);
        lookahead += max_length;
        to_insert->c = *lookahead;

        encoded.push_back(*to_insert);
    }

    return encoded;
}

vector<char> decode(vector<token> encoded)
{
    vector<char> decoded;

    for (token t:encoded)
    {
        if(t.get_offset()==0)
            decoded.push_back(t.c);
        else
        {
            int len = t.get_length();

            while(len--)
            {
                decoded.push_back(*(decoded.end()-t.get_offset()));
            }

            decoded.push_back(t.c);
        }
    }

    return decoded;
}

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
        vector<token> encoded = encode(test);
        printf("LZ77 encoded size: %d bytes\n",encoded.size()*2);

        if(f=fopen("encoded.bin","wb"))
        {
            fwrite(&encoded[0], sizeof(token), encoded.size(), f);
            fclose(f);
        }
        
        printf("\n\n");
    }
}