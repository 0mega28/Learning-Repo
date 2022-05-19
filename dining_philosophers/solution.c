#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <pthread.h>
#include <semaphore.h>

#define N 5
#define LEFT(i) ((i + N - 1) % N)
#define RIGHT(i) ((i + 1) % N)
#define THINKING 0
#define HUNGRY 1
#define EATING 2

#define MAX_THINK_TIME (int)1e3
#define MAX_FORK_TAKE_TIME (int)1e1
#define MAX_EAT_TIME (int)1e2
#define MAX_FORK_PUT_TIME (int)1e1

int state[N];
sem_t forks[N];
sem_t mutex;

void think();
void take_forks(int i);
void eat();
void put_forks(int i);
void test(int i);

void *philosopher(void *args)
{
    int i = *((int *)args);
    printf("Philosopher %d\n", i);

    while (1)
    {
        think();
        take_forks(i);
        eat();
        put_forks(i);
    }
}

int main()
{
    pthread_t threads[N];
    int args[N];

    for (int i = 0; i < N; i++)
        sem_init(&forks[i], 0, 1);
    sem_init(&mutex, 0, 1);

    for (int i = 0; i < N; i++)
    {
        args[i] = i;
        state[i] = THINKING;
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

void take_forks(int i)
{
    usleep(rand() % MAX_FORK_TAKE_TIME);

    sem_wait(&mutex);
    state[i] = HUNGRY;
    test(i); /* Try to acquire 2 forks */
    sem_post(&mutex);
    sem_wait(&forks[i]);

    printf("Philosopher %lu is eating\n", pthread_self());
}

void eat()
{
    usleep(rand() % MAX_EAT_TIME);
    printf("Philosopher %lu has eaten\n", pthread_self());
}

void put_forks(int i)
{
    usleep(rand() % MAX_FORK_PUT_TIME);

    sem_wait(&mutex);
    state[i] = THINKING;
    test(LEFT(i));  /* See if left neighbor can now eat */
    test(RIGHT(i)); /* See if right neighbor can now eat */
    sem_post(&mutex);

    printf("Philosopher %lu is putting fork\n", pthread_self());
}

void test(int i)
{
    if (state[i] == HUNGRY && state[LEFT(i)] != EATING && state[RIGHT(i)] != EATING)
    {
        state[i] = EATING;
        sem_post(&forks[i]);
    }
}
