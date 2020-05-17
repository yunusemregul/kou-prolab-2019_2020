class lz77_token
{
    public:
        uint8_t offset_length;
        char c;

        lz77_token();

        void set_offset(int offset);
        int get_offset();
        void set_length(int length);
        int get_length();
        void print();
};

vector<lz77_token> lz77_encode(char* input);
vector<char> lz77_decode(vector<lz77_token> encoded);