package answer.king.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import answer.king.model.Item;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.model.exception.InvalidOrderException;
import answer.king.repo.OrderRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderServiceTest {

	@Autowired
    private OrderService orderService;
	
	@MockBean
	private OrderRepository orderRepository;
    
	@Test
	public void valid_order_id_and_payment_gets_valid_receipt() throws Throwable {
	
		//	given an existing order id and a payment amount
		Long existingOrderId = 8L;
		BigDecimal payment = new BigDecimal(67.18);

		// set mock behaviour
		Order returnOrder = new Order();
		returnOrder.setId(existingOrderId);
		when(orderRepository.findOne(existingOrderId)).thenReturn(returnOrder);
		when(orderRepository.save(any(Order.class))).thenReturn(returnOrder);
		
		//	when pay is called
		Receipt receipt = orderService.pay(existingOrderId, payment);
		
		//	then the receipt contains the corresponding order and the payment
		assertEquals(existingOrderId, receipt.getOrder().getId());
		assertEquals(payment, receipt.getPayment());
	}
	
	@Test(expected=InvalidOrderException.class)
	public void invalid_order_id_exception_thrown() throws Throwable {
		
		//	given a non-existing order id and a payment amount
		Long nonExistingOrderId = 8L;
		BigDecimal payment = new BigDecimal(67.18);
		
		// set mock behaviour
		when(orderRepository.findOne(nonExistingOrderId)).thenReturn(null);
		
		//	when pay is called
		orderService.pay(nonExistingOrderId, payment);
		
		//	then an InvalidOrderException exception is thrown
	}
	
	@Test
	public void when_order_is_paid_it_is_marked_as_paid() throws Throwable {

		ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
		
		//	given an existing order id and a payment amount
		Long existingOrderId = 12L;
		BigDecimal payment = new BigDecimal(89.18);

		// set mock behaviour
		Order returnOrder = new Order();
		returnOrder.setId(existingOrderId);
		when(orderRepository.findOne(existingOrderId)).thenReturn(returnOrder);
		when(orderRepository.save(orderCaptor.capture())).thenReturn(returnOrder);
		
		//	when pay is called
		Receipt receipt = orderService.pay(existingOrderId, payment);
		
		//	then the order, marked as paid, is saved to the order repository
		assertEquals(existingOrderId, orderCaptor.getValue().getId());
		assertTrue(orderCaptor.getValue().getPaid());
		
		//	and the receipt contains the corresponding order which is marked as paid
		assertTrue(receipt.getOrder().getPaid());
	}
	
	@Test(expected=InvalidOrderException.class) 
	public void when_insufficient_payment_is_offered_exception_thrown() throws Throwable {

		//	given an existing order id whose order contains items whose prices sum to more than the payment amount
		Long existingOrderId = 17L;
		BigDecimal payment = new BigDecimal(20);
		Order order = new Order();
		List<Item> items = new ArrayList<>();
		Item itemOne = new Item();
		itemOne.setPrice(new BigDecimal(12));
		items.add(itemOne);
		Item itemTwo = new Item();
		itemTwo.setPrice(new BigDecimal(9));
		items.add(itemTwo);
		order.setItems(items);
		
		//	set mock behaviour
		when(orderRepository.findOne(existingOrderId)).thenReturn(order);
		
		//	when pay is called
		orderService.pay(existingOrderId, payment);
		
		//	then an InvalidOrderException exception is thrown
	}
}
