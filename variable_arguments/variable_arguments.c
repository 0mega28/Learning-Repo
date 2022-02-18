#include <stdio.h>
#include <stdarg.h>

double average(int, ...);

int main()
{
	printf("The average of 1, 2, 3, 4, 5 is %f\n", average(5, 1, 2, 3, 4, 5));
	return 0;
}

double average(int arg_count, ...)
{
	/*
	 * va_list is a type to hold information about
	 * variable arguemnts
	 */
	va_list valist;

	double sum = 0.0;

	/* 
	 * va_start must be called before accessing 
	 * variable argument list
	 */
	va_start(valist, arg_count);

	/*
	 * Arguments can be accessed one by one using
	 * va_arg macro
	 */
	for (int i = 0; i < arg_count; i++)
		sum += va_arg(valist, int);
	
	/*
	 * va_end should be called before the function
	 * returns
	 */
	va_end(valist);

	return sum / arg_count;
}