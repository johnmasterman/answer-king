package answer.king.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import answer.king.model.Item;
import answer.king.model.exception.InvalidItemException;
import answer.king.service.ItemService;
import answer.king.validator.ItemValidator;

@RestController
@RequestMapping("/item")
public class ItemController {

	private ItemValidator validator = new ItemValidator();
	
	private final Logger log = Logger.getLogger(getClass());
	
	@Autowired
	private ItemService itemService;

	@RequestMapping(method = RequestMethod.GET)
	public List<Item> getAll() {
		List<Item> items = itemService.getAll();
		log.info("returning a list of " + items.size() + " items");
		items.forEach(item -> log.info(item));
		return items;
	}

	@RequestMapping(method = RequestMethod.POST)
	public Item create(@RequestBody Item item) throws InvalidItemException {
		
		log.info("Create called with item " + item);
		validator.validate(item);
		return itemService.save(item);
	}
	
	@RequestMapping(value = "/{id}/pricechange", method = RequestMethod.PUT)
	public Item priceChange(@PathVariable("id") Long id, @RequestBody BigDecimal newPrice) throws InvalidItemException {
		return itemService.changeItemPrice(id, newPrice);
	}

	//	Ensures that a 400 bad request response is sent if an invalid item exception is thrown
	@ExceptionHandler({InvalidItemException.class})
	void handleBadRequests(HttpServletResponse response) throws IOException {
	    response.sendError(HttpStatus.BAD_REQUEST.value());
	}
}
