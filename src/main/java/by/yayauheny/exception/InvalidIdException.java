package by.yayauheny.exception;

public class InvalidIdException extends IllegalArgumentException {
    public InvalidIdException() {
        super("Invalid argument have been passed as id parameter");
    }

    public InvalidIdException(String s) {
        super(s);
    }
}
