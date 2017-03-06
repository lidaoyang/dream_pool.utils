package com.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 物品条码解析
 * 
 * @author
 * 
 */
public class BarcodeAnalytical2 {

    /**
     *            厂商编码|物品码|17效期|10批号|全局流水号  <br />
     * 条码格式：PRODUCTP_ID|PRODUCE_ID|17[+效期]|10[+批号]|全局流水号
     * 
     * cpm : 产品码<br />
     * 
     * yxrq ：有效日期<br />
     * 
     * sl ：包装内数量 <br />
     * 
     * scph ：生产批号<br />
     * 
     * wzxh ：物资型号<br />
     * 
     */

    public static Map<String, Object> analyticalBarcode(String barcode) {
        if (StrUtils.isNullOrEmpty(barcode))
            return null;

        Map<String, Object> infoMap = new HashMap<String, Object>();
        barcode = barcode.toUpperCase();

        System.out.println(barcode);

        //获得第一个|的位置
        int index1 = barcode.indexOf("|");

        //根据第一个|的位置 获得第二个|的位置
        int index2 = barcode.indexOf("|", index1 + 1);

        //根据第二个|的位置 获得第三个|的位置
        int index3 = barcode.indexOf("|", index2 + 1);

        //根据第三个|的位置 获得第四个|的位置
        int index4 = barcode.indexOf("|", index3 + 1);

        infoMap.put("barcode", barcode);

        String cpm = barcode.substring(0, index2);
        infoMap.put("cpm", cpm);
        System.out.println("产品码：" + cpm);

        String yxrq = barcode.substring(index2 + 3, index3);
        infoMap.put("yxrq", stringToDate(yxrq));
        System.out.println("有效期：" + stringToDate(yxrq));

        String scph = barcode.substring(index3 + 3, index4);
        infoMap.put("scph", scph);
        System.out.println("生产批号：" + scph);

        return infoMap;
    }

    public static void main(String[] args) {
        String barcode = "11111|222222|17121000|10240086711|100001";

        Map<String, Object> infoMap = BarcodeAnalytical2.analyticalBarcode(barcode);
        System.out.println("infoMap=" + infoMap);
    }

    private static Date stringToDate(String yxrqString) {
        if (yxrqString.length() == 4) {
            yxrqString += "01";
        }
        try {
            yxrqString = "20" + yxrqString;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date yxrq = formatter.parse(yxrqString.substring(0, 4) + "-" + yxrqString.substring(4, 6) + "-" + yxrqString.substring(6, 8));
            return yxrq;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
