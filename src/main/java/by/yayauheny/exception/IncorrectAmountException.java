package by.yayauheny.exception;

public class IncorrectAmountException extends IllegalArgumentException {
    public IncorrectAmountException() {
        super("Incorrect amount has been passed, try again");
    }

    public IncorrectAmountException(String message) {
        super(message);
    }
}
