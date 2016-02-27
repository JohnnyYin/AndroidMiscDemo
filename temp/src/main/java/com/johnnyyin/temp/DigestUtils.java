package com.johnnyyin.temp;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Digest utility
 */
public class DigestUtils {

    static final char[] HEX_CHARS = {
        '0','1','2','3','4','5','6','7','8','9',
        'a','b','c','d','e','f'
    };

    /** get hex string of specified bytes */
    public static String toHexString(byte[] bytes, int off, int len) {
        if (bytes == null)
            throw new NullPointerException("bytes is null");
        if (off < 0 || (off + len) > bytes.length)
            throw new IndexOutOfBoundsException();
        char[] buff = new char[len * 2];
        int v;
        int c = 0;
        for (int i = 0; i < len; i++) {
            v = bytes[i+off] & 0xff;
            buff[c++] = HEX_CHARS[(v >> 4)];
            buff[c++] = HEX_CHARS[(v & 0x0f)];
        }
        return new String(buff, 0, len * 2);
    }

    public static byte[] hexStringToBytes(final String s) throws IllegalArgumentException {
        if (s == null || (s.length () % 2) == 1)
            throw new IllegalArgumentException("hexBinary needs to be even-length: " + s);
        final char[] chars = s.toCharArray();
        final int len = chars.length;
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(chars[i], 16) << 4) + Character.digit(chars[i + 1], 16));
        }
        return data;
    }

    /** get hexadecimal md5 digest of file */
    public static String md5Hex(File file) {
        try {
            MessageDigest digester = MessageDigest.getInstance("MD5");
            if (digester == null)
                return null;
            FileInputStream in = new FileInputStream(file);
            byte[] buff = new byte[1024 * 8];
            int n;
            while ((n = in.read(buff, 0, buff.length)) > 0) {
                digester.update(buff, 0, n);
            }
            in.close();
            byte[] d = digester.digest();
            return toHexString(d, 0, d.length);
        } catch (Exception e) {
            return null;
        }
    }

    /** get hexadecimal md5 digest of given string (its UTF-8 encoded bytes) */
    public static String md5Hex(String str) {
        try {
            if (str == null || str.length() == 0)
                return null;
            MessageDigest digester = MessageDigest.getInstance("MD5");
            if (digester == null)
                return null;
            byte[] data = str.getBytes("UTF-8");
            digester.update(data);
            byte[] d = digester.digest();
            if (d == null || d.length < 1)
                return null;
            return toHexString(d, 0, d.length);
        } catch (Exception e) {
            return null;
        }
    }

    public static String md5Hex(byte[] data) {
        try {
            if (data == null || data.length == 0)
                return null;
            MessageDigest digester = MessageDigest.getInstance("MD5");
            if (digester == null)
                return null;
            digester.update(data);
            byte[] d = digester.digest();
            if (d == null || d.length < 1)
                return null;
            return toHexString(d, 0, d.length);
        } catch (Exception e) {
            return null;
        }
    }
}
