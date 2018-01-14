package answer.king.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import answer.king.model.Item;
import answer.king.model.exception.InvalidItemException;
import answer.king.repo.ItemRepository;

@Service
@Transactional
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

	public List<Item> getAll() {
		return itemRepository.findAll();
	}

	public Item save(Item item) {
		return itemRepository.save(item);
	}
	
	public Item changeItemPrice(Long id, BigDecimal newPrice) throws InvalidItemException {
		Item item = itemRepository.findOne(id);
		if (item == null) {
			throw new InvalidItemException("Could not find item for id " + id);
		}
		item.setPrice(newPrice);
		return itemRepository.save(item);
	}
}
