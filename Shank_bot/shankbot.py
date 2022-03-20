# Inspired by https://www.youtube.com/watch?v=DmfxIhmGPP4
# The Reciprocals of Primes

def calc_shank(num: int):
    # Stores the value after decimal 0.str
    quotient: str = ""
    # Current dividend
    dividend: int = 1
    # Stores all the remainder obtained
    remainder_map: dict = {}

    # Run the loop till the same remainder appears for two times
    while remainder_map.get(dividend, 0) != 2:
        # Save the quotient to return
        saved_quotient: str = quotient

        # Multiply the dividend by 10 since we are dealing with decimal
        dividend *= 10

        # Pad the dividend
        while dividend < num:
            dividend *= 10
            quotient += "0"

        quotient += str(int(dividend / num))
        dividend = dividend % num

        remainder_map.setdefault(dividend, 0)
        remainder_map[dividend] += 1

    return len(saved_quotient)

def main():
    num: int = int(input("Enter a number: "))
    print(calc_shank(num))

if __name__ == "__main__":
    main()
