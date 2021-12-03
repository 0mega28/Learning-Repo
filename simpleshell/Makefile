CC=/usr/bin/gcc
CFLAGS=-g -Wall -Werror

all:shell

shell: main.c
	$(CC) $(CFLAGS) -o shell $<

run: shell
	@-./shell

clean:
	rm shell