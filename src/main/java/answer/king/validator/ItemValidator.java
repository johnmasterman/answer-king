package answer.king.validator;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;
import answer.king.model.Item;
import answer.king.model.exception.InvalidItemException;

/**
 * Item is invalid when 
 * 	name is invalid because it is null or empty
 *  and/or price is invalid because it is null or negative 
 * 
 * @author John
 *
 */
@Component
public class ItemValidator {

	public void validate(Item item) throws InvalidItemException {
		
		if (item == null) {
			throw new InvalidItemException("Item cannot be null");
		}
		String name = item.getName();
		if (name == null || name.equals("")) {
			throw new InvalidItemException("Item name invalid, name = '" + name + "'");
		}
		BigDecimal price = item.getPrice();
		if (price == null  || price.signum() == -1) {
			throw new InvalidItemException("Item price invalid, price = '" + price + "'");
		}
	}
}
