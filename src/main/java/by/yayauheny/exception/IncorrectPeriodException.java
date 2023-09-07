package by.yayauheny.exception;

public class IncorrectPeriodException extends IllegalArgumentException {

    public IncorrectPeriodException() {
        super("Incorrect period of transactions has been passed, try again");
    }

    public IncorrectPeriodException(String message) {
        super(message);
    }
}
