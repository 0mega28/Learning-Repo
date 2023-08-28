{-# OPTIONS_GHC -Wno-unrecognised-pragmas #-}

{-# HLINT ignore "Use lambda-case" #-}
module Main where

import Control.Applicative (Alternative (empty, many), (<|>))
import Data.Char
import Text.XHtml (input)

data JsonValue
  = JsonNull
  | JsonBool Bool
  | JsonNumber Integer -- TODO: Support for floating point numbers
  | JsonString String
  | JsonArray [JsonValue]
  | JsonObject [(String, JsonValue)]
  deriving (Show, Eq)

newtype Parser a = Parser
  { runParser :: String -> Maybe (String, a)
  }

instance Functor Parser where
  fmap :: (a -> b) -> Parser a -> Parser b
  fmap f (Parser p) = Parser $ \input -> do
    (input', x) <- p input
    Just (input', f x)

instance Applicative Parser where
  pure x = Parser $ \input -> Just (input, x)
  (Parser p1) <*> (Parser p2) = Parser $ \input -> do
    (input', f) <- p1 input
    (input'', x) <- p2 input'
    Just (input'', f x)

instance Alternative Parser where
  empty :: Parser a
  empty = Parser $ \_ -> Nothing
  (<|>) :: Parser a -> Parser a -> Parser a
  (<|>) (Parser p1) (Parser p2) = Parser $ \input -> do
    p1 input <|> p2 input

jsonNull :: Parser JsonValue
jsonNull = JsonNull <$ stringP "null"

jsonBool :: Parser JsonValue
jsonBool = (\_ -> JsonBool True) <$> stringP "true" <|> (\_ -> JsonBool False) <$> stringP "false"

jsonNumber :: Parser JsonValue
jsonNumber = f <$> notNull (spanP isDigit)
  where
    f ds = JsonNumber $ read ds

jsonValue :: Parser JsonValue
jsonValue = jsonNull <|> jsonBool <|> jsonNumber <|> jsonString <|> jsonArray <|> jsonObject

jsonString :: Parser JsonValue
jsonString = JsonString <$> (charP '"' *> stringLiteral <* charP '"')

sepBy :: Parser a -> Parser b -> Parser [b]
sepBy sep element =
  (:) <$> element <*> many (sep *> element)
    <|> pure []

jsonArray :: Parser JsonValue
jsonArray = JsonArray <$> (charP '[' *> ws *> elements <* ws <* charP ']')
  where
    elements = sepBy (ws *> charP ',' <* ws) jsonValue

jsonObject :: Parser JsonValue
jsonObject = JsonObject <$> (charP '{' *> ws *> sepBy (ws *> charP ',' <* ws) pair <* ws <* charP '}')
  where
    pair =
      (\key _ value -> (key, value))
        <$> stringLiteral
        <*> (ws *> charP ':' <* ws)
        <*> jsonValue

stringLiteral :: Parser String
stringLiteral = spanP (/= '"')

ws :: Parser String
ws = spanP isSpace

spanP :: (Char -> Bool) -> Parser String
spanP f = Parser $ \input ->
  let (token, rest) = span f input
   in Just (rest, token)

notNull :: Parser [a] -> Parser [a]
notNull (Parser p) = Parser $ \input -> do
  (input', xs) <- p input
  if null xs
    then Nothing
    else Just (input', xs)

charP :: Char -> Parser Char
charP c = Parser f
  where
    f (y : ys)
      | y == c = Just (ys, c)
      | otherwise = Nothing
    f [] = Nothing

stringP :: String -> Parser String
stringP = sequenceA . map charP

main :: IO ()
main = undefined