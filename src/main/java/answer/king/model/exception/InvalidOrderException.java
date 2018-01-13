package answer.king.model.exception;

/**
 * Indicates an error in the order
 * 
 * @author John
 *
 */
public class InvalidOrderException extends Exception {

	public InvalidOrderException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InvalidOrderException(String message) {
		super(message);
	}
	
	public InvalidOrderException(Throwable cause) {
		super(cause);
	}
	
	public InvalidOrderException() {
		super();
	}
}
