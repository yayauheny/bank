package by.yayauheny.exception;

public class IncorrectTransactionTypeException extends IllegalArgumentException {
    public IncorrectTransactionTypeException() {
        super("Incorrect transaction type, try again");
    }

    public IncorrectTransactionTypeException(String message) {
        super(message);
    }
}
