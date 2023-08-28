toDigits    :: Integer -> [Integer]
toDigitsRev :: Integer -> [Integer]
doubleEveryOther :: [Integer] -> [Integer]
doubleEveryOtherFromRight :: [Integer] -> [Integer]
sumDigits :: [Integer] -> Integer
validate :: Integer -> Bool

toDigits n
    | n <= 0 = []
    | otherwise = toDigits (n `div` 10) ++ [n `mod` 10]

toDigitsRev n
    | n <= 0 = []
    | otherwise = (n `mod` 10) : toDigitsRev (n `div` 10)

doubleEveryOther [] = []
doubleEveryOther [x] = [x]
doubleEveryOther (x : y : xs) = x : y * 2 : doubleEveryOther xs

doubleEveryOtherFromRight = reverse . doubleEveryOther . reverse

sumDigits [] = 0
sumDigits (x : xs) = sum (toDigits x) + sumDigits xs

validate n = sumDigits ( doubleEveryOtherFromRight (toDigits n)) `mod` 10 == 0

isValidString :: Bool -> String
isValidString True = "is Valid"
isValidString False = "is not Valid"



main :: IO ()
main = do
    let creditCardNum = 2345243534531
    putStrLn ("The Credit Card Number: " ++ show creditCardNum ++ " " ++ isValidString (validate creditCardNum))
