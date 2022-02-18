CC=/usr/bin/gcc
CFLAGS=-g -Wall -Werror
C_SOURCES := $(wildcard *.c)

all:shell

shell: $(C_SOURCES)
	$(CC) $(CFLAGS) -o shell $^

run: shell
	@-./shell

clean:
	rm shell
	