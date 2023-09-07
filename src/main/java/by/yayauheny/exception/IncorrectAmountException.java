package by.yayauheny.exception;

public class IncorrectAmountException extends Exception {
    public IncorrectAmountException() {
        super("Cannot perform transaction, incorrect amount");
    }

    public IncorrectAmountException(String message) {
        super(message);
    }
}
