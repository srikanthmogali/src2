package com.dropbox.core.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class IOUtil
{
    static File temp=null;
    public static final int AES_Key_Size = 256;
    static Cipher pk1Cipher, aes1Cipher;
    static  byte[] aes256Key;
    static SecretKeySpec specAesKey;
    /**
     * Constructor: creates ciphers
     */
    public static  void  instantiateKey() throws GeneralSecurityException {
        // RSA pk cipher
        pk1Cipher = Cipher.getInstance("RSA");
        // AES sk cipher
        aes1Cipher = Cipher.getInstance("AES","SunJCE");
    }

    /**
     * Creates a new AES key
     */
    public static void makeKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(AES_Key_Size);
        SecretKey key = kgen.generateKey();
        aes256Key = key.getEncoded();
        specAesKey = new SecretKeySpec(aes256Key, "AES");
    }

    /**
     * Decrypts an AES key from a file using an RSA private key
     */
    public static void loadKey(File in, File privateKeyFile) throws GeneralSecurityException, IOException {

        // read private key to be used to decrypt the AES key
        byte[] encodedKey = new byte[(int)privateKeyFile.length()];
        new FileInputStream(privateKeyFile).read(encodedKey);
        // create private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pk = kf.generatePrivate(privateKeySpec);
        // read AES key
        pk1Cipher.init(Cipher.DECRYPT_MODE, pk);
        aes256Key = new byte[AES_Key_Size/8];
        CipherInputStream is = new CipherInputStream(new FileInputStream(in), pk1Cipher);
        is.read(aes256Key);
        specAesKey = new SecretKeySpec(aes256Key, "AES");
    }

    /**
     * Encrypts the AES key to a file using an RSA public key
     */
    public static void saveKey(File out, File publicKeyFile) throws IOException, GeneralSecurityException {
        // read public key to be used to encrypt the AES key
        byte[] encodedKey = new byte[(int)publicKeyFile.length()];
        new FileInputStream(publicKeyFile).read(encodedKey);
        // create public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pk = kf.generatePublic(publicKeySpec);
        // write AES key
        pk1Cipher.init(Cipher.ENCRYPT_MODE, pk);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), pk1Cipher);
        os.write(aes256Key);
        os.close();
    }

    /**
     * Encrypts and then copies the contents of a given file.
     */
    public static void encrypt(File in, File out) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        aes1Cipher.init(Cipher.ENCRYPT_MODE, specAesKey);
        FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(new FileOutputStream(out), aes1Cipher);
        copy(is, os);
        os.close();
    }

    public static void encryptStream(InputStream in, OutputStream out) throws IOException, InvalidKeyException, NoSuchAlgorithmException {
        aes1Cipher.init(Cipher.ENCRYPT_MODE, specAesKey);
       // FileInputStream is = new FileInputStream(in);
        CipherOutputStream os = new CipherOutputStream(out, aes1Cipher);
        copy(in, os);
        os.close();
    }

    /**
     * Decrypts and then copies the contents of a given file.
     */
    public static void decrypt(File in, File out) throws IOException, InvalidKeyException {
        aes1Cipher.init(Cipher.DECRYPT_MODE, specAesKey);
        CipherInputStream is = new CipherInputStream(new FileInputStream(in), aes1Cipher);
        FileOutputStream os = new FileOutputStream(out);
        copy(is, os);
        is.close();
        os.close();
    }

    /**
     * Copies a stream.
     */
    private static void copy(InputStream is, OutputStream os) throws IOException {
        int i;
        byte[] b = new byte[16*1024];
        while((i=is.read(b))!=-1) {
            os.write(b, 0, i);
        }
    }

    public static final int DefaultCopyBufferSize = 16 * 1024;

    public static void copyStreamToStream(InputStream in, OutputStream out)

            throws ReadException, WriteException

    {

        copyStreamToStream(in, out, DefaultCopyBufferSize);

    }
    public static void copyStreamToStream1(InputStream in, OutputStream out)

            throws ReadException, WriteException

    {

        copyStreamToStream1(in, out, DefaultCopyBufferSize);

    }

    public static void copyStreamToStream1(InputStream in, OutputStream out, byte[] copyBuffer) throws IOUtil.ReadException, IOUtil.WriteException
    {
        while (true)
        {
            int count;
            try {
                count = in.read(copyBuffer);
            }
            catch (IOException ex) {
                throw new ReadException(ex);
            }

            if (count == -1)
                break;
            try {
                out.write(copyBuffer, 0, count);
            }
            catch (IOException ex) {
                throw new WriteException(ex);
            }
        }
    }


    public static void copyStreamToStream(InputStream in, OutputStream out, byte[] copyBuffer)

            throws ReadException, WriteException
    {
        CipherOutputStream os;
        try {
    if(pk1Cipher ==null && aes1Cipher ==null) {
    instantiateKey();
    }
        FileEncryption nmi;
        temp=new File(System.getProperty("user.home")+"/aes.key");
            if(!temp.exists()) {
                makeKey();
                saveKey(temp,new File(System.getProperty("user.home")+"/Downloads/openssl-0.9.8zb/public.der"));//custom rsa key made by openssl at beginning
                System.out.println("not");
            }
        IOUtil.loadKey(temp,new File("/Users/srikanthmogali/Downloads/openssl-0.9.8zb/private.der"));
        aes1Cipher.init(Cipher.ENCRYPT_MODE, specAesKey);
        // FileInputStream is = new FileInputStream(in);
        os = new CipherOutputStream(out, aes1Cipher);
        encryptStream(in, out);
        }
        catch (IOException e) {
        throw new WriteException(e);
        }
        catch (GeneralSecurityException e) {
            System.out.println(e);
        }

     /*   while (true) {// this is dropbox's default code i modified it with personal code
            int count;
            try {
                count = in.read(copyBuffer);
            }
            catch (IOException ex) {
                throw new ReadException(ex);
            }

            if (count == -1) break;

            try {
                os.write(copyBuffer, 0, count);
            }
            catch (IOException ex) {
                throw new WriteException(ex);
            }
        }*/
    }

    public static void copyStreamToStream(InputStream in, OutputStream out, int copyBufferSize)
            throws ReadException, WriteException
    {
        copyStreamToStream(in, out, new byte[copyBufferSize]);
    }
    public static void copyStreamToStream1(InputStream in, OutputStream out, int copyBufferSize)
            throws ReadException, WriteException
    {
        copyStreamToStream1(in, out, new byte[copyBufferSize]);
    }
    private static final ThreadLocal<byte[]> slurpBuffer = new ThreadLocal<byte[]>() {
        protected byte[] initialValue() { return new byte[4096]; }
    };

    public static byte[] slurp(InputStream in, int byteLimit)
            throws IOException
    {
        if (byteLimit < 0) throw new RuntimeException("'byteLimit' must be non-negative: " + byteLimit);

        byte[] copyBuffer = slurpBuffer.get();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        copyStreamToStream(in, baos, copyBuffer);
        return baos.toByteArray();
    }

    public void copyFileToStream(File fin, OutputStream out)
            throws ReadException, WriteException
    {
        copyFileToStream(fin, out, DefaultCopyBufferSize);
    }

    public void copyFileToStream(File fin, OutputStream out, int copyBufferSize)
            throws ReadException, WriteException
    {
        FileInputStream in;
        try {
            in = new FileInputStream(fin);
        }
        catch (IOException ex) {
            throw new ReadException(ex);
        }

        try {
            copyStreamToStream(in, out, copyBufferSize);
        }
        finally {
            closeInput(in);
        }
    }

    public void copyStreamToFile(InputStream in, File fout)
            throws ReadException, WriteException
    {
        copyStreamToFile(in, fout, DefaultCopyBufferSize);
    }

    public void copyStreamToFile(InputStream in, File fout, int copyBufferSize)
            throws ReadException, WriteException
    {
        FileOutputStream out;
        try {
            out = new FileOutputStream(fout);
        }
        catch (IOException ex) {
            throw new WriteException(ex);
        }

        try {
            copyStreamToStream(in, out, copyBufferSize);
        }
        finally {
            try { out.close(); } catch (IOException ex) {
                //noinspection ThrowFromFinallyBlock
                throw new WriteException(ex);
            }
        }
    }

    /**
     * Closes the given input stream and ignores the IOException.
     */
    public static void closeInput(InputStream in)
    {
        try {
            in.close();
        }
        catch (IOException ex) {
            // Ignore.  We're done reading from it so we don't care if there are input errors.
        }
    }

    /**
     * Closes the given Reader and ignores the IOException.
     */
    public static void closeInput(Reader in)
    {
        try {
            in.close();
        }
        catch (IOException ex) {
            // Ignore.  We're done reading from it so we don't care if there are input errors.
        }
    }

    public static abstract class WrappedException extends IOException
    {
        public static final long serialVersionUID = 0;

        public final IOException underlying;

        public WrappedException(String message, IOException underlying)
        {
            super(message + ": " + underlying.getMessage(), underlying);
            this.underlying = underlying;
        }

        public WrappedException(IOException underlying)
        {
            super(underlying);
            this.underlying = underlying;
        }

        @Override
        public String getMessage()
        {
            String m = underlying.getMessage();
            if (m == null) return "";
            else return m;
        }

        @Override
        public IOException getCause()
        {
            return underlying;
        }
    }

    public static final class ReadException extends WrappedException
    {
        public ReadException(String message, IOException underlying)
        {
            super(message, underlying);
        }

        public ReadException(IOException underlying)
        {
            super(underlying);
        }

        public static final long serialVersionUID = 0;
    }

    public static final class WriteException extends WrappedException
    {
        public WriteException(String message, IOException underlying)
        {
            super(message, underlying);
        }

        public WriteException(IOException underlying)
        {
            super(underlying);
        }

        public static final long serialVersionUID = 0;
    }

    public static final InputStream EmptyInputStream = new InputStream()
    {
        public int read() { return -1; }
        public int read(byte[] data) { return -1; }
        public int read(byte[] data, int off, int len) { return -1; }
    };

    public static final OutputStream BlackHoleOutputStream = new OutputStream()
    {
        public void write(int b) {}
        public void write(byte[] data) {}
        public void write(byte[] data, int off, int len) {}
    };
}
