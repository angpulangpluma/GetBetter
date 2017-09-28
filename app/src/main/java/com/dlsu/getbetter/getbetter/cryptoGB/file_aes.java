package com.dlsu.getbetter.getbetter.cryptoGB;

/**
 * Created by YING LOPEZ on 9/28/2017.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;

public class file_aes {

    private aes filealgo;
//    private long encryptTime;
//    private long decryptTime;

    public file_aes(){
        filealgo = new aes();
        filealgo.setKey();
    }

    public file_aes(aes enc){
        this.filealgo = enc;
        filealgo.setKey();
    }

    public aes getCrypt(){
        return filealgo;
    }

    /*
    Implementation for encryptFile() from www.macs.hw.ac.uk/~ml355/lore/FileEncryption.java
    */

    public void encryptFile(File file){
        File encrypted = new File("demo2\\" + returnFileName(file)+"_encrypted."+returnFileExt(file));
        Cipher cp = filealgo.getCipher();
        SecretKeySpec k = filealgo.getKey();
        try{
            FileInputStream in = new FileInputStream(file);
            cp.init(Cipher.ENCRYPT_MODE, k);
            CipherOutputStream os = new CipherOutputStream(new FileOutputStream(encrypted),
                    cp);

//            long startTime = System.currentTimeMillis();
            copy(in, os);
//            long endTime = System.currentTimeMillis();
//            System.out.println("startTime - " + startTime);
//            System.out.println("endTime - " + endTime);
//            encryptTime = endTime - startTime;

            in.close();
            os.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }
    }

    /*
    Implementation for decryptFile() from www.macs.hw.ac.uk/~ml355/lore/FileEncryption.java
    */

    public void decryptFile(File file){
        File decrypted = new File("demo2de\\" + returnFileName(file)+"_decrypted."+returnFileExt(file));
        Cipher cp = filealgo.getCipher();
        SecretKeySpec k = filealgo.getKey();
        try{
            FileOutputStream os = new FileOutputStream(decrypted);
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//            AlgorithmParameters.getInstance("AES");
////            if (iv!=null)
//             byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
//            IvParameterSpec ivspec = new IvParameterSpec(iv);
            cp.init(Cipher.DECRYPT_MODE, k);
//            else cipher.init(Cipher.DECRYPT_MODE, k);
            CipherInputStream is = new CipherInputStream(new FileInputStream(file),
                    cp);

//            long startTime = System.currentTimeMillis();
            copy(is, os);
//            long endTime = System.currentTimeMillis();
//            System.out.println("startTime - " + startTime);
//            System.out.println("endTime - " + endTime);
//            decryptTime = endTime - startTime;

            is.close();
            os.close();
        } catch(Exception ex){
            ex.printStackTrace();
        }

    }

    private String returnFileExt(File file){
        String ext = "";
        int i = file.getName().lastIndexOf('.');
        if (i >= 0)
            ext = file.getName().substring(i+1);
        return ext;
    }

    private String returnFileName(File file){
        String filename = "";
        int i = file.getName().lastIndexOf('.');
        if (i >= 0)
            filename = file.getName().substring(0, i);
        return filename;
    }

//    public long getEncryptedTime(){
//        return this.encryptTime;
//    }
//
//    public long getDecryptedTime(){
//        return this.decryptTime;
//    }

    /*
    Implementation for copy() from www.macs.hw.ac.uk/~ml355/lore/FileEncryption.java
    */

    private void copy(InputStream is, OutputStream os){
        int i;
        byte[] b = new byte[1024];
        try{
            while((i=is.read(b))!=-1) {
                os.write(b, 0, i);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
