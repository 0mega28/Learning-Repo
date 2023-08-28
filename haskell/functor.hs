import Prelude hiding (fmap, Functor)

class Functor f where
    fmap :: (a -> b) -> f a -> f b

instance Functor [] where
    fmap = map

instance Functor Maybe where
    fmap f (Just x) = Just $ f x
    fmap f Nothing = Nothing


data Tree a
  = Leaf
  | Node
      { left :: Tree a,
        value :: a,
        right :: Tree a
      }
  deriving (Eq, Show, Read)

instance Functor Tree where
  fmap f (Node left val right) = Node (fmap f left) (f val) (fmap f right)
  fmap f Leaf = Leaf

