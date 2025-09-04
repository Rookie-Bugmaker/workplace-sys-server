package com.crrc.sys.common;

import cn.hutool.crypto.asymmetric.AsymmetricCrypto;
import com.crrc.sys.cache.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.crypto.Cipher;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;


@Component
@Slf4j
public class RSAUtils {

    @Resource
    private RedisCache redisCache;

    public static final String SECURITY_KEY = "SECURITY_KEY_CACHE";

    public static final String SECURITY_PUB_KEY = "PUB_KEY";

    public static final String SECURITY_PRIVATE_KEY = "PRIVATE_KEY";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    public void generateKeyPair() throws NoSuchAlgorithmException {

        PublicKey publicKeyInCache = redisCache.getCacheMapValue(SECURITY_KEY, SECURITY_PUB_KEY);

        if (!Objects.isNull(publicKeyInCache)) {
            log.info("加密密钥已生成，跳过");
            return;
        }

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        KeyPair keyPair = keyGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        redisCache.setCacheMapValue(SECURITY_KEY, SECURITY_PUB_KEY, publicKey);
        redisCache.setCacheMapValue(SECURITY_KEY, SECURITY_PRIVATE_KEY, privateKey);

        log.info("加密密钥已生成");
    }

    public String getPublicKey() throws NoSuchAlgorithmException {
        PublicKey publicKey = redisCache.getCacheMapValue(SECURITY_KEY, SECURITY_PUB_KEY);
        if (Objects.isNull(publicKey)) {
            generateKeyPair();
            publicKey = redisCache.getCacheMapValue(SECURITY_KEY, SECURITY_PUB_KEY);
        }
        org.apache.commons.codec.binary.Base64.encodeBase64(publicKey.getEncoded());
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    public String getPrivateKey() throws NoSuchAlgorithmException {
        PrivateKey privateKey = redisCache.getCacheMapValue(SECURITY_KEY, SECURITY_PRIVATE_KEY);
        if (Objects.isNull(privateKey)) {
            generateKeyPair();
            privateKey = redisCache.getCacheMapValue(SECURITY_KEY, SECURITY_PRIVATE_KEY);
        }
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }


    public byte[] encrypt(String data) throws Exception {

        PublicKey publicKey = redisCache.getCacheMapValue(SECURITY_KEY, SECURITY_PUB_KEY);

        return encrypt(data, publicKey);
    }

    // 加密方法
    public byte[] encrypt(String data, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data.getBytes());
    }

    public String decrypt(String encryptedData) throws Exception {
        PrivateKey privateKey = redisCache.getCacheMapValue(SECURITY_KEY, SECURITY_PRIVATE_KEY);
        return decrypt(encryptedData, privateKey);
    }

    // 解密方法
    public String decrypt(String encryptedData, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    // 自定义公钥/私钥的RSA算法对象，公钥/私钥是Base64加密后的字符串
    public AsymmetricCrypto generateRSA(String publicKeyBase64, String privateKeyBase64) {
        RSAPublicKey publicKey = null;
        RSAPrivateKey privateKey = null;
        try {
            X509EncodedKeySpec publickeySpec = new X509EncodedKeySpec((new BASE64Decoder()).decodeBuffer(publicKeyBase64));
            PKCS8EncodedKeySpec privatekeySpec = new PKCS8EncodedKeySpec((new BASE64Decoder()).decodeBuffer(privateKeyBase64));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = (RSAPrivateKey)keyFactory.generatePrivate(privatekeySpec);
            publicKey = (RSAPublicKey)keyFactory.generatePublic(publickeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return new AsymmetricCrypto("RSA", privateKey, publicKey);
    }

    // 生成RSA算法对象
    public AsymmetricCrypto generateRSA() {
        RSAPublicKey publicKey = null;
        RSAPrivateKey privateKey = null;
        try {
            X509EncodedKeySpec publickeySpec = new X509EncodedKeySpec((new BASE64Decoder()).decodeBuffer(getPublicKey()));
            PKCS8EncodedKeySpec privatekeySpec = new PKCS8EncodedKeySpec((new BASE64Decoder()).decodeBuffer(getPrivateKey()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKey = (RSAPrivateKey)keyFactory.generatePrivate(privatekeySpec);
            publicKey = (RSAPublicKey)keyFactory.generatePublic(publickeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
        return new AsymmetricCrypto("RSA", privateKey, publicKey);
    }


    /**
     * 私钥签名
     *
     * @param plainText  明文
     * @return 签名字符串
     */
    public String sign(String plainText) {
        String signStr = null;
        byte[] signeBytes;
        try {
            log.info("签名前明文:{}", plainText);
            PrivateKey privateKey = redisCache.getCacheMapValue(SECURITY_KEY, SECURITY_PRIVATE_KEY);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(privateKey);
            signature.update(plainText.getBytes());
            signeBytes = signature.sign();
            signStr = Base64.getEncoder().encodeToString(signeBytes);
            log.info("签名后密文:{}", signStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return signStr;
    }

    /**
     * 公钥验签
     *
     * @param plainText 待验签明文
     * @param signStr   签名密文
     * @return true/false
     */
    public boolean verifySign(String plainText, String signStr) {
        boolean verifySignSuccess = false;
        try {
            PublicKey publicKey = redisCache.getCacheMapValue(SECURITY_KEY, SECURITY_PUB_KEY);
            Signature verifySign = Signature.getInstance(SIGNATURE_ALGORITHM);
            verifySign.initVerify(publicKey);
            verifySign.update(plainText.getBytes());
            verifySignSuccess = verifySign.verify(Base64.getDecoder().decode(signStr));
            log.info("公钥验签结果:{}", verifySignSuccess);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verifySignSuccess;
    }

}
