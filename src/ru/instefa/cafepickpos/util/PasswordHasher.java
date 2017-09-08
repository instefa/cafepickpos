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


import java.util.Arrays;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

import ru.instefa.cafepickpos.PosLog;

public class PasswordHasher {

	public static void main(String[] args) throws Exception {
		PosLog.info(PasswordHasher.class, hashPassword("123".toCharArray())); //$NON-NLS-1$
	}

    /**
     * String -> char[] to improve security.
     * @param password chars to hash
     * @return hashed password in hex
     */
	public static String hashPassword(char[] password) {
        String hashedPassword = "";
		byte[] passwordBytes = toBytes(password);
        
		try {
			MessageDigest md = MessageDigest.getInstance("SHA1"); //$NON-NLS-1$
            hashedPassword = Hex.encodeHexString(md.digest(passwordBytes));
		} catch (NoSuchAlgorithmException e) {
			PosLog.error(PasswordHasher.class, e.getMessage());
		}

		return hashedPassword;
	}

    /**
     * Helper, converts char[] to byte[] without
     * using string pool to improve password security.
     * Takes in account trailing 0 in bytes array.
     * @param chars password char array
     * @return password as byte array
     */
    private static byte[] toBytes(char[] chars) {
        CharBuffer cb = CharBuffer.wrap(chars);
        ByteBuffer bb = Charset.forName("UTF-8").encode(cb);
        byte[] bytes = Arrays.copyOfRange(bb.array(), bb.position(), bb.limit());
        Arrays.fill(cb.array(), '\u0000'); // clear sensitive data
        Arrays.fill(bb.array(), (byte) 0); // clear sensitive data
        return bytes;
    }
}