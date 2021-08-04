/* g++ multi_thread_sum.cpp -lpthread && time ./a.out */

#include<iostream>
#include<thread>
#include<mutex>

typedef unsigned long long int lli;

/* Mutex lock */
std::mutex mtx;

lli sum = 0;

void sum_function(lli start, lli end, int id) {
    lli ret_sum = 0;
    
	std::cout << "Thread Started " << id << std::endl;

    for (lli i = start; i < end; i++)
        ret_sum += i;

	/* Lock before changing critical section */
	std::cout << "Thread Executed " << id << std::endl;
    mtx.lock();
    sum += ret_sum;
    mtx.unlock();
	std::cout << "Thread Finished " << id << std::endl;
}

int main() {
    lli start = 1, end = 10000000000;

	/* Number of thread used for execution */
    int number_of_threads = 6;

	std::cout << "Number of threads " << number_of_threads << "\n" << std::endl;

    std::thread threads[number_of_threads];

    lli cstart = start;
    for (int i = 0; i < number_of_threads; i++) {
        lli cend = (end * (i + 1)) / number_of_threads ;

		/* Create thread and call function "sum_function" */
        threads[i] = std::thread(sum_function, cstart, cend, i);

        cstart = cend;
    }

	/* Synchronise all the threads till here */
    for (auto &th: threads)
        th.join();

    std::cout << "\nSum: " << sum << std::endl;
	std::cout << std::endl;

	return 0;
}
