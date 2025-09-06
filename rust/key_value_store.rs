mod store {
    use std::collections::HashMap;
    use std::fmt::Debug;
    use std::hash::Hash;
    use std::sync::Mutex;

    #[derive(PartialEq, Debug)]
    pub enum Error {
        NonExisting,
    }

    pub struct Store<K, V>
    where
        K: Eq + Hash,
        V: Clone + Debug,
    {
        store: Mutex<HashMap<K, V>>,
    }

    impl<K, V> Store<K, V>
    where
        K: Eq + Hash,
        V: Clone + Debug,
    {
        pub fn new() -> Self {
            Self {
                store: Mutex::new(HashMap::new()),
            }
        }

        pub fn set(&mut self, key: K, value: V) -> Option<V> {
            self.store
                .lock().unwrap()
                .insert(key, value)
        }

        pub fn get(&self, key: &K) -> Result<V, Error> {
            self.store
                .lock().unwrap()
                .get(key)
                .map_or_else(|| Err(Error::NonExisting), |v| Ok(v.to_owned()))
        }

        pub fn delete(&mut self, key: &K) -> Result<V, Error> {
            self.store
                .lock().unwrap()
                .remove(key)
                .map_or_else(|| Err(Error::NonExisting), |v| Ok(v))
        }
    }
}

#[cfg(test)]
mod test {
    use super::store::Error;
    use super::store::Store;
    #[test]
    fn test() {
        let mut kv_store = Store::new();
        kv_store.set("hello".to_owned(), "world".to_owned());

        assert_eq!(kv_store.get(&"hello".to_owned()), Ok("world".to_owned()));
        assert_ne!(kv_store.get(&"hello".to_owned()), Ok("value".to_owned()));
        assert_eq!(kv_store.get(&"world".to_owned()), Err(Error::NonExisting));

        let old_value = kv_store.set("hello".to_owned(), "sentient".to_owned());
        assert_eq!(old_value, Some("world".to_owned()));
        assert_ne!(kv_store.get(&"hello".to_owned()), Ok("world".to_owned()));
        assert_eq!(kv_store.get(&"hello".to_owned()), Ok("sentient".to_owned()));

        let removed_value = kv_store.delete(&"hello".to_owned());
        assert_eq!(removed_value, Ok("sentient".to_owned()));
        assert_eq!(kv_store.get(&"hello".to_owned()), Err(Error::NonExisting));

        assert_eq!(kv_store.delete(&"test".to_owned()), Err(Error::NonExisting));
    }
}
