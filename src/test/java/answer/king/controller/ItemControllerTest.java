package answer.king.controller;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import answer.king.model.Item;
import answer.king.model.exception.InvalidItemException;
import answer.king.service.ItemService;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTest {

	@Autowired
    private MockMvc mvc;
 
    @MockBean
    private ItemService itemService;
    
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Test
    public void item_controller_is_not_accessible_on_incorrect_path() throws Throwable {

    	// when a get request is made to an invalid endpoint 
    	mvc.perform(get("/not_path_to_any_endpoint"))
    	
    	//	then a not found status is returned
    		.andExpect(status().isNotFound());
    }
    
    @Test
    public void item_controller_is_accessible_via_get_request_on_path_item() throws Throwable {
    	
    	when(itemService.getAll()).thenReturn(Collections.emptyList());	// mock service return value
    	
    	//	when a get request is made to the getAll endpoint 
    	mvc.perform(get("/item"))
    	
    	//	then an OK status code is returned
    		.andExpect(status().isOk());
    }
    
    @Test
    public void posting_no_item_data_returns_bad_request() throws Throwable {
    
    	//	when no data is posted to the create item endpoint
    	mvc.perform(post("/item"))
    	
    	//	then a bad request status code is returned
    		.andExpect(status().isBadRequest());
    }
    
    @Test 
    public void posting_item_returns_item() throws Throwable {
    	
    	// given an item
    	Item item = new Item();
    	item.setId(1L);
    	item.setName("itemName");
    	item.setPrice(new BigDecimal(45));
    	
    	when(itemService.save(any(Item.class))).thenReturn(item);	// mock service return value
    	
    	//	when the item is posted, as JSON, to the create item endpoint
    	mvc.perform(post("/item").content(mapper.writeValueAsString(item))
    		.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    	
    	//	then the item is returned in the response
    		.andExpect(status().isOk())
    		.andExpect(jsonPath("$.id", is(1)))
    		.andExpect(jsonPath("$.name", is(item.getName())))
    		.andExpect(jsonPath("$.price", is(45)));
    }
    
    @Test
    public void posting_item_causes_invocation_of_service_save_method_with_item() throws Throwable {

    	ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
    	
    	//	given an item
    	Item item = new Item();
    	item.setName("itemName");
    	item.setPrice(new BigDecimal(45));
    	
    	when(itemService.save(itemCaptor.capture())).thenReturn(item);
    	
    	//	when the item is posted, as JSON, to the create item endpoint
    	mvc.perform(post("/item").content(mapper.writeValueAsString(item))
        	.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        	.andExpect(status().isOk());
    	
    	//	then the itemService save method is called with the item values
    	assertEquals(item.getId(), itemCaptor.getValue().getId());
    	assertEquals(item.getName(), itemCaptor.getValue().getName());
    	assertEquals(item.getPrice(), itemCaptor.getValue().getPrice());
    }
    
    @Test
    public void posting_invalid_item_returns_bad_request() throws Throwable {

    	//	given an item that is invalid due to having no name
    	Item item = new Item();
    	item.setPrice(new BigDecimal(45));   	
    	
    	//	when the item is posted to the create item endpoint
    	mvc.perform(post("/item").content(mapper.writeValueAsString(item))
		.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    	
    	//	then a bad request status code is returned
    		.andExpect(status().isBadRequest());
    }
    
    @Test
    public void item_price_change_for_invalid_item_returns_bad_request() throws Throwable{
    	
    	//	given an item id that does not exist
    	Long itemId = new Long(7);
    	BigDecimal newPrice = new BigDecimal(45.23);
    	
    	//	set mocks
    	when(itemService.changeItemPrice(itemId, newPrice)).thenThrow(new InvalidItemException("Item does not exist"));
    	
    	//	when a put request is made to change the price
    	mvc.perform(put("/item/" + itemId + "/pricechange").content(mapper.writeValueAsString(newPrice))
		.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    	
    	//	then a bad request status code is returned
			.andExpect(status().isBadRequest());
    }
    
    @Test
    public void item_with_new_price_returned_when_price_changed() throws Throwable{
 
    	//	given an item id that exists and a new price
    	Long itemId = new Long(7);
    	BigDecimal newPrice = new BigDecimal(45);
    	
    	//	set mocks
    	Item item = new Item();
    	item.setId(itemId);
    	item.setName("itemName");
    	item.setPrice(newPrice);
    	when(itemService.changeItemPrice(itemId, newPrice)).thenReturn(item);
    	
    	//	when a put request is made to change the price
    	mvc.perform(put("/item/" + itemId + "/pricechange").content(mapper.writeValueAsString(newPrice))
		.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
    	
    	//	then the item is returned with the new price
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.price", is(45)));
    } 
}
