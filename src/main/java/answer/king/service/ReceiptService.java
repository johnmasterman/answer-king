package answer.king.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import answer.king.model.Receipt;
import answer.king.repo.ReceiptRepository;

@Service
@Transactional
public class ReceiptService {

	@Autowired
	private ReceiptRepository receiptRepository;
	
	public Receipt save(Receipt receipt) {
		Receipt retVal = receiptRepository.save(receipt);
		return retVal;
	}
	
	public Receipt getById(Long receiptId) {
		return receiptRepository.findOne(receiptId);
	}
}
