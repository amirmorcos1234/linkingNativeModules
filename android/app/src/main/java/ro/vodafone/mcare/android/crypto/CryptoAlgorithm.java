package ro.vodafone.mcare.android.crypto;

/**
 * Defines the algorithm actions for crypting and decrypting byte arrays.
 *
 */
public interface CryptoAlgorithm {

    /**
     * Encrypts an array of bytes. If encryption fails, the result will be <code>null</code>.
     *
     * @param decrypted the byte array to encrypt
     * @return the encrypted array
     * @throws CryptoException if encryption fails
     */
    byte[] encrypt(byte[] decrypted) throws CryptoException;

    /**
     * Decrypts an array of bytes.. If decryption fails, the result will be <code>null</code>.
     *
     * @param decrypted the byte array to encrypt
     * @return the encrypted array
     * @throws CryptoException if decryption fails
     */
    byte[] decrypt(byte[] encrypted) throws CryptoException;
}
