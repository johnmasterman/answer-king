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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.model.exception.InvalidOrderException;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;
import answer.king.repo.ReceiptRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class OrderServiceTest {

	@Autowired
    private OrderService orderService;
	
	@MockBean
	private OrderRepository orderRepository;
	
	@MockBean
	private ItemRepository itemRepository;
	
	@MockBean
	private ReceiptRepository receiptRepository;
    
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
		
		when(receiptRepository.save(any(Receipt.class))).thenAnswer(
            new Answer<Receipt>(){
            @Override
            public Receipt answer(InvocationOnMock invocation){
                return invocation.getArgumentAt(0, Receipt.class);
            }});
		
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
		
		when(receiptRepository.save(any(Receipt.class))).thenAnswer(
            new Answer<Receipt>(){
            @Override
            public Receipt answer(InvocationOnMock invocation){
                return invocation.getArgumentAt(0, Receipt.class);
            }});
			
		
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
		List<LineItem> lineItems = new ArrayList<>();
		LineItem itemOne = new LineItem();
		itemOne.setPrice(new BigDecimal(12));
		lineItems.add(itemOne);
		LineItem itemTwo = new LineItem();
		itemTwo.setPrice(new BigDecimal(9));
		lineItems.add(itemTwo);
		order.setLineItems(lineItems);
		
		//	set mock behaviour
		when(orderRepository.findOne(existingOrderId)).thenReturn(order);
		
		//	when pay is called
		orderService.pay(existingOrderId, payment);
		
		//	then an InvalidOrderException exception is thrown
	}
	
	@Test
	public void line_item_added_to_existing_order_when_add_item_called() {
		
		ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
		
		//	given an existing order id, and item id whose item price is known
		Long orderId = 17L;
		Long itemId = 21L;
		BigDecimal itemPrice = new BigDecimal(94.70);
		
		//	setup mocks
		Order order = new Order();
		order.setId(orderId);
		Item item = new Item();
		item.setId(itemId);
		item.setName("itemName");
		item.setPrice(itemPrice);
		when(orderRepository.findOne(orderId)).thenReturn(order);
		when(itemRepository.findOne(itemId)).thenReturn(item);
		when(orderRepository.save(orderCaptor.capture())).thenReturn(null);	//	Don't care what is returned
		
		//	when add item is called
		orderService.addItem(orderId, itemId, 1);
				
		//	then a line item is created which is persisted when the order is persisted
		Order savedOrder = orderCaptor.getValue();
		assertEquals(orderId, savedOrder.getId());
		LineItem lineItem = savedOrder.getLineItems().get(0);
		assertEquals(itemId, lineItem.getItem().getId());
		assertEquals(itemPrice, lineItem.getPrice());
		assertEquals("itemName", lineItem.getItem().getName());
	}
	
	@Test
	public void item_added_to_order_with_quantity_two_saves_order_with_two_items() {
		
		ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
		
		//	given an existing order id, and item id 
		Long orderId = 37L;
		Long itemId = 28L;
		BigDecimal itemPrice = new BigDecimal(10.99);
		String itemName = "Banana";
		
		//	setup mocks
		Order order = new Order();
		order.setId(orderId);
		Item item = new Item();
		item.setId(itemId);
		item.setName(itemName);
		item.setPrice(itemPrice);
		when(orderRepository.findOne(orderId)).thenReturn(order);
		when(itemRepository.findOne(itemId)).thenReturn(item);
		when(orderRepository.save(orderCaptor.capture())).thenReturn(null);	//	Don't care what is returned
		
		//	when the item is added to the order with a quantity of 2
		orderService.addItem(orderId, itemId, 2);
		
		//	then the order is saved with a line item with quantity 2
		//	then a line item is created which is persisted when the order is persisted
		Order savedOrder = orderCaptor.getValue();
		assertEquals(orderId, savedOrder.getId());
		LineItem lineItem = savedOrder.getLineItems().get(0);
		assertEquals(itemId, lineItem.getItem().getId());
		assertEquals(itemPrice, lineItem.getPrice());
		assertEquals(itemName, lineItem.getItem().getName());
		assertEquals(2, lineItem.getQuantity());
	}
	
	@Test
	public void two_bananas_added_to_order_which_has_3_bananas_and_2_apples_sums_bananas() {
	
		ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
		
		//	given an existing order with 3 bananas and 2 apples
		Long orderId = new Long(5382046);
	
		Long bananaId = new Long(1);
		String banana = "banana";
		BigDecimal bananaPrice = new BigDecimal(0.15);
		Item bananaItem = new Item();
		bananaItem.setId(bananaId);
		bananaItem.setName(banana);
		LineItem bananaLineItem = new LineItem(bananaPrice, bananaItem, 3);
		
		Long appleId = new Long(2);
		String apple = "apple";
		BigDecimal applePrice = new BigDecimal(0.20);
		Item appleItem = new Item();
		appleItem.setId(appleId);
		appleItem.setName(apple);
		LineItem appleLineItem = new LineItem(applePrice, appleItem, 2);
		
		List<LineItem> fruitAlreadyOnOrder = new ArrayList<>();
		fruitAlreadyOnOrder.add(bananaLineItem);
		fruitAlreadyOnOrder.add(appleLineItem);
		
		Order order = new Order();
		order.setLineItems(fruitAlreadyOnOrder);
		
		//	setup mocks
		when(orderRepository.findOne(orderId)).thenReturn(order);
		when(itemRepository.findOne(bananaId)).thenReturn(bananaItem);
		when(orderRepository.save(orderCaptor.capture())).thenReturn(null);	//	Don't care what is returned
		
		//	when two bananas are added to order
		orderService.addItem(orderId, bananaId, 2);
		
		//	then the order is for 5 bananas and 2 apples
		Order savedOrder = orderCaptor.getValue();
		List<LineItem> lineItems = savedOrder.getLineItems();
		LineItem lineItemOne = lineItems.get(0);
		LineItem lineItemTwo = lineItems.get(1);
		LineItem capturedBananaLineItem = lineItemOne.getItem().getName().equals(banana) ? lineItemOne : lineItemTwo;
		LineItem capturedAppleLineItem = lineItemOne.getItem().getName().equals(apple) ? lineItemOne : lineItemTwo;
		
		assertEquals(5, capturedBananaLineItem.getQuantity());
		assertEquals(bananaPrice, capturedBananaLineItem.getPrice());
		assertEquals(2, capturedAppleLineItem.getQuantity());
		assertEquals(applePrice, capturedAppleLineItem.getPrice());
	}
}
