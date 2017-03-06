package com.util;

import java.lang.reflect.Constructor;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.*;
import javax.crypto.spec.*;

public class MXCipherX {

	protected MXCipherX() {
		algorithm = "DESede";
		mode = "CBC";
		padding = "PKCS5Padding";
		key = "Sa#qk5usfmMI-@2dbZP9`jL3";
		spec = "beLd7$lB";
		modulus = "";
		cipherEncrypt = null;
		transformation = null;
		secretKey = null;
		ivSpec = null;
		pbeParamSpec = null;
		secretkeySpec = null;
		publicKey = null;
		privateKey = null;
		nonSunProviders = false;
		providerClass = null;
		padLen = 8;
	}

	public MXCipherX(String algTest, String modeTest, String paddingTest,
			String keyTest, String specTest) throws Exception {
		algorithm = "DESede";
		mode = "CBC";
		padding = "PKCS5Padding";
		key = "Sa#qk5usfmMI-@2dbZP9`jL3";
		spec = "beLd7$lB";
		modulus = "";
		cipherEncrypt = null;
		transformation = null;
		secretKey = null;
		ivSpec = null;
		pbeParamSpec = null;
		secretkeySpec = null;
		publicKey = null;
		privateKey = null;
		nonSunProviders = false;
		providerClass = null;
		padLen = 8;
		init(algTest, modeTest, paddingTest, keyTest, specTest, null, null);
	}

