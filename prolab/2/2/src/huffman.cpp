#include "includes.h"

huffman_node *new_huffman_node(char c, int freq)
{
    huffman_node *node = (huffman_node *)malloc(sizeof(huffman_node));
   
    node->c = c;
    node->freq = freq;
    node->left = node->right = NULL;

    return node;
}

huffman_tree *new_huffman_tree(unsigned cap)
{
    huffman_tree *tree = (huffman_tree *)malloc(sizeof(huffman_tree));
    tree->capacity = cap;
    tree->size = 0;
    tree->nodes = (huffman_node **)malloc(cap * sizeof(huffman_node));

    return tree;
}

void huffman_tree_swap(huffman_node **a, huffman_node **b)
{
    huffman_node *temp = *a;
    *a = *b;
    *b = temp;
}

void huffman_tree_sort(huffman_tree *tree)
{
    int max_index, max_value;

    int i;
    for (i = 0; i < tree->size - 1; i++)
    {
        for(int j = i + 1; j < tree->size; j++)
        {
            if(tree->nodes[j]->freq > max_value)
            {
                max_value = tree->nodes[j]->freq;
                max_index = j;
            }
        }
    }
}