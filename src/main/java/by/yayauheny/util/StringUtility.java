package by.yayauheny.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtility {

    public String center(String text, int width) {
        if (text.length() < width) {
            int totalSpace = width - text.length();
            int leftSideSpace = totalSpace / 2;
            int rightSideSpace = totalSpace - leftSideSpace;

            return " ".repeat(leftSideSpace).concat(text).concat(" ".repeat(rightSideSpace));
        } else {
            return text;
        }
    }
}
