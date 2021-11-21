#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <assert.h>
#include <string.h>

#include "wordlist.h"

#define MAX_CHILDREN 256

typedef struct Node
{
	struct Node *children[MAX_CHILDREN];
	bool isEnd;
} Node;

void insert(struct Node *root, const char *word)
{
	assert(root != NULL);
	if (*word == '\0')
	{
		root->isEnd = true;
		return;
	}

	struct Node *curr = root;
	for (int i = 0; word[i]; i++)
	{
		int index = word[i];
		if (!curr->children[index])
		{
			curr->children[index] = malloc(sizeof(struct Node));
			curr->children[index]->isEnd = false;
			for (int j = 0; j < MAX_CHILDREN; j++)
			{
				curr->children[index]->children[j] = NULL;
			}
		}
		curr = curr->children[index];
	}
	curr->isEnd = true;
	return;
}

void freeTrie(Node *root)
{
	for (int i = 0; i < MAX_CHILDREN; i++)
	{
		if (root->children[i])
		{
			freeTrie(root->children[i]);
		}
	}
	free(root);
}

void load_words(Node *root, const char *wordlist[], size_t wordlist_len)
{
	for (size_t i = 0; i < wordlist_len; i++)
		insert(root, wordlist[i]);

	return;
}

size_t buffsize = 1024;
void dfs_and_print(Node *root, char *word)
{
	if (strlen(word) == buffsize - 1)
	{
		word = realloc(word, buffsize * 2);
	}
	assert(root != NULL);
	if (root->isEnd)
	{
		printf("%s\n", word);
	}
	for (int i = 0; i < MAX_CHILDREN; i++)
	{
		if (root->children[i])
		{
			size_t word_len = strlen(word);
			word[word_len] = i;
			word[word_len + 1] = '\0';
			dfs_and_print(root->children[i], word);
			word[word_len] = '\0';
		}
	}
}

void autocomplete(Node *root, char *prefix)
{
	assert(root != NULL);
	if (*prefix == '\0')
	{
		return;
	}

	struct Node *curr = root;
	for (int i = 0; prefix[i]; i++)
	{
		int index = prefix[i];
		if (!curr->children[index])
		{
			return;
		}
		curr = curr->children[index];
	}
	dfs_and_print(curr, prefix);
	return;
}

void usage(FILE *sink, const char *progname)
{
	fprintf(sink, "Usage: %s <prefix>\n", progname);
	return;
}

int main(int argc, char **argv)
{
	const char *progname = argv[0];

	if (argc != 2)
	{
		usage(stderr, progname);
		fprintf(stderr, "Error: Invalid number of arguments\n");
	}
	if (strcmp(argv[1], "help") == 0)
	{
		usage(stdout, progname);
		return 0;
	}
	Node *root = (Node *)malloc(sizeof(Node));
	load_words(root, wordlist, wordlist_size);

	char *buff = (char *) malloc( sizeof(char) * buffsize );
	memset(buff, 0, buffsize);
	strcpy(buff, argv[1]);
	
	autocomplete(root, buff);

	free(buff);
	freeTrie(root);
	return 0;
}