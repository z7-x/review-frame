package com.z7.bespoke.frame.thirdparty.feishu.utils;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 项目名称：nb-inext
 * 类 名 称：Signature
 * 类 描 述：TODO  签名校验
 * 创建时间：2023/6/12 6:07 下午
 * 创 建 人：z7
 */
public class Signature {

    /*
        1.将请求头 X-Lark-Request-Timestamp、X-Lark-Request-Nonce 与 encrypt_key 拼接后 按照 encode('utf-8') 编码得到 byte[] b1，
        2.再拼接上请求的原始 body, 得到一个 byte[] b。将 b 用 sha256 加密，得到字符串 s， 校验 s 是否和请求头 X-Lark-Signature 一致。
    */

    /**
     * @param timestamp  Http请求头中的X-Lark-Request-Timestamp
     * @param nonce      Http请求头中的X-Lark-Request-Nonce
     * @param encryptKey 飞书应用后台自己配置的encrypt key
     * @param bodyString 事件请求json串
     */
    public static String calculateSignature(String timestamp,
                                            String nonce,
                                            String encryptKey,
                                            String bodyString) throws NoSuchAlgorithmException {
        StringBuilder content = new StringBuilder();
        content.append(timestamp).append(nonce).append(encryptKey).append(bodyString);
        MessageDigest alg = MessageDigest.getInstance("SHA-256");
        return Hex.encodeHexString(alg.digest(content.toString().getBytes()));
    }
}
