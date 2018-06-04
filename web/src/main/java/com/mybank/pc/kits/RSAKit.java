package com.mybank.pc.kits;

import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;

public class RSAKit {

	public static String encrypt(String content, PublicKey publicKey) throws Exception {
		return encrypt(content, getKeyString(publicKey));
	}

	public static String decrypt(String content, PrivateKey privateKey) throws Exception {
		return decrypt(content, getKeyString(privateKey));
	}

	public static String encrypt(String content, String publicKey) throws Exception {
		try {
			PublicKey pubKey = getPublicKey(publicKey);
			byte[] dataBytes = content.getBytes("UTF-8");
			if (dataBytes.length > 117) {
				int allLen = calculate(dataBytes.length, 100) * 128;
				byte[] encodedData = new byte[allLen];
				int len = 0;
				for (int i = 0; i < dataBytes.length; i += 100) {
					byte[] doFinal = encrypt(ArrayUtils.subarray(dataBytes, i, i + 100), pubKey);
					System.arraycopy(doFinal, 0, encodedData, len, doFinal.length);
					len += doFinal.length;
				}
				return Base64.encodeBase64String(encodedData);
			}
			byte[] encodedData = encrypt(dataBytes, pubKey);
			return Base64.encodeBase64String(encodedData);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static String decrypt(String content, String privateKey) throws Exception {
		try {
			PrivateKey priKey = getPrivateKey(privateKey);
			byte[] dataBytes = Base64.decodeBase64(content);
			if (dataBytes.length > 128) {
				int allLen = calculate(dataBytes.length, 128) * 128;
				byte[] tmp = new byte[allLen];
				int len = 0;
				for (int i = 0; i < dataBytes.length; i += 128) {
					byte[] doFinal = decrypt(ArrayUtils.subarray(dataBytes, i, i + 128), priKey);
					System.arraycopy(doFinal, 0, tmp, len, doFinal.length);
					len += doFinal.length;
				}
				byte[] decodedData = new byte[len];
				System.arraycopy(tmp, 0, decodedData, 0, len);
				return new String(decodedData, "UTF-8");
			}

			byte[] encodedData = decrypt(dataBytes, priKey);
			return new String(encodedData, "UTF-8");

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private static int calculate(int len, int size) {
		int inte = len / size;
		int surplusLen = len - inte * size;
		if (surplusLen > 0) {
			inte++;
		}
		return inte;
	}

	// 将base64编码后的公钥字符串转成PublicKey实例
	public static PublicKey getPublicKey(String publicKey) throws Exception {
		byte[] key = decryptBASE64(publicKey);
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePublic(publicKeySpec);
	}

	// 将base64编码后的私钥字符串转成PrivateKey实例
	public static PrivateKey getPrivateKey(String privateKey) throws Exception {
		byte[] key = decryptBASE64(privateKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(key);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		return keyFactory.generatePrivate(keySpec);
	}

	public static byte[] decryptBASE64(String key) throws IOException {
		return Base64.decodeBase64(key.getBytes());
	}

	public static String getKeyString(Key key) throws Exception {
		byte[] keyBytes = key.getEncoded();
		return Base64.encodeBase64String(keyBytes);
	}

	public static byte[] encrypt(byte[] content, PublicKey publicKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(content);
	}

	public static byte[] decrypt(byte[] b1, PrivateKey privateKey) throws Exception {
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		return cipher.doFinal(b1);
	}

}