module LogAnalysis where

import Log

parseMessage :: String -> LogMessage
parse :: String -> [LogMessage]
insert :: LogMessage -> MessageTree -> MessageTree
build :: [LogMessage] -> MessageTree
inOrder :: MessageTree -> [LogMessage]

parseMessage message = case words message of
    ("I" : timestamp : rest) -> parseInfoMessage timestamp (unwords rest)
    ("W" : timestamp : rest) -> parseWarnMessage timestamp (unwords rest)
    ("E" : severity : timestamp : rest) -> parseErrorMessage severity timestamp (unwords rest)
    _ -> Unknown message

parseInfoMessage :: String -> String -> LogMessage
parseInfoMessage timestamp msg = LogMessage Info (read timestamp) msg

parseWarnMessage :: String -> String -> LogMessage
parseWarnMessage timestamp msg = LogMessage Warning (read timestamp) msg

parseErrorMessage :: String -> String -> String -> LogMessage
parseErrorMessage severity timestamp msg = LogMessage (Error (read severity))  (read timestamp) msg

-- parse (message : '\n' : messages) = [parseMessage message] : parse messages

parse logs = map parseMessage (lines logs)

insert (Unknown _) tree = tree
insert logMsg Leaf = Node Leaf logMsg Leaf
insert logMsg@(LogMessage _ ts _) (Node left nodeMsg@(LogMessage _ nodeTs _) right)
    | ts < nodeTs = Node (insert logMsg left) nodeMsg right
    | otherwise = Node left nodeMsg (insert logMsg right)

build [] = Leaf
build (log: rest) = insert log (build rest)

inOrder Leaf = []
inOrder (Node left logMsg right) = inOrder left ++ [logMsg] ++ inOrder right
