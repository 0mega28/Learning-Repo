#include <stdio.h>
#include <stdlib.h>

int main(int argc, char **argv)
{
	if (argc <= 1)
	{
		printf("Too few arguments\n");
		exit(0);
	}

	FILE *file = fopen(argv[1], "r");

	if (!file)
	{
		printf("File not found\n");
		exit(0);
	}

	char c;
	while ((c = fgetc(file)) != EOF)
		printf("%c", c);

	fclose(file);
	return 0;
}