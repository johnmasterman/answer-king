package answer.king.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import answer.king.model.Item;
import answer.king.repo.ItemRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ItemServiceTest {

	@Autowired
    private ItemService itemService;
	
	@MockBean
    private ItemRepository itemRepository;
    
	@Test
	public void save_item_returns_an_item() {
	
		//	given an item
		Item item = new Item();
		
		when(itemRepository.save(any(Item.class))).thenReturn(item);	// mock repository return value 
		
		//	when save is called with that item
		Item returnedItem = itemService.save(item);
		
		//	then an item is returned (though it may not necessarily be the same one)
		assertNotNull(returnedItem);
		assertEquals(Item.class, returnedItem.getClass());
	}
	
	@Test
    public void saving_item_causes_invocation_of_repository_save_method_with_item() throws Throwable {
		
		ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
		
		//	given an item
		Item item = new Item();
		
		when(itemRepository.save(itemCaptor.capture())).thenReturn(null);	//	capture the argument but don't care about return value

		//	when save is called with that item
		itemService.save(item);
		
    	//	then the repository save method is called with the item
    	assertEquals(item, itemCaptor.getValue());
	}
}
