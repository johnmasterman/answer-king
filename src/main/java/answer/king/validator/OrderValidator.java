package answer.king.validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import answer.king.model.Item;
import answer.king.model.Order;
import answer.king.model.exception.InvalidOrderException;

public class OrderValidator {

	public void validate(Order order, BigDecimal payment) throws InvalidOrderException {
	
		BigDecimal orderCost = calculateOrderCost(order);
		if (orderCost.compareTo(payment) > 0) {
			throw new InvalidOrderException("Order cost " + orderCost + " is higher than payment " + payment);
		}
	}
	
	private BigDecimal calculateOrderCost(Order order) {
	
		List<Item> items = order.getItems();
		if (items == null) {
			return BigDecimal.ZERO;
		} else {
			Function<Item, BigDecimal> itemToPrice = item -> item.getPrice();
			return order.getItems().stream().map(itemToPrice).filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
		}
	}
}
