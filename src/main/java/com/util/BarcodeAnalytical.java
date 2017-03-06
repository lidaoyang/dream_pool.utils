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
public class BarcodeAnalytical {

    /**
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
     * 类型一：<br />
     * 
     * (01)00827002257313(17)120422(10)W2672740<br />
     * 
     * (01)10827002112053(17)121000(30)5(10)2400867<br />
     * 
     * (01)10827002112053(17)121000(30)51(10)2400867<br />
     * 
     * (01)10827002112053(17)121000(30)5(10)2400867(240)3652036<br />
     * 
     * 类型二：<br />
     * 
     * +H302572100401/
     * 
     * *+H302572100401/<br />
     * 
     * +$$1212ANTL1646/7<br />
     * 
     * +$$9000101212ANTL1646/7<br />
     * 
     * 符合规则的条码(在类型一中，辅助信息可能单独为一个条码，但是这个条码不需要验证合理性) <br />
     * barcode.startsWith("(01)") barcode.startsWith("+H302") barcode.startsWith("*+H302")<br />
     * barcode.startsWith("+$$") barcode.startsWith("*+$$") barcode.startsWith("**+$$")<br />
     * 
     *@param barcode
     *            物品条码
     * 
     *@param validateFormat
     *            验证条码格式, 是否符合规范, true-验证,false-不验证
     * 
     *@return infoMap HashMap, 可以get出cpm（10||00 = 产品码）,yxrq（17=有效日期）,scph（10=生产批号）,sl（30=包装内数量）, wzxh（240=物资型号）,scrq （11=生产日期）
     */

    public static Map<String, Object> analyticalBarcode(String barcode,
                                                        boolean validateFormat) {
        Map<String, Object> infoMap = null;
        barcode = barcode.toUpperCase();
        if (validateFormat && !barcode.startsWith("(01)") && !barcode.startsWith("+H302") && !barcode.startsWith("*+H302") && !barcode.startsWith("+$$") && !barcode.startsWith("*+$$") && !barcode.startsWith("**+$$")) {
            System.out.println("条码格式不合规则..........................");
            return infoMap;
        }

        if (barcode.startsWith("(01)")) {
            infoMap = new HashMap<String, Object>();

            // 产品码
            if (barcode.length()>=18) {
            	String cpm = barcode.substring(4, 18);
                infoMap.put("cpm", cpm.trim());

                infoMap = barcodeType1(barcode, infoMap);

                return infoMap;
			}
        } else if (barcode.startsWith("+")) {
        	if (barcode.startsWith("+H") || barcode.startsWith("+M")) {
            // 第二种条码
            infoMap = new HashMap<String, Object>();
            String cpm = barcode; // 产品码
            infoMap.put("cpm", cpm.trim());

            return infoMap;
            }else if (barcode.startsWith("+$$")) {
                infoMap = new HashMap<String, Object>();
                if (barcode.startsWith("+$$900010")) {
                    String yxrqString = barcode.substring(9, 13).trim(); // 有效日期
                    infoMap.put("yxrq", stringToDate(yxrqString));
                    if (barcode.length() > 13) {
                        String scph = barcode.substring(13, barcode.length() - 2);
                        infoMap.put("scph", scph.trim()); // 生产批号
                    }
                } else if (barcode.startsWith("+$$801")) {
                    infoMap.put("scph", barcode.substring(10, 18).trim()); // 生产批号
                    String yxrqString = barcode.substring(8,10)+ barcode.substring(6,8);
                    infoMap.put("yxrq", stringToDate(yxrqString));
                } else {
                    if (barcode.length() >= 7) {
                    	String yxrqString = barcode.substring(3, 7).trim();
                        infoMap.put("yxrq", stringToDate(yxrqString));
                        String scph = barcode.substring(7, barcode.length() - 2);
                        infoMap.put("scph", scph.trim());
                    }
                }
                return infoMap;
            }else {
            	infoMap = new HashMap<String, Object>();
                String scph = barcode.substring(6, 14); //  生产批号
                infoMap.put("scph", scph.trim());

                return infoMap;
			}
        } 
        if (barcode.contains("(")) {
            // 主要是解析条码类型一中的副条码, (17)121020(30)50(10)240086711(240)365203611
            infoMap = new HashMap<String, Object>();
            infoMap = barcodeType1(barcode, infoMap);
            return infoMap;
        }

        infoMap = new HashMap<String, Object>();
        if (barcode.startsWith("01")) {
            // 解析条码中没有圆括号的情况。这种情况只会在条码一种存在，01108270021120531712100030510240086711

            // 产品码
        	if (barcode.length()>=16) {
        		String cpm = barcode.substring(0, 16);
                infoMap.put("cpm", cpm.trim());
			}
            if (barcode.length() == 16)
                return infoMap;
            if (barcode.length() < 16){
            	infoMap.put("cpm", barcode);
	            return infoMap;
	            }
            // 有效日期
            if (barcode.length()>=24) {
            	String yxrq = "";
                String yxrqString = barcode.substring(18, 24).trim();
                if ("00".equals(yxrqString.substring(4, 6))) {
                    yxrq = yxrqString.substring(0, 4);
                } else {
                    yxrq = yxrqString;
                }
                infoMap.put("yxrq", stringToDate(yxrq));
                
                // 数量和生产批号 : 30510240086711 或者 1113083110240086711 生成日期和生产批号 11=生成日期开始标志
                String barcode_sub1 = barcode.substring(24).trim();

                String sl = "";
                String scph = "";
                String scrq = ""; // 生产日期
                if (barcode_sub1.startsWith("10")) {
                    scph = barcode_sub1.substring(2);
                    infoMap.put("scph", scph);
                } else if (barcode_sub1.startsWith("30")) {
                    if (barcode_sub1.substring(3, 5).equals("10")) { // 30510240086711
                        sl = barcode_sub1.substring(2, 3);
                        scph = barcode_sub1.substring(5);
                    } else if (barcode_sub1.substring(4, 6).equals("10")) { // 305010240086711
                        sl = barcode_sub1.substring(2, 4);
                        scph = barcode_sub1.substring(6);
                    } else if (barcode_sub1.substring(5, 7).equals("10")) {
                        sl = barcode_sub1.substring(2, 5);
                        scph = barcode_sub1.substring(7);
                    }
                    infoMap.put("sl", sl);
                    infoMap.put("scph", scph);
                } else if (barcode_sub1.startsWith("11")) {
                    String scrqString = barcode_sub1.substring(2, 8).trim();
                    scph = barcode_sub1.substring(8);
                    if ("00".equals(scrqString.substring(3, 5))) {
                        scrq = scrqString.substring(0, 4);
                    } else {
                        scrq = scrqString;
                    }
                    infoMap.put("scrq", stringToDate(scrq));
                    infoMap.put("scph", scph);
                }
            }
            
        } else if (barcode.startsWith("17")) {
            infoMap = barcodeType2(barcode, infoMap);
        }else if (barcode.startsWith("21")) {
            infoMap = barcodeType3(barcode, infoMap);
        }else if (barcode.startsWith("10")) {
            infoMap.put("scph", barcode.substring(2));
        } else {
            infoMap.put("cpm", barcode);
        }
        return infoMap;
    }

