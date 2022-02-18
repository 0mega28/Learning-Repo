#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

void pipeline(char **cmd1, char **cmd2)
{
	int fd[2];
#define WRITE 1
#define READ 0
	pipe(fd);

	pid_t child = fork();

	switch (child)
	{
	case -1:
		perror("fork");
		exit(EXIT_FAILURE);
	case 0: /* In child */
		close(STDIN_FILENO);
		if (dup2(fd[READ], STDIN_FILENO) == -1)
		{
			perror("dup2 in child");
			exit(EXIT_FAILURE);
		}
		close(fd[WRITE]);
		execvp(cmd2[0], cmd2);
		break;
	default: /* In parent */
		close(STDOUT_FILENO);
		if (dup2(fd[WRITE], STDOUT_FILENO) == -1)
		{
			perror("dup2 in parent");
			exit(EXIT_FAILURE);
		}
		close(fd[READ]);
		execvp(cmd1[0], cmd1);
		break;
	}
}

int main(void)
{
	char *cmd1[] = {"ls", "-l", NULL};
	char *cmd2[] = {"wc", "-l", NULL};

	pid_t child = fork();

	switch (child)
	{
	case 0: /* In child */
		pipeline(cmd1, cmd2);
	case -1:
		perror("fork");
		break;
	default: /* In parent */
		waitpid(child, NULL, 0);
	}

	printf("Pipeline finished\n");

	return 0;
}