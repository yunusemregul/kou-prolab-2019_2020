#include "includes.h"

#define OFFSET_BITS 12
#define LENGTH_BITS 4

#define OFFSET_MASK ((1 << (OFFSET_BITS)) - 1) << LENGTH_BITS
#define LENGTH_MASK ((1 << (LENGTH_BITS)) - 1)

void print_bits(int a)
{
    int i;
    int mul = 1;
    
    for(i=1; (mul<a); i++)
        mul *= 2;

    i = (i + 4-i%4);

    for(i=i-1; i>=0; i--)
    {
        if(i>0 && ((i+1)%4)==0)
            printf(" ");

        printf("%d",(a & 1<<i) ? 1 : 0);
    }
}

typedef class token
{
    public:
        uint16_t offset_length;
        char c;

        token()
        {
            offset_length = 0;
            c = 0;
        }

        void set_offset(int offset)
        {
            offset = (offset << LENGTH_BITS);
            offset = (offset & OFFSET_MASK);
            offset_length = (offset_length & LENGTH_MASK);
            offset_length = (offset_length | offset);
        }

        int get_offset()
        {
            return ((offset_length & OFFSET_MASK) >> LENGTH_BITS);
        }

        void set_length(int length)
        {
            length = (length & LENGTH_MASK);
            offset_length = (offset_length & OFFSET_MASK);
            offset_length = (offset_length | length);
        }

        int get_length()
        {
            return (offset_length & LENGTH_MASK);
        }
} token;

int main(void)
{
    token a;
    a.set_offset(5);
    a.set_offset(10);
    a.set_length(2);
    a.set_offset(8);
    printf("%d %d\n\n",a.get_offset(),a.get_length());
}