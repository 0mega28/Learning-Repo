#include <unistd.h>
#include <stdlib.h>

#define N 100 /* Number of slots in the buffer */
#define MAX_WAIT_TIME (int)1e2

int produce_item()
{
    usleep(rand() % MAX_WAIT_TIME);
    return 1;
}

void insert_item(int item)
{
    usleep(rand() % MAX_WAIT_TIME);
}

int remove_item()
{
    usleep(rand() % MAX_WAIT_TIME);
    return 1;
}

void consume_item()
{
    usleep(rand() % MAX_WAIT_TIME);
}
