package org.udoo.appinventor.iot.udooiotrestjava.util;

public class Util {
    public static String[] StringToValueArr(String value) {
        if (value != null && value.startsWith("[") && value.endsWith("]")) {
            return value.replaceAll("\\[", "").replaceAll("\"", "").replaceAll("]", "").replaceAll("\\s", "").split(",");
        }
        return new String[0];
    }
}
