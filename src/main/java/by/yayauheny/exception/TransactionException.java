package by.yayauheny.exception;

public class TransactionException extends RuntimeException {
    public TransactionException(Throwable cause) {
        super("Exception while processing transaction. Try again", cause);
    }

    public TransactionException(String message) {
        super(message);
    }
}
