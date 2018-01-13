package answer.king.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import answer.king.service.ItemService;

@RunWith(SpringRunner.class)
@WebMvcTest(ItemController.class)
public class ItemControllerTest {

	@Autowired
    private MockMvc mvc;
 
    @MockBean
    private ItemService itemService;
    
    @Test
    public void item_controller_is_not_accessible_on_incorrect_path() throws Throwable {

    	when(itemService.getAll()).thenReturn(Collections.emptyList());
    	
    	mvc.perform(get("/not_path_to_item")).andExpect(status().isNotFound());
    }
    
    @Test
    public void item_controller_is_accessible_via_get_request_on_path_item() throws Throwable {
    	
    	when(itemService.getAll()).thenReturn(Collections.emptyList());
    	
    	mvc.perform(get("/item")).andExpect(status().isOk());
    }
    
    @Test
    public void posting_no_item_data_returns_bad_request() throws Throwable {
    
    	mvc.perform(post("/item")).andExpect(status().isBadRequest());
    }
}
