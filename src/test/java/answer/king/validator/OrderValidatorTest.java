package answer.king.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import answer.king.model.Item;
import answer.king.model.Order;
import answer.king.model.exception.InvalidOrderException;

public class OrderValidatorTest {

	private final OrderValidator validator = new OrderValidator();
	
	@Test(expected=InvalidOrderException.class)
	public void payment_is_not_possible_when_price_is_more_than_payment() throws Throwable {
	
		//	given an order containing one item whose price is higher than the payment offered
		BigDecimal payment = new BigDecimal(10);
		Item item = new Item();
		item.setPrice(new BigDecimal(12));
		Order order = new Order();
		order.setItems(Collections.singletonList(item));
		
		//	when the order is validated
		validator.validate(order, payment);
		
		//	then InvalidOrderException is thrown
	}
	
	@Test(expected=InvalidOrderException.class) 
	public void payment_is_not_possible_when_sum_of_item_prices_is_more_than_payment() throws Throwable {

		//	given an order containing three items whose prices, when summed, are higher than the payment offered
		List<Item> items = new ArrayList<>();
		BigDecimal payment = new BigDecimal(10);
		Item itemOne = new Item();
		itemOne.setPrice(new BigDecimal(3));
		items.add(itemOne);
		Item itemTwo = new Item();
		itemTwo.setPrice(new BigDecimal(6));
		items.add(itemTwo);
		Item itemThree = new Item();
		itemThree.setPrice(new BigDecimal(2));
		items.add(itemThree);
		Order order = new Order();
		order.setItems(items);
		
		//	when the order is validated
		validator.validate(order, payment);
		
		//	then InvalidOrderException is thrown
	}
	
	@Test 
	public void payment_is_possible_when_sum_of_item_prices_is_less_than_payment() throws Throwable {

		//	given an order containing three items whose prices, when summed, are lower than the payment offered
		List<Item> items = new ArrayList<>();
		BigDecimal payment = new BigDecimal(12);
		Item itemOne = new Item();
		itemOne.setPrice(new BigDecimal(3));
		items.add(itemOne);
		Item itemTwo = new Item();
		itemTwo.setPrice(new BigDecimal(6));
		items.add(itemTwo);
		Item itemThree = new Item();
		itemThree.setPrice(new BigDecimal(2));
		items.add(itemThree);
		Order order = new Order();
		order.setItems(items);
		
		//	when the order is validated
		validator.validate(order, payment);
		
		//	then no Exception is thrown
	}
	
	@Test 
	public void payment_is_possible_when_sum_of_item_prices_is_equal_to_payment() throws Throwable {

		//	given an order containing three items whose prices, when summed, are equal to the payment offered
		List<Item> items = new ArrayList<>();
		BigDecimal payment = new BigDecimal(15);
		Item itemOne = new Item();
		itemOne.setPrice(new BigDecimal(4));
		items.add(itemOne);
		Item itemTwo = new Item();
		itemTwo.setPrice(new BigDecimal(5));
		items.add(itemTwo);
		Item itemThree = new Item();
		itemThree.setPrice(new BigDecimal(6));
		items.add(itemThree);
		Order order = new Order();
		order.setItems(items);
		
		//	when the order is validated
		validator.validate(order, payment);
		
		//	then no Exception is thrown
	}
}
