package com.util;

import java.math.BigDecimal;

public class Ariths {

	public Ariths() {
	}
	/**
     * 提供精确加法计算的add方法
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(Double v1,Double v2){
    	BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.add(b2).doubleValue();
    }
    
    /**
     * 提供精确减法运算的sub方法
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(Double v1,Double v2){
    	BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.subtract(b2).doubleValue();
    }
    
    /**
     * 提供精确乘法运算的mul方法
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(Double v1,Double v2){
    	BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2).doubleValue();
    }
    private static final int DEF_DIV_SCALE = 10;
    /**
     * * 两个Double数相除 *
     *
     * @param v1 *
     * @param v2 *
     * @return Double
     */
    public static Double div(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP)
                .doubleValue());
    }

    /**
     * * 两个Double数相除，并保留scale位小数 *
     *
     * @param v1    *
     * @param v2    *
     * @param scale *
     * @return Double
     */
    public static Double div(Double v1, Double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return new Double(b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue());
    }
}
