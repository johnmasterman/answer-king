package answer.king.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import answer.king.model.Item;
import answer.king.model.Order;
import answer.king.model.Receipt;
import answer.king.model.exception.InvalidOrderException;
import answer.king.repo.ItemRepository;
import answer.king.repo.OrderRepository;

@Service
@Transactional
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ItemRepository itemRepository;

	public List<Order> getAll() {
		return orderRepository.findAll();
	}

	public Order save(Order order) {
		return orderRepository.save(order);
	}

	public void addItem(Long id, Long itemId) {
		Order order = orderRepository.findOne(id);
		Item item = itemRepository.findOne(itemId);

		item.setOrder(order);
		order.getItems().add(item);

		orderRepository.save(order);
	}

	public Receipt pay(Long id, BigDecimal payment) throws InvalidOrderException {
		Order originalOrder = orderRepository.findOne(id);
		if (originalOrder == null) {
			throw new InvalidOrderException("Could not find order corresponding to order id " + id);
		}
		originalOrder.setPaid(true);
		Order updatedOrder = orderRepository.save(originalOrder);
		Receipt receipt = new Receipt();
		receipt.setPayment(payment);
		receipt.setOrder(updatedOrder);
		return receipt;
	}
}
