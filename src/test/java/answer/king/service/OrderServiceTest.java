package answer.king.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

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
}
