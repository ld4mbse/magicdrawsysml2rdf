package edu.gatech.mbsec.adapter.magicdraw.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Helper class for encrypting strings.
 * @author rherrera
 */
public class Cypher {
    /**
     * Gets the MD5 hash of a string.
     * @param source the input string.
     * @return the MD5 hash of the input string.
     * @throws NoSuchAlgorithmException if no Provider supports a
     * {@link MessageDigestSpi} implementation for MD5.
     * @throws UnsupportedEncodingException if UTF-8 is not supported.
     */
    public static String md5(String source) throws NoSuchAlgorithmException,
            UnsupportedEncodingException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = source.getBytes("UTF-8");
        return new BigInteger(1, md.digest(bytes)).toString(16);
    }
}