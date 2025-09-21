package top.nguyennd.restsqlbackend.abstraction.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Validator {

    public static void checkCondition(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }
}
