package answer.king.validator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import answer.king.model.LineItem;
import answer.king.model.Order;
import answer.king.model.exception.InvalidOrderException;

public class OrderValidatorTest {

	private final OrderValidator validator = new OrderValidator();
	
	@Test(expected=InvalidOrderException.class)
	public void payment_is_not_possible_when_price_is_more_than_payment() throws Throwable {
	
		//	given an order containing one line item whose price is higher than the payment offered
		BigDecimal payment = new BigDecimal(10);
		LineItem lineItem = new LineItem();
		lineItem.setPrice(new BigDecimal(12));
		Order order = new Order();
		order.setLineItems(Collections.singletonList(lineItem));
		
		//	when the order is validated
		validator.validate(order, payment);
		
		//	then InvalidOrderException is thrown
	}
	
	@Test(expected=InvalidOrderException.class) 
	public void payment_is_not_possible_when_sum_of_item_prices_is_more_than_payment() throws Throwable {

		//	given an order containing three line items whose prices, when summed, are higher than the payment offered
		List<LineItem> lineItems = new ArrayList<>();
		BigDecimal payment = new BigDecimal(10);
		LineItem lineItemOne = new LineItem();
		lineItemOne.setPrice(new BigDecimal(3));
		lineItems.add(lineItemOne);
		LineItem lineItemTwo = new LineItem();
		lineItemTwo.setPrice(new BigDecimal(6));
		lineItems.add(lineItemTwo);
		LineItem lineItemThree = new LineItem();
		lineItemThree.setPrice(new BigDecimal(2));
		lineItems.add(lineItemThree);
		Order order = new Order();
		order.setLineItems(lineItems);
		
		//	when the order is validated
		validator.validate(order, payment);
		
		//	then InvalidOrderException is thrown
	}
	
	@Test 
	public void payment_is_possible_when_sum_of_item_prices_is_less_than_payment() throws Throwable {

		//	given an order containing three line items whose prices, when summed, are lower than the payment offered
		List<LineItem> lineItems = new ArrayList<>();
		BigDecimal payment = new BigDecimal(12);
		LineItem lineItemOne = new LineItem();
		lineItemOne.setPrice(new BigDecimal(3));
		lineItems.add(lineItemOne);
		LineItem lineItemTwo = new LineItem();
		lineItemTwo.setPrice(new BigDecimal(6));
		lineItems.add(lineItemTwo);
		LineItem lineItemThree = new LineItem();
		lineItemThree.setPrice(new BigDecimal(2));
		lineItems.add(lineItemThree);
		Order order = new Order();
		order.setLineItems(lineItems);
		
		//	when the order is validated
		validator.validate(order, payment);
		
		//	then no Exception is thrown
	}
	
	@Test 
	public void payment_is_possible_when_sum_of_item_prices_is_equal_to_payment() throws Throwable {

		//	given an order containing three items whose prices, when summed, are equal to the payment offered
		List<LineItem> lineItems = new ArrayList<>();
		BigDecimal payment = new BigDecimal(15);
		LineItem lineItemOne = new LineItem();
		lineItemOne.setPrice(new BigDecimal(4));
		lineItems.add(lineItemOne);
		LineItem lineItemTwo = new LineItem();
		lineItemTwo.setPrice(new BigDecimal(5));
		lineItems.add(lineItemTwo);
		LineItem lineItemThree = new LineItem();
		lineItemThree.setPrice(new BigDecimal(6));
		lineItems.add(lineItemThree);
		Order order = new Order();
		order.setLineItems(lineItems);
		
		//	when the order is validated
		validator.validate(order, payment);
		
		//	then no Exception is thrown
	}
}
