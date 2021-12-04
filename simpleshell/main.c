#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include <stdbool.h>

#include "builtin_utils.h"
#include "cd.h"

#define NUMBER_OF_ARGS 40
#define MAX_ARG_LENGTH 100
#define BUFFER_SIZE 500

void show_prompt();
void read_command();
void run_command();
void cleanup();

char prompt[BUFFER_SIZE];
static char *argv[NUMBER_OF_ARGS];
static char *line;

int main(void)
{
	while (true)
	{
		show_prompt();
		read_command();
		run_command();
		cleanup();
	}
	return 0;
}

void show_prompt()
{
	char cwd[BUFFER_SIZE];
	getcwd(cwd, sizeof(cwd));

	strcpy(prompt, cwd);
	strcat(prompt, " $ ");

	printf("%s", prompt);
}

void read_command()
{
	size_t len = 0;
	ssize_t read;
	read = getline(&line, &len, stdin);
	if (read == -1)
	{
		exit(EXIT_FAILURE);
	}
	line[strcspn(line, "\n")] = 0;

	int i = 0;
	char *token = strtok(line, " ");
	while (token != NULL)
	{
		argv[i] = token;
		token = strtok(NULL, " ");
		i++;
	}
	argv[i] = NULL;
}

void run_command()
{
	if (run_builtin_command(argv) <= 0)
	{
		return;
	}

	pid_t child = fork();
	if (child == 0)
	{
		/* In child */
		execvp(argv[0], argv);
	}
	else if (child > 0)
	{
		/* In parent */
		int status;
		waitpid(child, &status, 0);
	}
	else
	{
		printf("fork failed\n");
		exit(EXIT_FAILURE);
	}
}

void cleanup()
{
	free(line);
}
