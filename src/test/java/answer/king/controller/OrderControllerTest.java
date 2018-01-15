package answer.king.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.model.exception.InvalidOrderException;
import answer.king.service.OrderService;

@RunWith(SpringRunner.class)
@WebMvcTest(OrderController.class)
public class OrderControllerTest {

	@Autowired
    private MockMvc mvc;
 
    @MockBean
    private OrderService orderService;
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void putting_order_id_and_payment_gets_receipt_for_order() throws Throwable {
    	
    	//	given an order id and payment
    	Long orderId = new Long(45);
    	BigDecimal payment = new BigDecimal(67);
    	
    	//	set mock behaviour
    	Receipt receipt = new Receipt();
    	receipt.setPayment(payment);
    	Order order = new Order();
    	order.setId(orderId);
    	order.setLineItems(Collections.emptyList());
    	receipt.setOrder(order);
    	when(orderService.pay(orderId, payment)).thenReturn(receipt);
    	
    	//	when put is called on the pay endpoint with order id and payment
    	mvc.perform(put("/order/" + orderId + "/pay").content(mapper.writeValueAsString(payment))
        	.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    	
    	//	then the order id is contained in the receipt returned in the response
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.order.id", is(orderId.intValue())))
			.andExpect(jsonPath("$.payment", is(67)));
    }
    
    @Test
    public void putting_order_id_and_payment_causes_invocation_of_service_pay_method() throws Throwable {
 
    	ArgumentCaptor<Long> orderIdCaptor = ArgumentCaptor.forClass(Long.class);
    	ArgumentCaptor<BigDecimal> paymentCaptor = ArgumentCaptor.forClass(BigDecimal.class);
    	
    	//	given an order id and payment
    	Long orderId = new Long(45);
    	BigDecimal payment = new BigDecimal(67);
    	
    	//	set mock behaviour
    	Receipt receipt = new Receipt();
    	receipt.setPayment(payment);
    	Order order = new Order();
    	order.setId(orderId);
    	order.setLineItems(Collections.emptyList());
    	receipt.setOrder(order);
    	when(orderService.pay(orderIdCaptor.capture(), paymentCaptor.capture())).thenReturn(receipt);
    	
    	//	when put is called on the pay endpoint with order id and payment
    	mvc.perform(put("/order/" + orderId + "/pay").content(mapper.writeValueAsString(payment))
        	.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
    	
    	//	then the order service pay method is called with the order id and payment
    	assertEquals(orderId, orderIdCaptor.getValue());
    	assertEquals(payment, paymentCaptor.getValue());
    }
    
    @Test
    public void putting_invalid_order_id_returns_bad_request() throws Throwable {

    	//	given an invalid order id and a payment
    	Long orderId = new Long(45);
    	BigDecimal payment = new BigDecimal(67);
    	
    	//	set mock behaviour
    	when(orderService.pay(orderId, payment)).thenThrow(new InvalidOrderException());
    	
    	//	when put is called on the pay endpoint with order id and payment
    	mvc.perform(put("/order/" + orderId + "/pay").content(mapper.writeValueAsString(payment))
        	.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    	
    	//	then a bad request status code is returned
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void create_order_with_no_items_creates_new_order() throws Throwable{
 
    	//	set mock behaviour
    	Order order = new Order();
    	when(orderService.save(any(Order.class))).thenReturn(order);
    	
    	//	when post is called on the order endpoint 
    	mvc.perform(post("/order").accept(MediaType.APPLICATION_JSON))
    	
    	//	then an order is returned
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$.id", is(order.getId())));
    }
    
    @Test
    public void add_item_to_existing_order_calls_item_service_add_item() throws Throwable {
    	
    	ArgumentCaptor<Long> orderIdCaptor = ArgumentCaptor.forClass(Long.class);
    	ArgumentCaptor<Long> itemIdCaptor = ArgumentCaptor.forClass(Long.class);
    	
    	//	given an existing order with no items
    	Long existingOrderId = new Long(2);
    	Long itemId = new Long(79);
    	
    	//	set mocks  	
    	doNothing().when(orderService).addItem(orderIdCaptor.capture(), itemIdCaptor.capture());
    	
    	//	when an item is added to the order
    	mvc.perform(put("/order/" + existingOrderId + "/addItem/" + itemId)
            .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON));
    	
    	//	then the item service is called with the order id and the item id
    	assertEquals(existingOrderId, orderIdCaptor.getValue());
    	assertEquals(itemId, itemIdCaptor.getValue());
    }
}
