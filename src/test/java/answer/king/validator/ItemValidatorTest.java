package answer.king.validator;

import java.math.BigDecimal;

import org.junit.Test;

import answer.king.model.Item;

/**
 * Item is 
 * 
 * @author John
 *
 */
public class ItemValidatorTest {

	private final ItemValidator validator = new ItemValidator();
	
	@Test
	public void item_with_name_and_price_is_valid() throws Throwable{
		
		//	given a valid item
		Item item = new Item();
		item.setName("name");
		item.setPrice(new BigDecimal(37.96));
		
		//	when the item is validated
		validator.validate(item);
		
		//	then no exception is thrown
	}
	
	@Test(expected=InvalidItemException.class)
	public void null_item_validation_exception_thrown() throws Throwable {
	
		//	when null is validated as an item
		validator.validate(null);
		
		//	then an InvalidItemException exception is thrown
	}
	
	@Test(expected=InvalidItemException.class)
	public void item_with_null_name_validation_exception_thrown() throws Throwable {
	
		//	given an item with no name
		Item item = new Item();
		item.setPrice(new BigDecimal(37.96));
		
		//	when the item is validated
		validator.validate(item);
		
		//	then an InvalidItemException exception is thrown
	}
	
	@Test(expected=InvalidItemException.class)
	public void item_with_empty_name_validation_exception_thrown() throws Throwable {
	
		//	given an item with no name
		Item item = new Item();
		item.setPrice(new BigDecimal(37.96));
		item.setName("");
		
		//	when the item is validated
		validator.validate(item);
		
		//	then an InvalidItemException exception is thrown
	}
	
	@Test(expected=InvalidItemException.class)
	public void item_with_null_price_validation_exception_thrown() throws Throwable {
	
		//	given an item with no price
		Item item = new Item();
		item.setName("name");
		
		//	when the item is validated
		validator.validate(item);
		
		//	then an InvalidItemException exception is thrown
	}
	
	@Test(expected=InvalidItemException.class)
	public void item_with_negative_price_validation_exception_thrown() throws Throwable {

		//	given an item with a negative price
		Item item = new Item();
		item.setName("name");
		item.setPrice(new BigDecimal(-37.96));
		
		//	when the item is validated
		validator.validate(item);
		
		//	then an InvalidItemException exception is thrown
	}
	
	@Test
	public void item_with_zero_price_is_valid() throws Throwable {

		//	given an item with a price of 0
		Item item = new Item();
		item.setName("name");
		item.setPrice(new BigDecimal(0));
		
		//	when the item is validated
		validator.validate(item);
		
		//	then no exception is thrown
	}
}