    private static Map<String, Object> barcodeType1(String barcode,
                                                    Map<String, Object> infoMap) {

        // 有效日期
        int index_17 = barcode.indexOf("(17)");
        if (index_17 != -1) {
            String yxrq = "";
            String yxrqString = barcode.substring(index_17 + 4, index_17 + 4 + 6).trim();
            if ("00".equals(yxrqString.substring(4, 6))) {
                yxrq = yxrqString.substring(0, 4);
            } else {
                yxrq = yxrqString;
            }
            infoMap.put("yxrq", stringToDate(yxrq));
        }

        // 包装内数量
        int index_30 = barcode.indexOf("(30)");
        if (index_30 != -1) {
            String sl = "";
            String barcodeString1 = barcode.substring(index_30 + 4);
            int index_30_ = barcodeString1.indexOf("(");
            if (index_30_ != -1) {
                sl = barcodeString1.substring(0, index_30_);
            } else {
                sl = barcodeString1;
            }
            infoMap.put("sl", sl.trim());
        }

        // 生产批号
        int index_10 = barcode.indexOf("(10)");
        if (index_10 != -1) {
            String scph = "";
            String barcodeString1 = barcode.substring(index_10 + 4);
            int index_10_ = barcodeString1.indexOf("(");
            if (index_10_ != -1) {
                scph = barcodeString1.substring(0, index_10_);
            } else {
                scph = barcodeString1;
            }
            infoMap.put("scph", scph.trim());
        }

        // 物资型号
        int index_240 = barcode.indexOf("(240)");
        if (index_240 != -1) {
            String wzxh = "";
            String barcodeString1 = barcode.substring(index_240 + 5);
            int index_240_ = barcodeString1.indexOf("(");
            if (index_240_ != -1) {
                wzxh = barcodeString1.substring(0, index_240_);
            } else {
                wzxh = barcodeString1;
            }
            infoMap.put("wzxh", wzxh.trim());
        }
        return infoMap;
    }

