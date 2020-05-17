typedef struct huffman_node
{
    char c;
    unsigned freq;

    huffman_node *left, *right;
} huffman_node;

typedef struct huffman_tree
{
    unsigned capacity;
    unsigned size;

    huffman_node **nodes;
} huffman_tree;

huffman_node *new_huffman_node(char c, int freq);
void huffman_tree_sort(huffman_tree *tree);