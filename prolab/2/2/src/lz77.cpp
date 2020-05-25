#include "includes.h"

#define OFFSET_BITS 12
#define LENGTH_BITS 4

#define OFFSET_MASK ((1 << (OFFSET_BITS)) - 1) << LENGTH_BITS
#define LENGTH_MASK ((1 << (LENGTH_BITS)) - 1)

lz77_token::lz77_token()
{
    offset_length = 0;
    c = 0;
}

void lz77_token::set_offset(int offset)
{
    offset = (offset << LENGTH_BITS);
    offset = (offset & OFFSET_MASK); // safety
    offset_length = (offset_length & LENGTH_MASK); // clean the offset value
    offset_length = (offset_length | offset); // assign it
}

int lz77_token::get_offset()
{
    return ((offset_length & OFFSET_MASK) >> LENGTH_BITS);
}

void lz77_token::set_length(int length)
{
    length = (length & LENGTH_MASK); // safety
    offset_length = (offset_length & OFFSET_MASK); // clean the length value
    offset_length = (offset_length | length); // assign it
}

int lz77_token::get_length()
{
    return (offset_length & LENGTH_MASK);
}

// for debugging purposes
void lz77_token::print()
{
    printf("(%d,%d,'%c')",get_offset(), get_length(), c);
}

vector<lz77_token> lz77_encode(char* input)
{
    char *lookahead, *search;

    vector<lz77_token> encoded;

    for(lookahead = input; lookahead < (input + strlen(input)); lookahead++)
    {
        lz77_token* to_insert = new lz77_token();

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
                length++;
            }
            if(length>max_length)
            {
                max_length = length;
                offset = (lookahead-search);
            }
        }

        if((lookahead+max_length)>=(input + strlen(input)))
        {
            max_length = input + strlen(input) - lookahead - 1;
        }
        
        to_insert->set_length(max_length);
        to_insert->set_offset(offset);
        lookahead += max_length;
        to_insert->c = *lookahead;

        encoded.push_back(*to_insert);
    }

    return encoded;
}

int lz77_write(vector<lz77_token> encoded, FILE* f)
{
    int total_written_bytes = 0;

    for (int i = 0; i < encoded.size(); i++)
    {
        total_written_bytes += sizeof(encoded[i].offset_length) + sizeof(encoded[i].c);
        fwrite(&encoded[i].offset_length, sizeof(encoded[i].offset_length), 1, f);
        fwrite(&encoded[i].c, sizeof(encoded[i].c), 1, f);
    }

    return total_written_bytes;
}