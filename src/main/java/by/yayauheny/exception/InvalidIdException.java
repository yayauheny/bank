package by.yayauheny.exception;

public class InvalidIdException extends IllegalArgumentException {
    public InvalidIdException(Throwable cause) {
        super("Invalid argument have been passed as id", cause);
    }
}
