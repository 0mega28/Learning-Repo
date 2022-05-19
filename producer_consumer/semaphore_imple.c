#include <stdio.h>
#include <stdlib.h>
#include <semaphore.h>
#include <pthread.h>

#include "util.h"

sem_t mutex, full, empty;

void *consumer(void *varg)
{
    while (1)
    {
        int item = produce_item();
        sem_wait(&empty);
        sem_wait(&mutex);
        insert_item(item);
        sem_post(&mutex);
        sem_post(&full);
    }

    return NULL;
}

void *producer(void *varg)
{
    while (1)
    {
        sem_wait(&full);
        sem_wait(&mutex);
        int item = remove_item();
        sem_post(&mutex);
        sem_post(&empty);
        consume_item(item);
        int val;
        sem_getvalue(&full, &val);
        printf("count = %d\n", val);
    }
    return NULL;
}

int main()
{
    sem_init(&mutex, 0, 1);
    sem_init(&full, 0, 0);
    sem_init(&empty, 0, N);

    pthread_t consumer_thread, producer_thread;
    pthread_create(&consumer_thread, NULL, consumer, NULL);
    pthread_create(&producer_thread, NULL, producer, NULL);
    pthread_join(consumer_thread, NULL);
    pthread_join(producer_thread, NULL);

    sem_destroy(&mutex);
    sem_destroy(&full);
    sem_destroy(&empty);

    return 0;
}
