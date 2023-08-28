import Data.List (tails, isPrefixOf)
import qualified Data.Map as Map

maximum' :: (Ord a) => [a] -> a
maximum' [] = error "Empty list"
maximum' [x] = x
maximum' (x: xs) = max x (maximum' xs)

replicate' :: (Num i, Ord i) => i -> a -> [a]
replicate' times value
    | times <= 0 = []
    | otherwise = value : replicate' (times - 1) value

take' :: (Num i, Ord i) => i -> [a] -> [a]
take' num lst
    | num <= 0 = []
take' _ [] = []
take' num (ele:rest) = ele : take' (num - 1) rest

reverse' :: [a] -> [a]
reverse' [] = []
reverse' (ele:rest) = reverse' rest ++ [ele]

repeat' :: a -> [a]
repeat' a = a : repeat' a

zip' :: [a] -> [b] -> [(a, b)]
zip' _ [] = []
zip' [] _ = []
zip' (a:as) (b:bs) = (a, b) : zip' as bs

elem' :: (Eq a) => a -> [a] -> Bool
elem' _ [] = False
elem' a (x:xs)
    | a == x = True
    | otherwise = elem' a xs

quicksort :: (Ord a) => [a] -> [a]
quicksort [] = []
quicksort (x:xs) = quicksort (filter (<=x) xs) ++ [x] ++ quicksort (filter (>x) xs)

foldl' :: (a -> b -> a) -> a -> [b] -> a
foldl' _ acc [] = acc
foldl' f acc (x:xs) = foldl' f (f acc x) xs

elem'' :: Eq a => a -> [a] -> Bool
elem'' x = foldl (\acc c -> (x == c) || acc) False

map' :: (a -> b) -> [a] -> [b]
map' f = foldr (\x a -> f x : a) []

max' :: (Num a, Ord a, Bounded a) => [a] -> a
max' = foldl (\a x -> if a > x then a else x) minBound

intersperse' :: a -> [a] -> [a]
intersperse' _ [] = []
intersperse' _ [x] = [x]
intersperse' a (x:xs) = x:a: intersperse' a xs

search :: (Eq a) => [a] -> [a] -> Bool
search subl ls =
    foldl (\acc x -> take slen x == subl) False $ tails ls
    where slen = length subl

isPrefixOf :: (Eq a) => [a] -> [a] -> Bool
isPrefixOf subl ls = subl == take sublen ls
    where sublen = length subl

findKey :: (Eq k) => k -> [(k, v)] -> Maybe v
findKey key = foldl (\acc (k, v) -> if key == k then Just v else Nothing) Nothing

delete' :: (Eq a) => a -> [a] -> [a]
delete' x = foldr (\e a -> if e == x then a else e:a) []
