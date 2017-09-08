/**
 * ********************************************************
 * Copyright 2002, 2003 by Paul Rubin.
 * Copying license: same as Python 2.3 license.
 * Ported to Java, 20170805, pymancer <pymancer@gmail.com>.
 * ********************************************************
 */
 /*
 * P3Decryptor.java
 *
 * Created on August 5, 2017, 13:40
 * @author pymancer
 * @since 2017.0.1
 */
package ru.instefa.cafepickpos.util;

import java.util.List;
import java.util.Arrays;
import java.util.Base64;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.ArrayUtils;
import com.google.common.primitives.Bytes;

import ru.instefa.cafepickpos.PosLog;
import ru.instefa.cafepickpos.exceptions.CryptorException;

/**
 * Decrypts p3 cipher using key.
 * Pure functions and variables container (namespace), no need to instantiate.
 */
public class P3Decryptor {
    private static final int IVLEN = 16;
    private static final int MACLEN = 8;
    private static final byte[] IPAD = genPadBytes((byte)0x36, 64);
    private static final byte[] OPAD = genPadBytes((byte)0x5c, 64);
    private static final byte[] ITRANS = genTransString((byte)0x36, 256);
    private static final byte[] OTRANS = genTransString((byte)0x5c, 256);
    
    /**
     * 
     * @param cipher CafePick encrypted connection string
     * @param key CafePick connection string password
     * @return decrypted connection string
     * @throws ru.instefa.cafepickpos.exceptions.CryptorException
     * @throws java.io.UnsupportedEncodingException
     */
    public static String p3Decrypt(String cipher, String key) throws CryptorException, UnsupportedEncodingException {
        byte[] keyB = key.getBytes("UTF-8");
        byte[] cipherB = Base64.getDecoder().decode(cipher);

        int cipherLength = cipherB.length - IVLEN - MACLEN;
        
        if (cipherLength < 0) {
            throw new CryptorException("invalid ciphertext");
        }

        byte[] nonce = Arrays.copyOfRange(cipherB, 0, IVLEN);
        
        byte[] streamEnd = "0000".getBytes("UTF-8");
        streamEnd = Arrays.copyOfRange(streamEnd, cipherLength & 3, streamEnd.length);
        byte[] stream = ArrayUtils.addAll(Arrays.copyOfRange(cipherB, IVLEN, IVLEN + cipherLength), streamEnd);
        
        byte[] auth = Arrays.copyOfRange(cipherB, IVLEN + cipherLength, cipherB.length);

        byte[] kEnc = hash(ArrayUtils.addAll(ArrayUtils.addAll("enc".getBytes("UTF-8"), keyB), nonce));
        byte[] kAuth = hash(ArrayUtils.addAll(ArrayUtils.addAll("auth".getBytes("UTF-8"), keyB), nonce));
        
        byte[] vAuth = Arrays.copyOfRange(hmac(Arrays.copyOfRange(cipherB, 0, IVLEN + cipherLength), kAuth), 0, MACLEN);
        
        if (!Arrays.equals(auth, vAuth)) {
            throw new CryptorException("invalid key or ciphertext");
        }

        // could use `long` for speed
        byte[] xKey = expandKey(kEnc, cipherLength + streamEnd.length);

        for (int i=0; i<stream.length; i++) {
            stream[i] = (byte)(stream[i] ^ xKey[i]);
        }
        
        return new String(Arrays.copyOfRange(stream, 0, cipherLength), "UTF-8");
    }
    
    /**
     * calculate input's digest using hash algorithm
     * @param value input byte[]
     * @return calculated digest
     */
    private static byte[] hash(byte[] value) {
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("SHA1"); //$NON-NLS-1$
		} catch (NoSuchAlgorithmException e) {
			PosLog.error(P3Decryptor.class, e.getMessage());
		}

		return md != null ? md.digest(value) : new byte[0];
    }
    
    private static byte[] genPadBytes(byte b, int num) {
        byte[] result = new byte[num];
        Arrays.fill(result, (byte)b);
        return result;
    }
    
    private static byte[] genTransString(byte b, int num) {
        byte[] result = new byte[num];
        
        for (int i=0; i<result.length; i++) {
            result[i] = (byte)(i ^ b);
        }
        
        return result;
    }
    
    private static byte[] translate(byte[] target, byte[] dict) {
        assert dict.length == 256;
        byte[] result = new byte[target.length];
        int ord;
        
        for (int i=0; i<target.length; i++) {
            ord = target[i] >= 0 ? target[i] : 256 + target[i];
            result[i] = dict[ord];
        }
        
        return result;
    }
    
    private static byte[] hmac(byte[] msg, byte[] key) {
        if (key.length > 64) {
            key = hash(key);
        }
        byte[] ki = Arrays.copyOfRange(ArrayUtils.addAll(translate(key, ITRANS), IPAD), 0, 64);
        byte[] ko = Arrays.copyOfRange(ArrayUtils.addAll(translate(key, OTRANS), OPAD), 0, 64);
        return hash(ArrayUtils.addAll(ko, hash(ArrayUtils.addAll(ki, msg))));
    }
    
    private static byte[] expandKey(byte[] key, int cLen) {
        int blocks = (cLen + 19) / 20;
        List<byte[]> xKey = new ArrayList<>();
        byte[] seed = key.clone();
        
        for (int i=0; i<blocks; i++) {
            seed = hash(ArrayUtils.addAll(key, seed));
            xKey.add(seed);
        }
        
        List<Byte> xKeyMono = new ArrayList<>();
        for (byte[] bs : xKey) {
            for (byte b : bs) {
                xKeyMono.add(b);
            }
        }
        
        byte[] result = Bytes.toArray(xKeyMono);
        return result;
    }
}
