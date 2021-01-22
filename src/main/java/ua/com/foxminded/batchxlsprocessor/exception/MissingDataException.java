package ua.com.foxminded.batchxlsprocessor.exception;

public class MissingDataException extends RuntimeException {

	private static final long serialVersionUID = -5271632614570856375L;
	
	private final String message;

    public MissingDataException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