    private static Map<String, Object> barcodeType2(String barcode,
                                                    Map<String, Object> infoMap) {

        // 有效日期
        String yxrq = "";
        String yxrqString = barcode.substring(2, 8).trim();
        if ("00".equals(yxrqString.substring(4, 6))) {
            yxrq = yxrqString.substring(0, 4);
        } else {
            yxrq = yxrqString;
        }
        infoMap.put("yxrq", stringToDate(yxrq));

        // 数量和生产批号 : 30510240086711 或者 1113083110240086711 生成日期和生产批号 11=生成日期开始标志
        String barcode_sub1 = barcode.substring(8).trim();

        String sl = "";
        String scph = "";
        String scrq = ""; // 生产日期
        if (barcode_sub1.startsWith("10")) {
            scph = barcode_sub1.substring(2);
            infoMap.put("scph", scph);
        } else if (barcode_sub1.startsWith("21")) {
        	scph = barcode_sub1.substring(2);
        	infoMap.put("scph", scph);
        }else if (barcode_sub1.startsWith("30")) {
            if (barcode_sub1.substring(3, 5).equals("10")) { // 30510240086711
                sl = barcode_sub1.substring(2, 3);
                scph = barcode_sub1.substring(5);
            } else if (barcode_sub1.substring(4, 6).equals("10")) { // 305010240086711
                sl = barcode_sub1.substring(2, 4);
                scph = barcode_sub1.substring(6);
            } else if (barcode_sub1.substring(5, 7).equals("10")) {
                sl = barcode_sub1.substring(2, 5);
                scph = barcode_sub1.substring(7);
            }
            infoMap.put("sl", sl.replaceAll("^(0+)", "")); // replaceAll("^(0+)", "") 去除数量前面的无效的0，如005
            infoMap.put("scph", scph);
        } else if (barcode_sub1.startsWith("11")) {
            String scrqString = barcode_sub1.substring(2, 8).trim();
            scph = barcode_sub1.substring(8);
            if ("00".equals(scrqString.substring(3, 5))) {
                scrq = scrqString.substring(0, 4);
            } else {
                scrq = scrqString;
            }
            infoMap.put("scrq", stringToDate(scrq));
            infoMap.put("scph", scph.substring(2));
        }

        return infoMap;
    }
    
    private static Map<String, Object> barcodeType3(String barcode,
            Map<String, Object> infoMap) {
		
		// 有效日期
		String yxrq = "";
		String yxrqString = barcode.substring(10).trim();
		if (yxrqString.startsWith("17")) {
		yxrq = yxrqString.substring(2);
		}
		infoMap.put("yxrq", stringToDate(yxrq));
		
		// 数量和生产批号 : 30510240086711 或者 1113083110240086711 生成日期和生产批号 11=生成日期开始标志
		String scph = "";
		if (barcode.startsWith("21")) {
		scph = barcode.substring(2,10);
		infoMap.put("scph", scph);
		}
		return infoMap;
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

    @SuppressWarnings("unused")
    public static void main(String[] args) {
        String barcode1 = "(01)10827002112053(17)121000(30)50(10)240086711(240)365203611";
        String barcode2 = "*+H302572100401/";
        String barcode3 = "+$$9000101212ANTL1646/7";
        String barcode4 = "(17)121000(30)50(10)240086711(240)365203611";

        String barcode5 = "01108270021120531712100030510240086711";
        String barcode6 = "0010827002112053171210001113083110240086711";
        String barcode7 = "171210001113083110240086711";
        String barcode8 = "10240086711";
        String barcode9 = "+$$80109161638772039";
        String barcode10 = "+$$801061517027591II";
        String barcode11 = "6936733720172";
        String barcode12 = "+1505915581583OZ";
        String barcode13 = "+H739670056009O";
        String barcode14 = "1715052021NEFH81BA";
        String barcode15 = "17160630104072171914935";
        String barcode16 = "1716051910208359769";
        String barcode17 = "01006139944974821714101810206261313";
        Map<String, Object> infoMap = BarcodeAnalytical.analyticalBarcode(barcode17, false);
        System.out.println("infoMap=" + infoMap);
    }
}
