CC=gcc
CFLAGS=-g -Wall -Werror

all: fatal_race semaphore_imple

fatal_race: fatal_race.c util.h
	${CC} ${CFLAGS} $^ -o $@

semaphore_imple: semaphore_imple.c util.h
	${CC} ${CFLAGS} $^ -o $@
