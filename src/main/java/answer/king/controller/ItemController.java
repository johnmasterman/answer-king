package answer.king.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import answer.king.model.Item;
import answer.king.service.ItemService;
import answer.king.validator.InvalidItemException;
import answer.king.validator.ItemValidator;

@RestController
@RequestMapping("/item")
public class ItemController {

	private ItemValidator validator = new ItemValidator();
	
	@Autowired
	private ItemService itemService;

	@RequestMapping(method = RequestMethod.GET)
	public List<Item> getAll() {
		return itemService.getAll();
	}

	@RequestMapping(method = RequestMethod.POST)
	public Item create(@RequestBody Item item) throws InvalidItemException {
		
		validator.validate(item);
		return itemService.save(item);
	}

	//	Ensures that a 400 bad request response is sent if an invalid item exception is thrown
	@ExceptionHandler({InvalidItemException.class})
	void handleBadRequests(HttpServletResponse response) throws IOException {
	    response.sendError(HttpStatus.BAD_REQUEST.value());
	}

}
