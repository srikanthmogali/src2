package com.dropbox.core.util; /**
 * Created by srikanthmogali on 9/14/14.
 */
import java.io.*;
import java.security.*;
import java.security.spec.*;

import javax.crypto.*;
import javax.crypto.spec.*;
//working finally
//need to do research on openssl
//if required to get more keys like public keys and private keys

/**
 * Utility class for encrypting/decrypting files.
 * @author Michael Lones
 */
public class FileEncryption {

    public static final int AES_Key_Size = 256;

    Cipher pkCipher, aesCipher;
    byte[] aesKey;
    SecretKeySpec aeskeySpec;

    /**
     * Constructor: creates ciphers
     */
    public FileEncryption() throws GeneralSecurityException {
        // create RSA public key cipher
        pkCipher = Cipher.getInstance("RSA");
        // create AES shared key cipher
        aesCipher = Cipher.getInstance("AES","SunJCE");
    }

    /**
     * Creates a new AES key
     */
    public void makeKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(AES_Key_Size);
        SecretKey key = kgen.generateKey();
        aesKey = key.getEncoded();
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
    }

    /**
     * Decrypts an AES key from a file using an RSA private key
     */
    public void loadKey(File in, File privateKeyFile) throws GeneralSecurityException, IOException {
        // read private key to be used to decrypt the AES key
        byte[] encodedKey = new byte[(int)privateKeyFile.length()];
        new FileInputStream(privateKeyFile).read(encodedKey);

        // create private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pk = kf.generatePrivate(privateKeySpec);

        // read AES key
        pkCipher.init(Cipher.DECRYPT_MODE, pk);
        aesKey = new byte[AES_Key_Size/8];
        CipherInputStream is = new CipherInputStream(new FileInputStream(in), pkCipher);
        is.read(aesKey);
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
    }

    /**
     * Encrypts the AES key to a file using an RSA public key
     */
    public void saveKey(File out, File publicKeyFile) throws IOException, GeneralSecurityException {
        // read public key to be used to encrypt the AES key
        byte[] encodedKey = new byte[(int)publicKeyFile.length()];
        new FileInputStream(publicKeyFile).read(encodedKey);

        // create public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pk = kf.generatePublic(publicKeySpec);

        // write AES key
        pkCipher.init(Cipher.ENCRYPT_MODE, pk);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), pkCipher);
        os.write(aesKey);
        os.close();
    }

    /**
     * Encrypts and then copies the contents of a given file.
     */
    public void encrypt(File in, File out) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);

        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aesCipher);

        copy(is, os);

        os.close();
    }

    /**
     * Decrypts and then copies the contents of a given file.
     */
    public void decrypt(File in, File out) throws IOException, InvalidKeyException {
        aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);

        CipherInputStream is = new CipherInputStream(new FileInputStream(in), aesCipher);
        FileOutputStream os = new FileOutputStream(out);

        copy(is, os);

        is.close();
        os.close();
    }

    /**
     * Copies a stream.
     */
    private void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[1024];
        while((i=is.read(b))!=-1) {
            os.write(b, 0, i);
        }
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
       //Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        FileEncryption nmi=   new FileEncryption();
        nmi.makeKey();
        File k=new File("/Users/srikanthmogali/aes2.key");
      nmi.saveKey(k,new File("/Users/srikanthmogali/Downloads/openssl-0.9.8zb/public.der"));//since i created key in one step program.
        nmi.loadKey(k,new File("/Users/srikanthmogali/Downloads/openssl-0.9.8zb/private.der"));
     nmi.encrypt(new File("/Users/srikanthmogali/Desktop/Screen Shot 2014-10-08 at 12.37.19 AM.png"),new File("/Users/srikanthmogali/Desktop/untitled folder/Screen Shot 2014-10-08 at 12.37.19 AM.png"));

nmi.decrypt(new File("/Users/srikanthmogali/Desktop/untitled folder/Screen Shot 2014-10-08 at 12.37.19 AM.png"),new File("/Users/srikanthmogali/Desktop/Screen Shot 2014-10-08 at 12.37.19 AM1.png"));
            Provider p[] = Security.getProviders();
       for(int i=0;i<p.length;i++) System.out.println(p[i]);


    }
}