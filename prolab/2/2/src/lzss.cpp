#include "includes.h"

#define OFFSET_BITS 12
#define LENGTH_BITS 4

#define OFFSET_MASK ((1 << (OFFSET_BITS)) - 1) << LENGTH_BITS
#define LENGTH_MASK ((1 << (LENGTH_BITS)) - 1)

lzss_token::lzss_token()
{
    offset_length = 0;
    c = 0;
}

void lzss_token::set_offset(int offset)
{
    offset = (offset << LENGTH_BITS);
    offset = (offset & OFFSET_MASK); // safety
    offset_length = (offset_length & LENGTH_MASK); // clean the offset value
    offset_length = (offset_length | offset); // assign it
}

int lzss_token::get_offset()
{
    return ((offset_length & OFFSET_MASK) >> LENGTH_BITS);
}

void lzss_token::set_length(int length)
{
    length = (length & LENGTH_MASK); // safety
    offset_length = (offset_length & OFFSET_MASK); // clean the length value
    offset_length = (offset_length | length); // assign it
}

int lzss_token::get_length()
{
    return (offset_length & LENGTH_MASK);
}

void lzss_token::set_flag(int fl)
{
    fl = (fl & 1);
    flag = fl;
}

int lzss_token::get_flag()
{
    return flag;
}

// for debugging purposes
void lzss_token::print()
{
    if(get_flag())
    {
        printf("%d(%d,%d)",get_flag(),get_offset(), get_length());
    }
    else
    {
        printf("%d(%c)",get_flag(),c);
    }
}

vector<lzss_token> lzss_encode(char* input)
{
    char *lookahead, *search;

    vector<lzss_token> encoded;

    for(lookahead = input; lookahead < (input + strlen(input)); lookahead++)
    {
        lzss_token* to_insert = new lzss_token();

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
            if(length>3 && length>max_length)
            {
                max_length = length;
                offset = (lookahead-search);
            }
        }

        if((lookahead+max_length)>=(input + strlen(input)))
        {
            max_length = input + strlen(input) - lookahead - 1;
        }
        
        if(max_length==0)
        {
            to_insert->set_flag(0);
            to_insert->c = *lookahead;
        }
        else
        {
            to_insert->set_flag(1);
            to_insert->set_length(max_length);
            to_insert->set_offset(offset);
            lookahead += max_length - 1;
        }

        encoded.push_back(*to_insert);
    }

    return encoded;
}

int lzss_write(vector<lzss_token> encoded, FILE *f)
{
    // önce flag leri yazıyoruz ama 8in katı olacak şekilde yazıyoruz
    // bu yöntemin zararı en kötü ihtimalle 7 bit boşuna yazılmış olabilir ama başka yol yok

    int total_written_bytes = 0;
    int bytes_to_fit_flags = (encoded.size()+(encoded.size()%8!=0 ? (8-encoded.size()%8) : 0))/8;
    uint8_t flags[bytes_to_fit_flags] = {0};

    int flags_index = 0;
    for (int i = 0; i < encoded.size(); i++)
    {
        if(i!=0 && i%8==0)
        {
            flags_index++;
        }
        
        flags[flags_index] |= (encoded[i].flag << i%8);
    }
    
    for (int i = 0; i < bytes_to_fit_flags; i++)
    {
        fwrite(&flags[i], sizeof(uint8_t), 1, f);
        total_written_bytes += sizeof(uint8_t);
        for (int j = 0; j < 8; j++)
        {
            int encoded_index = i * 8 + j;
            
            if(encoded_index>=encoded.size())
                break;

            if(encoded[encoded_index].flag)
            {
                fwrite(&encoded[encoded_index].offset_length, sizeof(encoded[encoded_index].offset_length), 1, f);
                total_written_bytes += sizeof(encoded[encoded_index].offset_length);
            }
            else
            {
                fwrite(&encoded[encoded_index].c, sizeof(encoded[encoded_index].c), 1, f);
                total_written_bytes += sizeof(encoded[encoded_index].c);
            }
        }
    }
    
    return total_written_bytes;
}