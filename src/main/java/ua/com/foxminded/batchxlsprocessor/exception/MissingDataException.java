package ua.com.foxminded.batchxlsprocessor.exception;

public class MissingDataException extends RuntimeException {

    private final String message;

    public MissingDataException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
