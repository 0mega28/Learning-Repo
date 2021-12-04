#include <stdio.h>
#include <pthread.h>
#include <unistd.h>
#include <stdlib.h>

pthread_t thread1, thread2;
pthread_mutex_t lock1, lock2;

void *thread1_func(void *args)
{
	pthread_mutex_lock(&lock1);
	printf("Thread 1: Lock 1 acquired\n");

	sleep(1);

	pthread_mutex_lock(&lock2);
	printf("Thread 1: Lock 2 acquired\n");

	pthread_mutex_unlock(&lock1);
	printf("Thread 1: Lock 1 released\n");

	pthread_mutex_unlock(&lock2);
	printf("Thread 1: Lock 2 released\n");

	return NULL;
}

void *thread2_func(void *args)
{
	pthread_mutex_lock(&lock2);
	printf("Thread 2: Lock 2 acquired\n");

	sleep(1);

	pthread_mutex_lock(&lock1);
	printf("Thread 2: Lock 1 acquired\n");

	pthread_mutex_unlock(&lock2);
	printf("Thread 2: Lock 2 released\n");

	pthread_mutex_unlock(&lock1);
	printf("Thread 2: Lock 1 released\n");

	return NULL;
}

int main (void) 
{
	pthread_mutex_init(&lock1, NULL);
	pthread_mutex_init(&lock2, NULL);

	pthread_create(&thread1, NULL, thread1_func, NULL);
	pthread_create(&thread2, NULL, thread2_func, NULL);

	pthread_join(thread1, NULL);
	pthread_join(thread2, NULL);

	pthread_mutex_destroy(&lock1);
	pthread_mutex_destroy(&lock2);
	
	return 0;
}
