package com.z7.bespoke.frame.thirdparty.feishu.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * 项目名称：nb-inext
 * 类 名 称：NotifyDataDecrypter
 * 类 描 述：TODO 解密订阅事件发送key的内容
 * 创建时间：2023/6/12 2:46 下午
 * 创 建 人：z7
 */
@Slf4j
public class Decrypt {

    /*
     * 事件内容采用 AES-256-CBC 加密，加密原理如下：
     * 1.使用 SHA256 对 Encrypt Key进行哈希得到密钥key。
     * 2.使用 PKCS7Padding 方式将事件内容进行填充。
     * 3.生成 16 字节的随机数作为初始向量 iv。
     * 4.使用 iv 和 key 对事件内容加密得到 encrypted_event。
     * 5.应用收到的密文为 base64(iv+Encrypted_event)。
     */
    private byte[] keyBs;

    /**
     * @param encryptKey 飞书应用配置的 Encrypt Key
     * @description:
     */
    public Decrypt(String encryptKey) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            // won't happen
        }
        keyBs = digest.digest(encryptKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @param encrypt 请求json encrypt的对应的值
     * @description:
     */
    public String decrypt(String encrypt) throws Exception {

        byte[] decode = Base64.getDecoder().decode(encrypt);
        Cipher cipher = Cipher.getInstance("AES/CBC/NOPADDING");

        byte[] iv = new byte[16];
        System.arraycopy(decode, 0, iv, 0, 16);

        byte[] data = new byte[decode.length - 16];
        System.arraycopy(decode, 16, data, 0, data.length);

        this.changerCryptoJceSecurity();

        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBs, "AES"), new IvParameterSpec(iv));
        byte[] r = cipher.doFinal(data);
        if (r.length > 0) {
            int p = r.length - 1;
            for (; p >= 0 && r[p] <= 16; p--) {
            }
            if (p != r.length - 1) {
                byte[] rr = new byte[p + 1];
                System.arraycopy(r, 0, rr, 0, p + 1);
                r = rr;
            }
        }
        return new String(r, StandardCharsets.UTF_8);
    }

    /**
     * jdk 1.8以下使用AES时如果秘钥大于128会报错：java.security.InvalidKeyException: Illegal key size
     * 解决办法：
     * 1、升级jdk版本
     * 2、替换jdk中的jar包
     * 3、利用反射绕过
     *
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     */
    private void changerCryptoJceSecurity() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");
        //这个field是 private static final的，需要找到这个field的modifiers，将final去掉，才能修改
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        //修改field
        field.setAccessible(true);
        field.set(null, false);
    }


//    public static void main(String[] args) throws Exception {
//        Decrypt f = new Decrypt("6YezwdTZnkkei0lTgsMyMc2PJBTuc7JW");
//        String decrypt = f.decrypt("AnNGYMujvEMCoei2FRXasUf7NzJfa0gIgc0dshuozl2hFGU0vvF5yZo3dZYDMrWabtrFtxA9wxAqYhr4PYHX1dFWxXVpxFZlVAWAXZCxjgQwSI/BOMIMzIRxPFTwE7ax5LkPdM5sVgvaKPq/HZ+zs+iEAYWd/KPhg4ch2EhoeNsioMPAelpDGEe3AGM6p71J");
//        System.out.println(decrypt);
//    }
}
