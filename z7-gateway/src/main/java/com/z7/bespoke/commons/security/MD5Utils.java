package com.z7.bespoke.commons.security;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;


/**
 * 项目名称：review-frame
 * 类 名 称：MD5Utils
 * 类 描 述：TODO MD5校验
 * 创建时间：2023/4/24 4:12 下午
 * 创 建 人：z7
 */
public class MD5Utils {

    /**
     * 签名字符串
     *
     * @param text          需要签名的字符串
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static String sign(String text, String input_charset) {
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(getContentBytes(text, input_charset));
    }


    public static String sign(String text) {
        return sign(text, "UTF-8");
    }

    /**
     * 签名字符串
     *
     * @param text          需要签名的字符串
     * @param sign          签名结果
     * @param input_charset 编码格式
     * @return 签名结果
     */
    public static boolean verify(String text, String sign, String input_charset) {
        String mysign = org.apache.commons.codec.digest.DigestUtils.md5Hex(getContentBytes(text, input_charset));
        if (mysign.equals(sign)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    private static byte[] getContentBytes(String content, String charset) {
        if (charset == null || "".equals(charset)) {
            return content.getBytes();
        }
        try {
            return content.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Wrong Charset! Your Charset Is:" + charset);
        }
    }
}
