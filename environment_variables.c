#include <stdio.h>

extern char **environ;

int length_of_string_array(char **array)
{
	int length = -1;
	while (array[++length] != 0)
		;
	return length;
}

void print_string_array(char **array)
{
	size_t length = length_of_string_array(array);

	for (size_t i = 0; i < length; i++)
		printf("%s\n", array[i]);
}

int main()
{
	print_string_array(environ);

	return 0;
}