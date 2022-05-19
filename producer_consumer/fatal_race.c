/* Page #89 */
/* Figure 2-13. The producer-consumer problem with a fatal race condition. */

#include <unistd.h>
#include <stdlib.h>
#include <pthread.h>
#include <stdio.h>

#include "util.h"

int count = 0; /* Number of items in the buffer */

int producer_f = 0;
int consumer_f = 0;

/*
 * NOTE: goto_sleep, wakeup
 * This is not the correct implementation of goto_sleep and wakeup
 * in actual implementation they are system calls and managed by the kernel
 */

/*
 * @param entity: producer or consumer
 */
void goto_sleep(int *entity)
{
    *entity = 1;
    while (*entity != 0)
        ;
}

/*
 * @param entity: producer or consumer
 */
void wakeup(int *entity)
{
    *entity = 0;
}

void *producer(void *varg)
{
    printf("Starting producer\n");
    while (1)
    {
        int item = produce_item();
        if (count == N)
        {
            goto_sleep(&producer_f);
            printf("Producer sleep\n");
        }
        insert_item(item);
        count++;
        if (count == 1)
        {
            wakeup(&consumer_f);
            printf("Consumer Wake\n");
        }
    }
    return NULL;
}

void *consumer(void *varg)
{
    printf("Starting consumer\n");
    while (1)
    {
        if (count == 0)
        {
            goto_sleep(&consumer_f);
            printf("Consumer sleep\n");
        }
        int item = remove_item();
        count--;
        if (count == N - 1)
        {
            wakeup(&producer_f);
            printf("Producer Wake\n");
        }
        consume_item(item);
        printf("count = %d\n", count);
    }
    return NULL;
}

int main()
{
    pthread_t consumer_thread, producer_thread;
    pthread_create(&consumer_thread, NULL, consumer, NULL);
    pthread_create(&producer_thread, NULL, producer, NULL);
    pthread_join(consumer_thread, NULL);
    pthread_join(producer_thread, NULL);
    return 0;
}
