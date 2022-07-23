#include <iostream>

template <typename T>
class Shared_ptr
{
private:
	T *ptr;
	size_t *count;

public:
	Shared_ptr(T *p = nullptr) : ptr(p), count(new size_t(1)){};

	Shared_ptr(const Shared_ptr &other) : ptr(other.ptr), count(other.count)
	{
		++*count;
	}

	~Shared_ptr()
	{
		if (--*count == 0)
		{
			delete ptr;
			delete count;
		}
	}

	Shared_ptr &operator=(const Shared_ptr &other)
	{
		this->ptr = other.ptr;
		this->count = other.count;
		++*count;
		return *this;
	}

	T &operator*()
	{
		return *ptr;
	}

	T *operator->()
	{
		return ptr;
	}

	template <typename U>
	friend std::ostream &operator<<(std::ostream &os, const Shared_ptr<U> &sp)
	{
		os << "value: " << *sp.ptr << " ptr: " << sp.ptr << " count: " << *sp.count << std::endl;
		return os;
	}
};

int main(void)
{
	/* main function is taken from
	 * https://www.geeksforgeeks.org/how-to-implement-user-defined-shared-pointers-in-c/
	 */
	using namespace std;
	Shared_ptr<int> ptr1(new int(151));
	cout << "--- Shared pointers ptr1 ---\n";
	*ptr1 = 100;
	cout << " ptr1's value now: " << *ptr1 << endl;
	cout << ptr1;

	{
		// ptr2 pointing to same integer
		// which ptr1 is pointing to
		// Shared pointer reference counter
		// should have increased now to 2.
		Shared_ptr<int> ptr2 = ptr1;
		cout << "--- Shared pointers ptr1, ptr2 ---\n";
		cout << ptr1;
		cout << ptr2;

		{
			// ptr3 pointing to same integer
			// which ptr1 and ptr2 are pointing to.
			// Shared pointer reference counter
			// should have increased now to 3.
			Shared_ptr<int> ptr3(ptr2);
			cout << "--- Shared pointers ptr1, ptr2, ptr3 "
					"---\n";
			cout << ptr1;
			cout << ptr2;
			cout << ptr3;
		}

		// ptr3 is out of scope.
		// It would have been destructed.
		// So shared pointer reference counter
		// should have decreased now to 2.
		cout << "--- Shared pointers ptr1, ptr2 ---\n";
		cout << ptr1;
		cout << ptr2;
	}

	// ptr2 is out of scope.
	// It would have been destructed.
	// So shared pointer reference counter
	// should have decreased now to 1.
	cout << "--- Shared pointers ptr1 ---\n";
	cout << ptr1;

	return 0;
}