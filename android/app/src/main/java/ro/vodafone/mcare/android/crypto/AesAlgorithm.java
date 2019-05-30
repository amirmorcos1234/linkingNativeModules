package ro.vodafone.mcare.android.crypto;


import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Made encryption and decryption of using AES algorithm with 128 bits. If the key is <code>null</code>, will be used o
 * generated one.
 *
 */
public class AesAlgorithm implements CryptoAlgorithm {

    /**
     * The key used for encryption and decryption.
     */
    private byte[] key;

    /**
     * String for initializing the Chiper object with algorithm/mode/padding
     */
    private static final String ALGORITHM_INIT = "AES/CBC/NoPadding";

    /**
     * Create an 16-byte initialization vector.
     */
    private byte[] initializationVector = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00 };

    private static final String ENCODING = "UTF8";

    /**
     * Encrypts an array of bytes using a 128b key. If encryption fails, the result will be <code>null</code>.
     *
     * @param decrypted the byte array to encrypt
     * @return the encrypted array
     *
     */
    public byte[] encrypt(byte[] decrypted) throws CryptoException {
        try {
            byte[] encodedKey = getKey();
            SecretKeySpec skeySpec = new SecretKeySpec(encodedKey, "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(initializationVector);

            Cipher cipher = Cipher.getInstance(ALGORITHM_INIT);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, paramSpec);

            int blockSize = cipher.getBlockSize();
            int diffSize = decrypted.length % blockSize;
            if (diffSize != 0) {
                diffSize = blockSize - diffSize;
                byte[] oldDecrypted = decrypted;
                decrypted = new byte[decrypted.length + diffSize];
                System.arraycopy(oldDecrypted, 0, decrypted, 0, oldDecrypted.length);
                for (int i = 0; i < diffSize; i++) {
                    decrypted[i + oldDecrypted.length] = " ".getBytes()[0];
                }
            }
            return cipher.doFinal(decrypted);
        }
        catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        catch (NoSuchPaddingException e) {
            throw new CryptoException(e);
        }
        catch (InvalidKeyException e) {
            throw new CryptoException(e);
        }
        catch (IllegalBlockSizeException e) {
            throw new CryptoException(e);
        }
        catch (BadPaddingException e) {
            throw new CryptoException(e);
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Decrypts an array of bytes using a 128b key. If decryption fails, the result will be <code>null</code>.
     *
     * @param decrypted the byte array to encrypt
     * @return the encrypted array
     *
     */
    public byte[] decrypt(byte[] encrypted) throws CryptoException {
        try {
            byte[] encodedKey = getKey();
            SecretKeySpec skeySpec = new SecretKeySpec(encodedKey, "AES");
            AlgorithmParameterSpec paramSpec = new IvParameterSpec(initializationVector);

            Cipher cipher = Cipher.getInstance(ALGORITHM_INIT);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, paramSpec);

            return cipher.doFinal(encrypted);
        }
        catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
        catch (NoSuchPaddingException e) {
            throw new CryptoException(e);
        }
        catch (InvalidKeyException e) {
            throw new CryptoException(e);
        }
        catch (IllegalBlockSizeException e) {
            throw new CryptoException(e);
        }
        catch (BadPaddingException e) {
            throw new CryptoException(e);
        }
        catch (InvalidAlgorithmParameterException e) {
            throw new CryptoException(e);
        }
    }

    /**
     * Gets the key used for encryption and decryption. If the key is <code>null</code>, will be generated one.
     *
     * @return the key used for encryption and decryption
     * @throws NoSuchAlgorithmException if AES algorithm cannot be loaded
     */
    public byte[] getKey() throws NoSuchAlgorithmException {
        if (key == null) {
            // Get the KeyGenerator
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128);
            key = kgen.generateKey().getEncoded();
        }
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key the key to set
     */
    public void setKey(byte[] key) {
        this.key = key;
    }

    /**
     * Sets the key as a string text.
     *
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key.getBytes();
    }

    /**
     * Converts a {@link String} object to an array of 32 bytes.
     *
     * @param str the {@link String} to convert.
     * @return the resulting byte array.
     */
    public byte[] convertStringTo32ByteArray(String str) {
        byte[] decrypted;
        try {
            decrypted = str.getBytes(ENCODING);
            int blockSize = 32;
            int diffSize = decrypted.length % blockSize;
            if (diffSize != 0) {
                diffSize = blockSize - diffSize;
                byte[] oldDecrypted = decrypted;
                decrypted = new byte[decrypted.length + diffSize];
                System.arraycopy(oldDecrypted, 0, decrypted, 0, oldDecrypted.length);
                for (int i = 0; i < diffSize; i++) {
                    decrypted[i + oldDecrypted.length] = " ".getBytes()[0];
                }
            }
            return decrypted;
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

}
