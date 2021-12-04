#include "cd.h"

#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

int change_directory(char *path)
{
	if (path == NULL)
	{
		return -1;
	}
	if (chdir(path) == -1)
	{
		perror(path);
		return -1;
	}
	return 0;
}
