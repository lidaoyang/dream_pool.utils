package com.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.client.ClientProtocolException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class PDFReader {
	public static String getTextFromPDF(String url) 
	{
		String result = null;
		PDDocument document = null;
		InputStream is = null;
		try {
			HttpResponse<InputStream> httpis = Unirest.get(url).asBinary();
			is =httpis.getBody();
			document =PDDocument.load(is);
			if(document.isEncrypted()){
				return "isEncrypted";
			}
			PDFTextStripper stripper = new PDFTextStripper();
			stripper.setEndPage(36);
			result = stripper.getText(document);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnirestException e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
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
	public  static void main(String[] args) throws UnirestException, ClientProtocolException, IOException
	{
//		String str=PDFReader.getTextFromPDF("http://www.cninfo.com.cn/finalpage/2015-03-25/1200736869.PDF");
		String str=PDFReader.getTextFromPDF("http://www.cninfo.com.cn/finalpage/2015-10-29/1201734106.PDF");
//		String str ="评估过程中使用的基本计算公式为：  𝑃 =   𝑅𝑖  1 + 𝑟 𝑖 𝑛 𝑖=1 + 𝐴  式中：  P 股东全部权益价值 ";
		System.out.println(str);
    }  
	
}