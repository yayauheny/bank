package by.yayauheny.util;

import by.yayauheny.exception.InvalidIdException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Validator {

    public <T extends Number> boolean validateId(T id) throws InvalidIdException {
        if (id == null || id.intValue() < 0) {
            throw new InvalidIdException();
        }

        return true;
    }
}
