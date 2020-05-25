class lzss_token
{
    public:
        unsigned flag : 1;
        uint16_t offset_length;
        char c;

        lzss_token();

        void set_offset(int offset);
        int get_offset();

        void set_length(int length);
        int get_length();

        void set_flag(int flag);
        int get_flag();

        void print();
};

vector<lzss_token> lzss_encode(char* input);
int lzss_write(vector<lzss_token> encoded, FILE *f);