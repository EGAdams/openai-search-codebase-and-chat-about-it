package com.nac.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by andreikaralkou on 3/3/14.
 */
public class MathUtils {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
