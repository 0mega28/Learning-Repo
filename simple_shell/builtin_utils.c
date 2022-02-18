#include "builtin_utils.h"
#include "cd.h"

#include <stdbool.h>
#include <string.h>

const char *builtin_commands[] = {
	"cd",
};

#define NUM_BUILTIN_COMMANDS (sizeof(builtin_commands) / sizeof(char *))

static bool is_builtin(char *cmd) {
	for (int i = 0; i < NUM_BUILTIN_COMMANDS; i++) {
		if (strcmp(cmd, builtin_commands[i]) == 0) {
			return true;
		}
	}
	return false;
}

int run_builtin_command(char **argv)
{
	if (!is_builtin(argv[0])) {
		return 1;
	}

	if (strcmp(argv[0], "cd") == 0) {
		return change_directory(argv[1]);
	}

	return -1;
}
