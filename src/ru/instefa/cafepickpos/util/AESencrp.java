/**
 * ************************************************************************
 * * The contents of this file are subject to the MRPL 1.2
 * * (the  "License"),  being   the  Mozilla   Public  License
 * * Version 1.1  with a permitted attribution clause; you may not  use this
 * * file except in compliance with the License. You  may  obtain  a copy of
 * * the License at http://www.floreantpos.org/license.html
 * * Software distributed under the License  is  distributed  on  an "AS IS"
 * * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * * License for the specific  language  governing  rights  and  limitations
 * * under the License.
 * * The Original Code is FLOREANT POS.
 * * The Initial Developer of the Original Code is OROCUBE LLC
 * * All portions are Copyright (C) 2015 OROCUBE LLC
 * * All Rights Reserved.
 * * Contributor(s): pymancer <pymancer@gmail.com>.
 * ************************************************************************
 */
package ru.instefa.cafepickpos.util;

import java.util.Base64;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import ru.instefa.cafepickpos.PosLog;

public class AESencrp {

	private static final String ALGO = "AES"; //$NON-NLS-1$
	private static final byte[] KEYVALUE = new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't',

	'S', 'e', 'c', 'r', 'e', 't', 'K', 'e', 'y' };

	public static String encrypt(String Data) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
        // 20170824, pymancer, proprietary sun.misc.BASE64Encoder replaced
		return Base64.getEncoder().encodeToString(encVal);
	}

	public static String decrypt(String encryptedData) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
        // 20170824, pymancer, proprietary sun.misc.BASE64Decoder replaced
		byte[] decValue = c.doFinal(Base64.getDecoder().decode(encryptedData));
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}

	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(KEYVALUE, ALGO);
		return key;
	}
	
	public static void main(String[] args) throws Exception {
//		String s = "12458";
//		String encrypt = encrypt(s);
//		PosLog.debug(getClass(),encrypt);
		
		String decrypt = decrypt("4T9H+1LqawVTsVvifd/TxA=="); //$NON-NLS-1$
		PosLog.info(AESencrp.class, decrypt);
	}

}
