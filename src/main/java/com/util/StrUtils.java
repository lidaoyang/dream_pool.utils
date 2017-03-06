package com.util;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:字符工具类
 * @itemName:swell
 */
public class StrUtils extends org.apache.commons.lang.StringUtils {

    public static String leftPad(int value,
                                 int size,
                                 char padChar) {
        String str = String.valueOf(value);
        str = leftPad(str, size, padChar);
        return str;
    }

    public static String rightPad(int value,
                                  int size,
                                  char padChar) {
        String str = String.valueOf(value);
        str = rightPad(str, size, padChar);
        return str;
    }

    public static String GetString(Object as_str) {
        if (as_str == null) {
            return "";
        }
        else if ("null".equals(as_str)) {
        	return "";
		}
        return "undefined".equals(as_str.toString().toLowerCase())? "" : as_str.toString();
    }

    public static String GetNum(Object as_str) {
        String val = GetString(as_str);
        if (val.equals("")) {
            return "0";
        }
        return val;
    }

    public static boolean isNullOrEmpty(String input) {
        return input == null || input.length() == 0;
    }

    public static String addQuote(String str) {
        if (!"NULL".equalsIgnoreCase(str)) {
            str = "'" + encodeSingleQuotedString(str) + "'";
        }
        return str;
    }

    public static String leftQuote(String str) {
        str = "'" + encodeSingleQuotedString(str);

        return str;
    }

    public static String rightQuote(String str) {
        str = encodeSingleQuotedString(str) + "'";

        return str;
    }

    public static String encodeSingleQuotedString(String str) {
        if (isNotEmpty(str)) {
            StringBuffer sb = new StringBuffer(64);
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                if (c == '\'')
                    sb.append("''");
                else
                    sb.append(c);
            }
            return sb.toString();
        }

