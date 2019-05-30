package ro.vodafone.mcare.android.crypto;

import java.io.UnsupportedEncodingException;

/**
 * Class handling the user's password encryption.
 *
 *
 */
public class EncryptionManager {

    public static final String ENCRYPTION_KEY = "!p3\\8r^S.<MJh<?M";
    public static final String ENCODING = "UTF-8";
    private AesAlgorithm cryptoAlgorithm;
    private static EncryptionManager cryptoService;

    private EncryptionManager() {
        cryptoAlgorithm = new AesAlgorithm();
    }

    public static EncryptionManager getInstance() {
        if (cryptoService == null) {
            cryptoService = new EncryptionManager();
        }
        return cryptoService;
    }

    /**
     * Sets the key that will be used for data encryption and decryption.
     *
     * @param newKey the new key to set as a {@link String}.
     */
    private void setKey(String newKey) {
        if (newKey != null) {
            cryptoAlgorithm.setKey(convertStringTo32ByteArray(newKey));
        }
    }

    /**
     * Converts a {@link String} object to an array of 32 bytes.
     *
     * @param str the {@link String} to convert.
     * @return the resulting byte array.
     */
    private byte[] convertStringTo32ByteArray(String str) {
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

    /**
     * Encrypts the given string.
     *
     * @param input the {@link String} data to encrypt.
     * @return an array of bytes representing the encrypted data.
     * @throws CryptoException if an error occurs during encryption operation.
     */
    public byte[] encrypt(String input) throws CryptoException {
        setKey(EncryptionManager.ENCRYPTION_KEY);
        byte[] bytes;
        try {
            bytes = input.getBytes(ENCODING);
            byte[] encryptedData = cryptoAlgorithm.encrypt(bytes);
            return encryptedData;
        }
        catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }

    }

    /**
     * Decrypts the given {@link String}.
     *
     * @param input an array of bytes representing the data to be encrypted.
     * @return an array of bytes representing the decrypted data.
     * @throws CryptoException if an error occurs during decryption operation.
     */
    public byte[] decrypt(byte[] input) throws CryptoException {
        cryptoService.setKey(EncryptionManager.ENCRYPTION_KEY);
        byte[] decryptedData = cryptoAlgorithm.decrypt(input);
        return decryptedData;
    }
}
