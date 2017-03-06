package com.util;

/**
 * @Description 加密
 * @param ms
 * @throws RemoteException
 * @author bjz
 * @date Jul 23, 2013
 */

public class MXCipher extends MXCipherX {

	// 配置
	public MXCipher() throws Exception {
		String algTest = "DESede";
		String modeTest = "CBC";
		String paddingTest = "PKCS5Padding";
		String keyTest = "Sa#qk5usfmMI-@2dbZP9`jL3";
		String specTest = "beLd7$lB";
		String modTest = "";
		String providerTest = "";

		init(algTest, modeTest, paddingTest, keyTest, specTest, modTest,
				providerTest);
	}

	public static void main(String[] argv) {

		String value = "maxiadmin";
		String value1 = "111111";
		System.out.println(getCipher(value));
		System.out.println(getCipher(value1));

	}

	// 加密
	public static String getCipher(String mcipher) {
		String acipher = "";
		MXCipher MXCipher;
		try {
			MXCipher = new MXCipher();
			byte[] temp = MXCipher.encData(mcipher);
			acipher = byte2hex(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return acipher;
	}

	// 二进制转字符串
	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			stmp = stmp.toUpperCase();
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs;
	}
}
