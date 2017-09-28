package com.dlsu.getbetter.getbetter.cryptoGB;

/**
 * Created by YING LOPEZ on 9/28/2017.
 */

import java.security.MessageDigest;

public class text_aes {
    private aes textalgo;

    public text_aes() {
        textalgo = new aes();
        textalgo.setKey();
    }

    public text_aes(aes enc){
        this.textalgo = enc;
    }

    public aes getCrypt(){
        return textalgo;
    }

    public String getEncString(String str){
        return textalgo.getEncryptedString(str);
    }

    public String getDecString(String str){
        return textalgo.getDecryptedString(str);
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
