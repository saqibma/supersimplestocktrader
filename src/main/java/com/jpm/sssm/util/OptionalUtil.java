package com.jpm.sssm.util;

import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * Created by adnan_saqib on 10/12/2016.
 */
public class OptionalUtil {

    private static final String BLANK = "";

    public static OptionalInt stringToInt(String data) {
        try {
            return OptionalInt.of(Integer.parseInt(data));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }

    public static OptionalDouble safeFixedDividened(String data) {
        if (data.equals(BLANK)) {
            return OptionalDouble.empty();
        }
        try {
            data = data.replaceAll("%", "");
            return OptionalDouble.of(Double.parseDouble(data) / 100);
        } catch (NumberFormatException e) {
            return OptionalDouble.empty();
        }
    }
}
