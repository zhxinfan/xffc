package utils;

/**
 * Created by xinfan on 2017/8/7.
 */

public class StringUtils {
    public static String getBigDecimal(double number) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.#####");
        return df.format(number);
    }

}
