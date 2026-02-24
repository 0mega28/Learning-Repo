#include <atomic>

/**
 * Sticky counter implementation
 * 
 */
struct Counter
{
public:
    /**
     * Increments the counter only if it was not zero
     * @returns true if the counter is not zero, otherwise, false
     */
    bool increment_if_not_zero();
    /**
     * Decrements the counter
     * @returns true if the counter is zero, otherwise, false
     */
    bool decrement();
    /**
     * Reads the current state of the counter
     */
    uint64_t read();
private:
    std::atomic<uint64_t> counter{1};

    // if is_zero flag is set then it means that counter is zero
    static constexpr uint64_t is_zero = 1ull << 63;

    // helped flag is set by read to help the decrement operation
    static constexpr uint64_t helped  = 1ull << 62;
};

bool Counter::increment_if_not_zero()
{
    // if is_zero flag is set than it means the counter is zero
    return (counter.fetch_add(1) & is_zero) == 0;
}

bool Counter::decrement()
{
    if (counter.fetch_sub(1) == 1) {
        // reaching here means that we have possibility of setting the counter to zero
        //  the reason it's a possiblity is because another increment operation might be happening
        uint64_t e = 0;

        // if we are able to succeed with the following operation then well and good, now the counter is zero
        if (counter.compare_exchange_strong(e, is_zero)) return true;

        // if the helped flag is set (means it was helped by the read operation and the counter should be zero now)
        //  and then try to exchange counter value with is_zero
        // Since, multiple decrements might be happening only one decrement operation should return true, which has
        //  got helped flag back returned from the exchange operation
        else if ((e & helped) && (counter.exchange(is_zero) & helped)) return true;
    }
    return false;
}

uint64_t Counter::read()
{
    uint64_t value = counter.load();
    // if value is zero, then it means we are in if condition of decrement operation and we are trying to help
    //  the decrement operation
    if (value == 0 && counter.compare_exchange_strong(value, is_zero | helped)) return 0;
    return (value & is_zero) ? 0 : value;
}
