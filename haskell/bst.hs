data Tree a
  = Leaf
  | Node
      { left :: Tree a,
        value :: a,
        right :: Tree a
      }
  deriving (Eq, Show, Read)

singleton :: a -> Tree a
singleton x = Node Leaf x Leaf

treeInsert :: (Ord a) => a -> Tree a -> Tree a
treeInsert x Leaf = singleton x
treeInsert x node@(Node left val right)
    | x == val = node
    | x < val = Node (treeInsert x left) val right
    | otherwise = Node left val (treeInsert x right)

treeElem :: (Ord a) => a -> Tree a -> Bool
treeElem x Leaf = False
treeElem x node@(Node left val right)
    | x == val = True
    | x < val = treeElem x left
    | x > val = treeElem x right
