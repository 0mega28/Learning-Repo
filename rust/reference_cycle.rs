use std::rc::Rc;
use std::cell::RefCell;

/**
 *                /—————————————\
 *               |               V
 *              [A]             [B]
 *               ^               |
 *                \_____________/
 * 
 * 
 * - Running the below program won't print `Data Dropped`
 */


struct Data {
    pub data: Option<Rc<RefCell<Data>>>,
}

impl Drop for Data {
    fn drop(&mut self) {
        println!("Data Dropped");
    }

}

fn main() {
    let data = Data{ data: None };
    let ref_to_data = Rc::new(RefCell::new(data));

    let another_data = Data { data: None };
    let ref_to_another_data = Rc::new(RefCell::new(another_data));

    let _ = ref_to_data.borrow_mut().data.insert(ref_to_another_data.clone());
    let _ = ref_to_another_data.borrow_mut().data.insert(ref_to_data.clone());

}
