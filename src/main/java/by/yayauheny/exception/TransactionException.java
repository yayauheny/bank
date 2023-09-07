package by.yayauheny.exception;

import com.sun.jdi.InternalException;

public class TransactionException extends InternalException {
    public TransactionException(Throwable cause) {
        super("Exception while processing transaction. Try again");
    }

    public TransactionException(String message) {
        super(message);
    }
}
