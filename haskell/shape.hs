module Shapes (
    Point(..),
    Shape(..),
    surface,
    nudge,
    baseCircle,
    baseRect
) where

data Point = Point Float Float deriving (Show)
data Shape = Circle Point Float |
             Rectangle Point Point
             deriving (Show)

surface :: Shape -> Float
surface c@(Circle _ r) = pi * r ^ 2
surface r@(Rectangle p1@(Point x1 y1) p2@(Point x2 y2)) = abs (x1 - x2) * abs (y1 - y2)

nudge :: Shape -> Float -> Float -> Shape
nudge (Circle (Point x y) r) a b = Circle (Point (x+a) (y+b)) r
nudge (Rectangle (Point x1 y1) (Point x2 y2)) a b = Rectangle (Point (x1+a) (y1+b)) (Point (x2+a) (y2+b))

baseCircle :: Float -> Shape
baseCircle = Circle (Point 0 0)

baseRect :: Float -> Float -> Shape
baseRect width height = Rectangle (Point 0 0) (Point width height)

data Person = Person {
    firstName :: String,
    lastName :: String
} deriving Show

data Maybe a = Nothing | Just a deriving Show
