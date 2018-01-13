package answer.king.model.exception;

/**
 * Indicates that an item is invalid.
 * 
 * @author John
 *
 */
public class InvalidItemException extends Exception {

	public InvalidItemException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidItemException(String message) {
		super(message);
	}
	
	public InvalidItemException(Throwable cause) {
		super(cause);
	}
	
	public InvalidItemException() {
		super();
	}
}
