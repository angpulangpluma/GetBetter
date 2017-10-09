package com.dlsu.getbetter.getbetter.cryptoGB;

/**
 * Created by YING LOPEZ on 9/28/2017.
 */

import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;

import javax.crypto.Cipher;

public class text_aes {
    private aes textalgo;
    private Cipher ciph;

    public text_aes() {
        textalgo = new aes();
        textalgo.setKey();
        ciph = textalgo.getCipher();
    }

    public text_aes(aes enc){
        this.textalgo = enc;
        this.ciph = textalgo.getCipher();
    }

    public aes getCrypt(){
        return textalgo;
    }

    public String getEncString(String str){
        String decrypted = "";

        try{
            ciph = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            ciph.init(Cipher.DECRYPT_MODE, textalgo.getKey());
            decrypted = new String(ciph.doFinal(Base64.decodeBase64(str)));
        }catch(Exception e){
            e.printStackTrace();
        }
        return decrypted;
    }

    public String getDecString(String str){
        String encrypted = "";
        try{
            ciph = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            ciph.init(Cipher.ENCRYPT_MODE, textalgo.getKey());
            encrypted = Base64.encodeBase64String(ciph.doFinal(str.getBytes("UTF-8")));
        }catch(Exception e){
            e.printStackTrace();
        }
        return encrypted;
       }

    /*
    //use SHA-256 with salt start and end, check whether adding salt per
    //letter works for both web and mobile based on how fast it goes
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
    */
    public String getHashedString(String word){
        String result = "";
        byte[] hashed;
        MessageDigest digest;
        try{
            digest = MessageDigest.getInstance("SHA-256");
            hashed = digest.digest(word.getBytes("UTF-8"));
            result = new String(hashed, "UTF-8");
        } catch(Exception e){
            e.printStackTrace();
            result = "";
        }
        return result;
    }

}