        return str;
    }

    public static String formatDouble(double value) {
        String str = "0.0";

        if (value != 0.0D) {
            DecimalFormat nf = new DecimalFormat("#.##");

            nf.setParseIntegerOnly(false);
            nf.setDecimalSeparatorAlwaysShown(false);

            str = nf.format(value);
        }

        return str;
    }

    public static String formatDouble(double value,
                                      int digitNum) {
        String str = "0.0";

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digitNum; i++) {
            sb.append("0");
        }
        DecimalFormat nf = new DecimalFormat("#." + sb.toString());
        nf.setParseIntegerOnly(false);
        nf.setDecimalSeparatorAlwaysShown(false);
        str = nf.format(value);
        if (isEmpty(str.split("\\.")[0]))
            str = "0" + str;
        return str;
    }

    public static String[] tokenize(String str,
                                    String delimiter) {
        ArrayList<String> v = new ArrayList<String>();

        StringTokenizer t = new StringTokenizer(str, delimiter);

        while (t.hasMoreTokens()) {
            String s = t.nextToken();
            if (isNotEmpty(s)) {
                v.add(s.trim());
            }
        }

        String[] pro = new String[v.size()];
        for (int i = 0; i < pro.length; i++) {
            pro[i] = v.get(i);
        }
        return pro;
    }

    public static void byte2hex(byte b,
                                StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

        int high = (b & 0xF0) >> 4;
        int low = b & 0xF;

        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    public static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer(64);

        int len = block.length;

        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
        }

        return buf.toString();
    }

    public static String addFlagQuote(String str) {
        StringBuffer sbFlag = new StringBuffer(16);

        String[] flags = tokenize(str, ",");

        int count = flags.length;
        for (int i = 0; i < count; i++) {
            if (i != 0) {
                sbFlag.append(",");
            }
            sbFlag.append("'" + flags[i] + "'");
        }

        return sbFlag.toString();
    }

    public static String getData(String resouce,
                                 String label) {
        String result = "";
        String labelB = "";
        String labelE = "";

        int site1 = 0;
        int site2 = 0;

        if ((resouce == null) || (label == null)) {
            return result;
        }

        resouce = resouce.trim();
        labelB = "<" + label + ">";
        labelE = "</" + label + ">";

        site1 = resouce.indexOf(labelB) + labelB.length();
        site2 = resouce.indexOf(labelE);

        if ((site1 < 0) || (site2 < 0)) {
            return "";
        }

        result = resouce.substring(site1, site2);

        return result;
    }

    public static String convertToGB(String str) {
        String result = str;
        String charCodeOld = "UTF-8";
        String charCodeNew = "GBK";

        if (isNotEmpty(result)) {
            try {
                byte[] bytes = result.getBytes(charCodeOld);
                result = new String(bytes, charCodeNew);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return result;
    }

    public static String convertToISO(String str) {
        String result = str;
        String charCodeOld = "GBK";
        String charCodeNew = "8859_1";

        if (isNotEmpty(result)) {
            try {
                byte[] bytes = result.getBytes(charCodeOld);
                result = new String(bytes, charCodeNew);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    public static String fomatHtmlText(String reCeiv) {
        String cMesg = "";
        String sMesg = "";

        for (int ii = 0; ii < reCeiv.length(); ii++) {
            cMesg = reCeiv.substring(ii, ii + 1);
            if ("\n".compareTo(cMesg) == 0) {
                sMesg = sMesg + "<br>";
            } else if (" ".compareTo(cMesg) == 0) {
                sMesg = sMesg + "&nbsp";
            } else {
                sMesg = sMesg + cMesg;
            }
        }
        return sMesg;
    }

    public static String toUtf8String(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if ((c >= 0) && (c <= 'ÿ')) {
                sb.append(c);
            } else {
                byte[] b;
                try {
                    b = Character.toString(c).getBytes("utf-8");
                } catch (Exception ex) {
                    b = new byte[0];
                }
                for (int j = 0; j < b.length; j++) {
                    int k = b[j];
                    if (k < 0)
                        k += 256;
                    sb.append("%" + Integer.toHexString(k).toUpperCase());
                }
            }
        }

        return sb.toString();
    }

    public static String getFormatDate(String s) {
        String s_date = "";
        if (s.trim().length() == 14)
            s_date = s.substring(0, 4) + "-" + s.substring(4, 6) + "-" + s.substring(6, 8);
        else
            s_date = s;
        return s_date;
    }

    public static String getPriorDay(int offset) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

        Calendar theday = Calendar.getInstance();
        theday.add(5, offset);

        df.applyPattern("yyyyMMdd");
        return df.format(theday.getTime());
    }

    public static String trim(String s) {
        if ((s == null) || ("".equalsIgnoreCase(s)) || ("null".equalsIgnoreCase(s))) {
            return "";
        }
        return s.trim();
    }

    public static String getStrSysYear() {
        Calendar getTime = Calendar.getInstance(Locale.CHINA);
        return Integer.toString(getTime.get(1));
    }

    public static int getIntSysYear() {
        Calendar getTime = Calendar.getInstance(Locale.CHINA);
        return getTime.get(1);
    }

    public static int getIntSysMonth() {
        Calendar getTime = Calendar.getInstance(Locale.CHINA);
        return getTime.get(2) + 1;
    }

    public static String getStrSysDay() {
        Calendar getTime = Calendar.getInstance(Locale.CHINA);
        return Integer.toString(getTime.get(5));
    }

    public static int getIntSysDay() {
        Calendar getTime = Calendar.getInstance(Locale.CHINA);
        return getTime.get(5);
    }

    public static String getFileModifyTime(String fileName) {
        File fn = new File(fileName);

        Long.toString(fn.lastModified());
        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar theday = Calendar.getInstance();
        theday.setTimeInMillis(fn.lastModified());
        df.applyPattern("yyyy/MM/dd HH:mm:ss");
        return df.format(theday.getTime());
    }

    public static String getLikeString(String colName,
                                       String colValue) {
        return colName + " Like '%" + colValue + "%'";
    }

    public static String getUpperInteger(int in) {
        String out = "";
        int tmp = in;
        while (true) {
            int tmp1 = tmp % 10;
            switch (tmp1) {
            case 1:
                out = "一" + out;
                break;
            case 2:
                out = "二" + out;
                break;
            case 3:
                out = "三" + out;
                break;
            case 4:
                out = "四" + out;
                break;
            case 5:
                out = "五" + out;
                break;
            case 6:
                out = "六" + out;
                break;
            case 7:
                out = "七" + out;
                break;
            case 8:
                out = "八" + out;
                break;
            case 9:
                out = "九" + out;
            }
            tmp /= 10;
            if (tmp == 0)
                break;
        }
        return out;
    }

    public static String changeNullToEmpty(String str) {
        if (isEmpty(str)) {
            return "";
        }

        return str;
    }

    public static String convertURL(String URL) {
        if (isEmpty(URL)) {
            return "";
        }

        return URL.replace('\\', '/');
    }

    public static String byteToString(byte b) {
        byte maskHigh = -16;
        byte maskLow = 15;

        byte high = (byte) ((b & maskHigh) >> 4);
        byte low = (byte) (b & maskLow);

        StringBuffer buf = new StringBuffer();
        buf.append(findHex(high));
        buf.append(findHex(low));

        return buf.toString();
    }

    private static char findHex(byte b) {
        int t = new Byte(b).intValue();
        t = t < 0 ? t + 16 : t;

        if ((t >= 0) && (t <= 9)) {
            return (char) (t + 48);
        }

        return (char) (t - 10 + 65);
    }

    public static int stringToByte(String in,
                                   byte[] b) {
        if (b.length < in.length() / 2) {
            return 0;
        }

        int j = 0;
        StringBuffer buf = new StringBuffer(2);
        for (int i = 0; i < in.length(); j++) {
            buf.insert(0, in.charAt(i));
            buf.insert(1, in.charAt(i + 1));
            int t = Integer.parseInt(buf.toString(), 16);

            b[j] = ((byte) t);
            i++;
            buf.delete(0, 2);

            i++;
        }

        return j;
    }

    public static byte[] hex2Bytes(String hexString) {
        if (hexString == null) {
            return null;
        }

        if (hexString.length() % 2 != 0) {
            hexString = '0' + hexString;
        }

        byte[] result = new byte[hexString.length() / 2];

        for (int i = 0; i < result.length; i++) {
            result[i] = ((byte) Integer.parseInt(hexString.substring(i * 2, (i + 1) * 2), 16));
        }

        return result;
    }

    public static String bytes2Hex(byte[] bytes) {
        StringBuffer result = new StringBuffer("");
        if (bytes == null) {
            return "";
        }

        for (int i = 0; i < bytes.length; i++) {
            result.append(padding2Head(Integer.toHexString(bytes[i] & 0xFF), '0', 2));
        }
        return result.toString();
    }

    public static String padding2Head(String s,
                                      char ch,
                                      int destLength) {
        StringBuffer str = null;
        if (destLength < 0) {
            return "";
        }
        if (s == null) {
            str = new StringBuffer();
            for (int i = 0; i < destLength; i++) {
                str.append(ch);
            }
        } else {
            if (s.length() > destLength) {
                return "";
            }

            str = new StringBuffer();
            for (int i = 0; i < destLength - s.length(); i++) {
                str.append(ch);
            }
            str.append(s);
        }

        return str.toString();
    }

    public static String subStringCN(final String str,
                                     final int maxLength) {
        if (str == null) {
            return str;
        }
        String suffix = "**";
        int suffixLen = suffix.length();

        final StringBuffer sbuffer = new StringBuffer();
        final char[] chr = str.trim().toCharArray();
        int len = 0;
        for (int i = 0; i < chr.length; i++) {

            if (chr[i] >= 0xa1) {
                len += 2;
            } else {
                len++;
            }
        }

        len = 0;
        for (int i = 0; i < chr.length; i++) {

            if (chr[i] >= 0xa1) {
                len += 2;
                if (len + suffixLen > maxLength) {
                    break;
                }
                sbuffer.append(chr[i]);
            } else {
                len++;
                if (len + suffixLen > maxLength) {
                    break;
                }
                sbuffer.append(chr[i]);
            }
        }
        sbuffer.append(suffix);
        return sbuffer.toString();
    }

    /**
     * 字符串编码转换的实现方法
     * 
     * @param str
     *            待转换编码的字符串
     * @param oldCharset
     *            原编码
     * @param newCharset
     *            目标编码
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String changeCharset(String str,
                                       String oldCharset,
                                       String newCharset) {
        if (isNotEmpty(str)) {
            try {
                // 用旧的字符编码解码字符串。解码可能会出现异常。
                byte[] bs = str.getBytes(oldCharset);
                // 用新的字符编码生成字符串
                return new String(bs, newCharset);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 对字符串中的数字求和
     * 
     * @param text
     * @return
     */
    public static int sum(String text,
                          int len) {
        text = len > 0 ? text.substring(0, len) : text;
        int value = 0;
        Pattern p = Pattern.compile("[^0-9]");
        Matcher m = p.matcher(text);
        String result = m.replaceAll("");
        for (int i = 0; i < result.length(); i++) {
            value += NumberUtils.parseInt(result.substring(i, i + 1));
        }
        return value;
    }

    /**
     * 拼接在某属性的 set方法
     * 
     * @param fieldName
     * @return String
     */
    public static String parSetName(String fieldName) {
        if (StrUtils.isEmpty(fieldName)) {
            return null;
        }
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    /**
     * set属性的值到Bean
     * 
     * @param bean
     * @param name
     * @param value
     */
    public static void setFieldValue(Object bean,
                                     String name,
                                     Object value) {
        try {
            Field field = bean.getClass().getDeclaredField(name);
            String fieldSetName = parSetName(field.getName());
            Method fieldSetMet = bean.getClass().getMethod(fieldSetName, field.getType());
            String str = StrUtils.GetString(value);
            if (null != value && !"".equals(value)) {
                String fieldType = field.getType().getSimpleName();
                if ("String".equals(fieldType)) {
                    fieldSetMet.invoke(bean, value);
                } else if ("Date".equals(fieldType)) {
                    Date temp = DateUtils.StrToDate(str, "");
                    fieldSetMet.invoke(bean, temp);
                } else if ("Integer".equals(fieldType) || "int".equals(fieldType)) {
                    Integer intval = NumberUtils.parseInt(str);
                    fieldSetMet.invoke(bean, intval);
                } else if ("Long".equalsIgnoreCase(fieldType)) {
                    Long temp = NumberUtils.parseLong(str);
                    fieldSetMet.invoke(bean, temp);
                } else if ("Double".equalsIgnoreCase(fieldType)) {
                    Double temp = NumberUtils.parseDouble(str);
                    fieldSetMet.invoke(bean, temp);
                } else if ("Boolean".equalsIgnoreCase(fieldType)) {
                    Boolean temp = Boolean.parseBoolean(str);
                    fieldSetMet.invoke(bean, temp);
                } else if ("BigDecimal".equalsIgnoreCase(fieldType)) {
                    BigDecimal temp = BigDecimal.valueOf(NumberUtils.parseDouble(str));
                    fieldSetMet.invoke(bean, temp);
                } else {
                }
            }
        } catch (Exception e) {
        }
    }
    /**
     * 判断字符是否是中文
     *
     * @param c 字符
     * @return 是否是中文
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否是乱码
     *
     * @param strName 字符串
     * @return 是否是乱码
     */
    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("[s*|t*|r*|n*]");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("[p{P}]", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = ch.length;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 替换四个字节的字符为空
     *
     * @param strName 字符串
     * @return 内容
     * @throws UnsupportedEncodingException 
     */
  	public static String removeFourChar(String content,String charsetName) throws UnsupportedEncodingException {
          byte[] conbyte = content.getBytes(charsetName);
          for (int i = 0; i < conbyte.length; i++) {
              if ((conbyte[i] & 0xF8) == 0xF0) {
                  for (int j = 0; j < 4; j++) {                          
                      conbyte[i+j]=0x30;                     
                  }  
                  i += 3;
              }
          }
          content = new String(conbyte,charsetName);
          return content.replaceAll("0000", "");
      }
    public static void main(String[] args) {
    	System.out.print(GetString("undefined"));
    }
}