#include <stdio.h>

int main()
{
	int var = 1;
	char buff[2];

	gets(buff);

	if (var != 1)
	{
		printf("%s: var=%d\n", "buffer overflow", var);
	}

	return 0;
}
