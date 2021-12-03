#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/wait.h>
#include <stdbool.h>

#define NUMBER_OF_ARGS 20
#define MAX_ARG_LENGTH 100
#define PROMT_SIZE 500

void print_argv(char **argv);
void type_prompt(char *prompt);
void read_command(char **argv);
void run_command(char **argv);
void alloc_argv(char **argv);
void free_argv(char **argv);

int main(void)
{
	char prompt[PROMT_SIZE] = "shell$ ";
	char *argv[NUMBER_OF_ARGS];

	while (true)
	{
		alloc_argv(argv);

		type_prompt(prompt);
		read_command(argv);
		run_command(argv);

		free_argv(argv);
	}

	return 0;
}

void type_prompt(char *prompt)
{
	printf("%s", prompt);
}

void read_command(char **argv)
{
	char *line = NULL;
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
		strcpy(argv[i], token);
		token = strtok(NULL, " ");
		i++;
	}
	free(argv[i]);
	argv[i] = NULL;
	free(line);
}

void run_command(char **argv)
{
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

void print_argv(char **argv)
{
	int i = 0;
	while (argv[i] != NULL)
	{
		printf("%s ", argv[i]);
		i++;
	}
}

void alloc_argv(char **argv)
{
	for (int i = 0; i < NUMBER_OF_ARGS; i++)
	{
		argv[i] = malloc(sizeof(char) * MAX_ARG_LENGTH);
	}
}

void free_argv(char **argv)
{
	for (int i = 0; i < NUMBER_OF_ARGS; i++)
	{
		free(argv[i]);
	}
}
