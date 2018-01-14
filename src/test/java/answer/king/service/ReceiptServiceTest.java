package answer.king.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import answer.king.model.Receipt;
import answer.king.repo.ReceiptRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReceiptServiceTest {

	@Autowired
    private ReceiptService receiptService;
	
	@MockBean
    private ReceiptRepository receiptRepository;
	
	@Test
	public void save_receipt_returns_a_receipt() {
	
		//	given a receipt
		Receipt receipt = new Receipt();
		
		when(receiptRepository.save(any(Receipt.class))).thenReturn(receipt);	// mock repository return value 
		
		//	when save is called with that receipt
		Receipt returnedReceipt = receiptService.save(receipt);
		
		//	then a receipt is returned (though it may not necessarily be the same one)
		assertNotNull(returnedReceipt);
		assertEquals(Receipt.class, returnedReceipt.getClass());
	}
}
