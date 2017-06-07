package com.androidquanjiakan.encode;

import org.apache.commons.codec.binary.Base64;

public class RSAHttp {


	/**
	 * 转换为Http传输字符
	 * 1. + URL 中+号表示空格 %2B
	 * 2. 空格 URL中的空格可以用+号或者编码 %20
	 * 3. / 分隔目录和子目录 %2F
	 * 4. ? 分隔实际的 URL 和参数 %3F
	 * 5. % 指定特殊字符 %25
	 * 6. # 表示书签 %23
	 * 7. & URL 中指定的参数间的分隔符 %26
	 * 8. = URL 中指定参数的值 %3D
	 */
	public static String httpTransferCharacter(String src) {
		String dscTemp = src;

		StringBuilder sbTab = new StringBuilder();
		sbTab.append("\\").append(0x0A);	//换行
		StringBuilder sbReturn = new StringBuilder();
		sbReturn.append("\\").append(0x0D);	//回车
		StringBuilder sbSpace = new StringBuilder();
		sbSpace.append("\\").append(0x20);	//空格

		dscTemp = dscTemp.replaceAll("\\+", "%2B");
		dscTemp = dscTemp.replaceAll("\\ ", "%20");
		//dscTemp = dscTemp.replaceAll(sbTab.toString(), "%0A");
		//dscTemp = dscTemp.replaceAll(sbReturn.toString(), "%0D");
		//dscTemp = dscTemp.replaceAll(sbSpace.toString(), "%20");
		//dscTemp = dscTemp.replaceAll("\\%", "%25");
		//dscTemp = dscTemp.replaceAll("\\#", "%23");
		//dscTemp = dscTemp.replaceAll("\\&", "%26");
		//dscTemp = dscTemp.replaceAll("\\/", "%2F");
		//dscTemp = dscTemp.replaceAll("\\?", "%3F");
		//dscTemp = dscTemp.replaceAll("\\=", "%3D");
		return dscTemp;
	}

	public static String httpPactualCharacter(String src) {
		String dscTemp = src;

		//StringBuilder sbTab = new StringBuilder();
		//sbTab.append("\\").append(0x0A);	//换行
		//StringBuilder sbReturn = new StringBuilder();
		//sbReturn.append("\\").append(0x0D);	//回车
		//StringBuilder sbSpace = new StringBuilder();
		//sbSpace.append("\\").append(0x20);	//空格

		dscTemp = dscTemp.replaceAll("%2B", "+");
		dscTemp = dscTemp.replaceAll("%20", " ");
		//dscTemp = dscTemp.replaceAll(sbTab.toString(), "%0A");
		//dscTemp = dscTemp.replaceAll(sbReturn.toString(), "%0D");
		//dscTemp = dscTemp.replaceAll(sbSpace.toString(), "%20");
		//dscTemp = dscTemp.replaceAll("\\%", "%25");
		//dscTemp = dscTemp.replaceAll("\\#", "%23");
		//dscTemp = dscTemp.replaceAll("\\&", "%26");
		//dscTemp = dscTemp.replaceAll("\\/", "%2F");
		//dscTemp = dscTemp.replaceAll("\\?", "%3F");
		//dscTemp = dscTemp.replaceAll("\\=", "%3D");
		return dscTemp;
	}

	/**
	 * 私钥解密数据
	 * @param ciphertext  密文
	 * @param privateKey  私钥
	 * @return
	 */
	public static String privateDecryptData(String ciphertext, String privateKey) {
		String content = null;

		ciphertext = httpPactualCharacter(ciphertext);

		byte[] ciphertextBytes = Base64.decodeBase64(ciphertext);
		byte[] decodedData = null;
		try {
			decodedData = RSACoder.decryptByPrivateKey(ciphertextBytes, privateKey);
		} catch (Exception e) {
		}
		if (decodedData != null) {
			content = new String(decodedData);
		}
		return content;
	}

	/**
	 * 公钥解密数据
	 * @param ciphertext  密文
	 * @param publicKey   公钥
	 * @return
	 */
	public static String publicDecryptData(String ciphertext, String publicKey) {
		String content = null;
		byte[] ciphertextBytes = Base64.decodeBase64(ciphertext.getBytes());
		byte[] decodedData = null;
		try {
			decodedData = RSACoder.decryptByPublicKey(ciphertextBytes, publicKey);
		} catch (Exception e) {
		}
		if (decodedData != null) {
			content = new String(decodedData);
		}
		return content;
	}

}
