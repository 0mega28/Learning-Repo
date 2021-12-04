#ifndef SHELL_BUILTIN_UTILS_H
#define SHELL_BUILTIN_UTILS_H

/* args: **argv
 * return 0 if successfull
 * -1 if error
 * greater than 0 if command not found 
 */
int run_builtin_command(char **argv);

#endif // SHELL_BUILTIN_UTILS_H
