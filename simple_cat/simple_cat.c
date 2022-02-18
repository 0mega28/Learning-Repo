#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <string.h>

int main(int argc, char **argv)
{
	if (argc <= 1)
	{
		printf("Too few arguments\n");
		exit(EXIT_FAILURE);
	}

	FILE *file = fopen(argv[1], "r");

	if (!file)
	{
		printf("%s\n", strerror(errno));
		exit(EXIT_FAILURE);
	}

	char c;
	while ((c = fgetc(file)) != EOF)
		printf("%c", c);

	fclose(file);
	return 0;
}