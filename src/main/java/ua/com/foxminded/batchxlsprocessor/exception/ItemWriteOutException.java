package ua.com.foxminded.batchxlsprocessor.exception;

public class ItemWriteOutException extends RuntimeException {

	private static final long serialVersionUID = -92524771609044367L;

	public ItemWriteOutException(String message, Throwable cause) {
		super(message, cause);
	}

	public ItemWriteOutException(String message) {
		super(message);
	}

	public ItemWriteOutException(Throwable cause) {
		super(cause);
	}
	
	 

}
