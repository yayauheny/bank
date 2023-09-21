package by.yayauheny.exception;

import com.sun.jdi.InternalException;

public class ReceiptBuildingException extends InternalException {
    public ReceiptBuildingException() {
        super("Exception while printing receipt. Something gone wrong, try again");
    }

    public ReceiptBuildingException(String message) {
        super(message);
    }
}