	public MXCipherX(String algTest, String modeTest, String paddingTest,
			String keyTest, String specTest, String modTest, String providerTest)
			throws Exception {
		algorithm = "DESede";
		mode = "CBC";
		padding = "PKCS5Padding";
		key = "Sa#qk5usfmMI-@2dbZP9`jL3";
		spec = "beLd7$lB";
		modulus = "";
		cipherEncrypt = null;
		transformation = null;
		secretKey = null;
		ivSpec = null;
		pbeParamSpec = null;
		secretkeySpec = null;
		publicKey = null;
		privateKey = null;
		nonSunProviders = false;
		providerClass = null;
		padLen = 8;
		init(algTest, modeTest, paddingTest, keyTest, specTest, modTest,
				providerTest);
	}
	@SuppressWarnings(value={"rawtypes","unchecked"})
	protected void init(String algTest, String modeTest, String paddingTest,
			String keyTest, String specTest, String modTest, String providerTest)
			throws Exception {
		try {
			if (providerTest != null && !providerTest.equals("")) {
				
				Class c = Class.forName(providerTest);
				Class paramTypes[] = new Class[0];
				
				Constructor ctor = c.getConstructor(paramTypes);
				Object params[] = new Object[0];
				providerClass = (Provider) ctor.newInstance(params);
				Security.addProvider(providerClass);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Provider provs[] = Security.getProviders();
		for (int xx = 0; xx < provs.length; xx++) {
			if (provs[xx].getName().toUpperCase().startsWith("SUN"))
				;
			nonSunProviders = true;
		}

		validateParams(algTest, modeTest, paddingTest, keyTest, specTest,
				modTest);
		transformation = algorithm;
		if (mode != null && !mode.equals("") && padding != null
				&& !padding.equals(""))
			transformation = (new StringBuilder()).append(transformation)
					.append("/").append(mode).append("/").append(padding)
					.toString();
		try {
			cipherEncrypt = buildCipher(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Cipher buildCipher(boolean encrypt) throws Exception {
		Cipher cipher = null;
		int cryptMode = !encrypt ? 2 : 1;
		if (algorithm.equals("DESede") || algorithm.equals("TripleDES")) {
			if (secretKey == null || ivSpec == null) {
				DESedeKeySpec keyspec = new DESedeKeySpec(key.getBytes());
				SecretKeyFactory factory = SecretKeyFactory
						.getInstance(algorithm);
				secretKey = factory.generateSecret(keyspec);
				ivSpec = new IvParameterSpec(spec.getBytes());
			}
			if (providerClass == null)
				cipher = Cipher.getInstance(transformation);
			else
				cipher = Cipher.getInstance(transformation, providerClass);
			if (transformation.indexOf("ECB") < 0)
				cipher.init(cryptMode, secretKey, ivSpec);
			else
				cipher.init(cryptMode, secretKey);
		} else if (algorithm.equals("DES")) {
			if (secretKey == null || ivSpec == null) {
				DESKeySpec keyspec = new DESKeySpec(key.getBytes());
				SecretKeyFactory factory = SecretKeyFactory
						.getInstance(algorithm);
				secretKey = factory.generateSecret(keyspec);
				ivSpec = new IvParameterSpec(spec.getBytes());
			}
			if (providerClass == null)
				cipher = Cipher.getInstance(transformation);
			else
				cipher = Cipher.getInstance(transformation, providerClass);
			if (transformation.indexOf("ECB") < 0)
				cipher.init(cryptMode, secretKey, ivSpec);
			else
				cipher.init(cryptMode, secretKey);
		} else if (algorithm.startsWith("PBEWith")) {
			if (secretKey == null || pbeParamSpec == null) {
				pbeParamSpec = new PBEParameterSpec(spec.getBytes(), 20);
				PBEKeySpec pbeKeySpec = new PBEKeySpec(spec.toCharArray());
				SecretKeyFactory keyFac = SecretKeyFactory
						.getInstance(algorithm);
				secretKey = keyFac.generateSecret(pbeKeySpec);
			}
			if (providerClass == null)
				cipher = Cipher.getInstance(transformation);
			else
				cipher = Cipher.getInstance(transformation, providerClass);
			cipher.init(cryptMode, secretKey, pbeParamSpec);
		} else if (algorithm.equals("RSA")) {
			if (publicKey == null || privateKey == null) {
				KeyFactory fac = KeyFactory.getInstance("RSA", providerClass);
				publicKey = fac.generatePublic(new RSAPublicKeySpec(
						new BigInteger(modulus), new BigInteger(key)));
				privateKey = fac.generatePrivate(new RSAPrivateKeySpec(
						new BigInteger(modulus), new BigInteger(spec)));
			}
			if (providerClass == null)
				cipher = Cipher.getInstance(transformation);
			else
				cipher = Cipher.getInstance(transformation, providerClass);
			if (encrypt)
				cipher.init(cryptMode, publicKey);
			else
				cipher.init(cryptMode, privateKey);
		} else {
			if (secretkeySpec == null) {
				int padLen = algorithm.equals("SKIPJACK") ? 10 : 16;
				byte byteArray[] = spec.getBytes();
				byteArray = pad(byteArray, padLen);
				secretkeySpec = new SecretKeySpec(byteArray, algorithm);
			}
			if (providerClass == null)
				cipher = Cipher.getInstance(transformation);
			else
				cipher = Cipher.getInstance(transformation, providerClass);
			cipher.init(cryptMode, secretkeySpec);
		}
		return cipher;
	}

	private void validateParams(String algTest, String modeTest,
			String paddingTest, String keyTest, String specTest, String modTest)
			throws Exception {
		if (algTest != null && !algTest.equals(""))
			algorithm = algTest;
		if (modeTest != null && !modeTest.equals(""))
			mode = modeTest;
		if (paddingTest != null && !paddingTest.equals(""))
			padding = paddingTest;
		if (keyTest != null && !keyTest.equals(""))
			key = keyTest;
		if (specTest != null && !specTest.equals(""))
			spec = specTest;
		if (modTest != null && !modTest.equals(""))
			modulus = modTest;
		if (algorithm == null)

			if (algorithm.equals("AES") || algorithm.equals("Serpent")
					|| algorithm.equals("MARS") || algorithm.equals("RC6")
					|| algorithm.equals("Rijndael")
					|| algorithm.equals("Square")
					|| algorithm.equals("Twofish"))
				padLen = 16;
			else if (algorithm.equals("RSA"))
				padLen = 0;
		if (!algorithm.equals("DES") && !algorithm.equals("DESede")
				&& !algorithm.equals("AES")) {
			if (modeTest == null || modeTest.equals(""))
				mode = "";
			if (paddingTest == null || paddingTest.equals(""))
				padding = "";
		}

	}

	public synchronized byte[] encData(String in) throws Exception {
		byte temp[] = in.getBytes();
		temp = pad(temp);
		byte encryptVal[] = null;
		try {
			encryptVal = cipherEncrypt.doFinal(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encryptVal;
	}

	protected byte[] pad(byte in[]) {
		return pad(in, padLen);
	}

	protected byte[] pad(byte in[], int padLen) {
		if (padLen == 0)
			return in;
		int inlen = in.length;
		int outlen = inlen;
		int rem = inlen % padLen;
		if (rem > 0)
			outlen = inlen + (padLen - rem);
		byte out[] = new byte[outlen];
		for (int xx = 0; xx < inlen; xx++)
			out[xx] = in[xx];

		return out;
	}

	String algorithm;
	String mode;
	String padding;
	String key;
	String spec;
	String modulus;
	private Cipher cipherEncrypt;
	String transformation;
	SecretKey secretKey;
	IvParameterSpec ivSpec;
	PBEParameterSpec pbeParamSpec;
	SecretKeySpec secretkeySpec;
	PublicKey publicKey;
	PrivateKey privateKey;
	boolean nonSunProviders;
	Provider providerClass;
	int padLen;
}
