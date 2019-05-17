package com.airlenet;

//import org.apache.tomcat.util.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSA {
    private static String src = "123456";

    private static String publicKey="MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAIuvXCEiqWwv0bWlc+84IQQ5FokRdCfA\n" +
            "SG6+U8pzU7ymBmEyZkH7clbQx7ooowsVtZkU+e8ntV3NKPkQNzR/7XcCAwEAAQ==";
    private static String privateKey="";

    @Test
    public void jdkRSA() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(512);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();           //公钥
            RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();       //私钥
            System.out.println("public key:" + Base64.encodeBase64String(rsaPublicKey.getEncoded()));
            System.out.println("private key:" + Base64.encodeBase64String(rsaPrivateKey.getEncoded()));

            //私钥加密，公钥解密--加密
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] result = cipher.doFinal(src.getBytes());
            System.out.println("RSA私钥加密，公钥解密--加密:" + Base64.encodeBase64String(result));

            //私钥加密，公钥解密--解密
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
            keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            result = cipher.doFinal(result);
            System.out.println("RSA私钥加密，公钥解密--解密:" + new String(result));

            //公钥加密，私钥解密--加密
            x509EncodedKeySpec = new X509EncodedKeySpec(rsaPublicKey.getEncoded());
            keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            result = cipher.doFinal(src.getBytes());
            System.out.println("RSA公钥加密，私钥解密--加密:" + Base64.encodeBase64String(result));

            //公钥加密，私钥解密--解密
            pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(rsaPrivateKey.getEncoded());
            keyFactory = KeyFactory.getInstance("RSA");
            privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            result = cipher.doFinal(result);
            System.out.println("RSA公钥加密，私钥解密--解密:" + new String(result));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
