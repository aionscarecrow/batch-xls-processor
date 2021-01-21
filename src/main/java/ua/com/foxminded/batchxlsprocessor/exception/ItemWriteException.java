package ua.com.foxminded.batchxlsprocessor.exception;

public class ItemWriteException extends RuntimeException {

	private static final long serialVersionUID = -92524771609044367L;

	public ItemWriteException(String message, Throwable cause) {
		super(message, cause);
	}

	public ItemWriteException(String message) {
		super(message);
	}

	public ItemWriteException(Throwable cause) {
		super(cause);
	}
	
	 

}
