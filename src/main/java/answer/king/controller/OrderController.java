package answer.king.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.model.exception.InvalidOrderException;
import answer.king.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@RequestMapping(method = RequestMethod.GET)
	public List<Order> getAll() {
		return orderService.getAll();
	}

	@RequestMapping(method = RequestMethod.POST)
	public Order create() {
		return orderService.save(new Order());
	}

	@RequestMapping(value = "/{id}/addItem/{itemId}/{quantity}", method = RequestMethod.PUT)
	public void addItem(@PathVariable("id") Long id, @PathVariable("itemId") Long itemId,  @PathVariable("quantity") int quantity) {
		orderService.addItem(id, itemId, quantity);
	}

	@RequestMapping(value = "/{id}/pay", method = RequestMethod.PUT)
	public Receipt pay(@PathVariable("id") Long id, @RequestBody BigDecimal payment) throws InvalidOrderException {
		return orderService.pay(id, payment);
	}
	
	//	Ensures that a 400 bad request response is sent if an invalid order exception is thrown
	@ExceptionHandler({InvalidOrderException.class})
	void handleBadRequests(HttpServletResponse response) throws IOException {
	    response.sendError(HttpStatus.BAD_REQUEST.value());
	}
}
