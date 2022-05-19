#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>

#define N 5
#define MAX_THINK_TIME (int)1e3
#define MAX_FORK_TAKE_TIME (int)1e1
#define MAX_EAT_TIME (int)1e2
#define MAX_FORK_PUT_TIME (int)1e1

sem_t forks[N];

void think();
void take_fork(int i);
void eat();
void put_fork(int i);

void *philosopher(void *args)
{
    int i = *((int *)args);
    printf("Philosopher %d\n", i);

    while (1)
    {
        think();
        take_fork(i);
        take_fork((i + 1) % N);
        eat();
        put_fork(i);
        put_fork((i + 1) % N);
    }
}

int main()
{
    pthread_t threads[N];
    int args[N];

    for (int i = 0; i < N; i++)
        sem_init(&forks[i], 0, 1);


    for (int i = 0; i < N; i++)
    {
        args[i] = i;
        pthread_create(&threads[i], NULL, philosopher, &args[i]);
    }

    for (int i = 0; i < N; i++)
    {
        pthread_join(threads[i], NULL);
    }

    for (int i = 0; i < N; i++)
        sem_destroy(&forks[i]);

    return 0;
}

void think()
{
    printf("Philosopher %lu is thinking\n", pthread_self());
    usleep(rand() % MAX_THINK_TIME);
}

void take_fork(int i)
{
    usleep(rand() % MAX_FORK_TAKE_TIME);
    sem_wait(&forks[i]);
    printf("Philosopher %lu is eating\n", pthread_self());
}

void eat()
{
    usleep(rand() % MAX_EAT_TIME);
    printf("Philosopher %lu has eaten\n", pthread_self());
}

void put_fork(int i)
{
    usleep(rand() % MAX_FORK_PUT_TIME);
    sem_post(&forks[i]);
    printf("Philosopher %lu is putting fork\n", pthread_self());
}
