package answer.king.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import answer.king.model.Item;
import answer.king.model.exception.InvalidItemException;
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
	
	@Test(expected=InvalidItemException.class)
	public void unknown_item_on_price_change_throws_exception() throws Throwable {
	
		//	given an id for an item that does not exist
		Long nonExistentItemId = new Long(9);
		
		//	when the changeItemPrice method is called
		itemService.changeItemPrice(nonExistentItemId, new BigDecimal(66.78));
		
		//	then an InvalidItemException is called
	}
	
	@Test 
	public void updating_item_price_returns_item_with_new_price() throws Throwable{
	
		// given an id for an item that does exist and a new price
		Long existingItemId = new Long(78);
		BigDecimal originalPrice = new BigDecimal(67.24);
		BigDecimal newPrice = new BigDecimal(67.34);
		
		//	set mocks
		Item existingItem = new Item();
		existingItem.setId(existingItemId);
		existingItem.setPrice(originalPrice);
		when(itemRepository.findOne(existingItemId)).thenReturn(existingItem);
		
		when(itemRepository.save(any(Item.class))).thenAnswer(
            new Answer<Item>() {
	            @Override
	            public Item answer(InvocationOnMock invocation){
	                return invocation.getArgumentAt(0, Item.class);
            }});	// return the item that is saved
		
		//	when the changeItemPrice method is called
		Item updatedItem = itemService.changeItemPrice(existingItemId, newPrice);
		
		//	then the item has the new price	
		assertEquals(newPrice, updatedItem.getPrice());
	}
	
	@Test 
	public void updating_item_price_invokes_item_repository_save_with_new_price() throws Throwable{
	
		ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
		
		// given an id for an item that does exist and a new price
		Long existingItemId = new Long(78);
		BigDecimal originalPrice = new BigDecimal(67.24);
		BigDecimal newPrice = new BigDecimal(67.34);
		
		//	set mocks
		Item existingItem = new Item();
		existingItem.setId(existingItemId);
		existingItem.setPrice(originalPrice);
		when(itemRepository.findOne(existingItemId)).thenReturn(existingItem);
		when(itemRepository.save(itemCaptor.capture())).thenReturn(null);	//	capture the argument but don't care about return value
		
		//	when the changeItemPrice method is called
		itemService.changeItemPrice(existingItemId, newPrice);
		
		//	then the item repository save method has been called with an item with the new price
		assertEquals(newPrice, itemCaptor.getValue().getPrice());
	}
}
