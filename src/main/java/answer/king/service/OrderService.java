package answer.king.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import answer.king.model.Item;
import answer.king.model.LineItem;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.model.exception.InvalidOrderException;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;
import answer.king.validator.OrderValidator;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ItemRepository itemRepository;
	
	@Autowired
	ReceiptService receiptService;
	
	private OrderValidator orderValidator = new OrderValidator();

	public List<Order> getAll() {
		return orderRepository.findAll();
	}

	public Order save(Order order) {
		return orderRepository.save(order);
	}

	public void addItem(Long orderId, Long itemId, int quantity) {
		Order order = orderRepository.findOne(orderId);
		Item item = itemRepository.findOne(itemId);
		LineItem lineItem = new LineItem(item.getPrice(), item, quantity);

		List<LineItem> lineItems = order.getLineItems();
		if (lineItems == null)
		{
			lineItems = new ArrayList<>();
			order.setLineItems(lineItems);
		}
		
		LineItem existingLineItemForItem = getExistingLineItemForItemIfExists(itemId, lineItems);
		if (existingLineItemForItem == null) {
			lineItems.add(lineItem);
		} else {
			int newQuantity = existingLineItemForItem.getQuantity() + quantity;
			existingLineItemForItem.setQuantity(newQuantity);
		}
		

		orderRepository.save(order);
	}
	
	private LineItem getExistingLineItemForItemIfExists(Long itemId, List<LineItem> lineItems) {
	
		for (LineItem lineItem : lineItems) {
			if (itemId.equals(lineItem.getItem().getId())) {
				return lineItem;
			}
		}
		return null;
	}

	public Receipt pay(Long id, BigDecimal payment) throws InvalidOrderException {
		Order originalOrder = orderRepository.findOne(id);
		if (originalOrder == null) {
			throw new InvalidOrderException("Could not find order corresponding to order id " + id);
		}
		orderValidator.validate(originalOrder, payment);
		originalOrder.setPaid(true);
		Order updatedOrder = orderRepository.save(originalOrder);
		Receipt receipt = new Receipt();
		receipt.setPayment(payment);
		receipt.setOrder(updatedOrder);
		return receiptService.save(receipt);
	}
}
