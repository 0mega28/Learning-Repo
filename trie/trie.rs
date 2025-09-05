struct Trie {
    children: Vec<Option<Box<Trie>>>,
    is_leaf: bool
}

/**
 * `&self` means the method takes an immutable reference.
 * If you need a mutable reference, change it to `&mut self` instead.
 */
impl Trie {
    fn new() -> Self {
        Self {
            children    : (0..26).map(|_| None).collect(),
            is_leaf     : false
        }
    }

    fn idx(chr: char) -> usize {
        ((chr as u8) - ('a' as u8)) as usize
    }

    fn insert(&mut self, word: String) {
        let mut node = self;

        for chr in word.chars() {
            let idx = Self::idx(chr);
            if node.children[idx].is_none() {
                node.children[idx] = Some(Box::new(Trie::new()));
            }
            
            node = node.children[idx].as_mut().unwrap();
        }

        node.is_leaf = true;
    }

    fn search(&self, word: String) -> bool {
        let mut node = self;

        for chr in word.chars() {
            let idx = Self::idx(chr);
            if node.children[idx].is_none() {
                return false;
            }
            
            node = node.children[idx].as_ref().unwrap();
        }

        return node.is_leaf;
    }

    fn starts_with(&self, prefix: String) -> bool {
        let mut node = self;

        for chr in prefix.chars() {
            let idx = Self::idx(chr);
            if node.children[idx].is_none() {
                return false;
            }
            
            node = node.children[idx].as_ref().unwrap();
        }

        return true;
    }
}

fn main() {
    let mut trie = Trie::new();
    trie.insert("hello".to_string());
    trie.insert("world".to_string());
    

    assert!(trie.search("hello".to_string())            , "Trie Contains `hello`");
    assert!(trie.search("world".to_string())            , "Trie Contains `world`");
    assert!(!trie.search("banana".to_string())          , "Trie Doesn't Contains `banana`");
    assert!(!trie.search("hell".to_string())            , "Trie Doesn't Contains `hell`");
    assert!(trie.starts_with("hel".to_string())         , "Trie Startswith `hel`");
    assert!(!trie.starts_with("heml".to_string())       , "Trie Doesn't Startswith `heml`");
    assert!(!trie.starts_with("hellos".to_string())     , "Trie Doesn't Startswith `hellos`");
}
