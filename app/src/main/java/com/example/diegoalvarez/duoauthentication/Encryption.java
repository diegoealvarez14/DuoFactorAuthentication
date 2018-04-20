package com.example.diegoalvarez.duoauthentication;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
/**
 *
 * Created by Tony Nguyen and Hayden Conley on 3/21/2018.
 *
 * This class contains methods to encrypt and decrypt strings
 *
 * API information gathered from https://docs.oracle.com/javase/7/docs/api/javax/crypto/Cipher.html
 * and https://docs.oracle.com/javase/7/docs/api/javax/crypto/KeyGenerator.html
 *
 * https://developer.android.com/training/articles/keystore.html for keystore

 */

public class Encryption extends AppCompatActivity {

    // The type of encryption for use in the app
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    // String for fetching of instance of android key store
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    // Alias for key store retrieval
    private static final String KEY_ALIAS = "DuoFactorAuthenticationKey";

    // The initialization vector
    public byte[] iv;

    // Android Keystore where encryption keys are stored for use in encryption and decryption
    private KeyStore keyStore;

    public Encryption() {
    }

    /**
     * Creates an instance of AES and uses it to encrypt a string.
     *
     * @param sensitiveData the string you want to encrypt
     * @return
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws InvalidAlgorithmParameterException
     * @throws SignatureException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     */
    public byte[] encryptAES(final String sensitiveData)
            throws Exception {
        //Makes sure that if no key exists yet it will create one with specified alias and store in KeyStore
        generateKey();

        // Get an instance of the cipher according to the algorithm you want to use
        final Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Set the cipher to Encrypt mode and call method
        cipher.init(Cipher.ENCRYPT_MODE, getKey());

        // Generate an initialization vector to randomize encryption and avoid repeated occurrences
        iv = cipher.getIV();

        // Convert String input to a sequence of bytes, then encrypt the data according to the
        // encryption algorithm specified in the cipher
        byte[] encryptedData = cipher.doFinal(sensitiveData.getBytes("UTF-8"));

        // Append the IV to the end of the byte array with the encrypted bytes and return the resulting array
        return appendIVToData(encryptedData, iv);
    }

    /**
     * Creates the AES key to encrypt data and stores the key in the keystore under the given alias
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     */
    @NonNull
    private void generateKey() throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException, IOException, CertificateException {

        //Retrieve the current KeyStore
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);

        //If the current alias is not recognized by the KeyStore it generates a new key and stores it in the keystore.
        if (!keyStore.containsAlias(KEY_ALIAS)) {

            // Get an instance of KeyGenerator for the specified algorithm
            final KeyGenerator keyGen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

            keyGen.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(128) // Variable to change for using regular AES, AES_128, or AES_256
                    .build());

            // Generate a key
            Key k = keyGen.generateKey();

            // Store the key in the keystore
            keyStore.setKeyEntry(KEY_ALIAS, k, null, null);
        }
    }

    /**
     * Returns the encryption key from the Keystore under the alias
     *
     * @return the encryption key
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    private Key getKey() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return keyStore.getKey(KEY_ALIAS, null);
    }

    /**
     * Adds the IV to the end of the encrypted byte array for use in decrypting
     *
     * @param encryptedData - the encrypted string
     * @param iv            - initialization vector used to encrypt the string
     * @return - the encrypted data and iv combined into one byte array
     * @throws Exception
     */
    private static byte[] appendIVToData(byte[] encryptedData, byte[] iv) throws Exception {

        // Stream to combine the two arrays
        ByteArrayOutputStream combinedBytes = new ByteArrayOutputStream();

        //Write the encrypted data and the IV to the Stream
        combinedBytes.write(encryptedData);
        combinedBytes.write(iv);
        combinedBytes.close();

        // Convert stream to byte array and return
        return combinedBytes.toByteArray();
    }

    /**
     * Decrypts a string of encrypted data
     *
     * @param encryptedData the byte array with the encrypted string and IV at the end
     * @return the decrypted string
     * @throws UnrecoverableEntryException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     * @throws NoSuchProviderException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws IOException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException
     * @throws InvalidAlgorithmParameterException
     * @throws CertificateException
     */
    public String decryptAES(final byte[] encryptedData)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, CertificateException {

        // Return the current keystore
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);

        // Get an instance of the cipher according to your encryption algorithm
        final Cipher cipher = Cipher.getInstance(ALGORITHM);

        // Separate the encrypted data from the IV
        //Create a stream of the size of the IV
        ByteArrayOutputStream encryptedIV = new ByteArrayOutputStream(12);

        //Write the last 12 bytes from the byte array to the stream
        encryptedIV.write(encryptedData, encryptedData.length - 12, 12);

        //Convert the stream to a byte array
        iv = encryptedIV.toByteArray();

        //Create a stream to hold the encrypted Data
        ByteArrayOutputStream encryptedDataHolder = new ByteArrayOutputStream();

        //Write everything but the last 12 bytes of the byte array to the stream
        encryptedDataHolder.write(encryptedData, 0, encryptedData.length - 12);

        //Convert the stream to a byte array for decryption
        byte[] encryptedDataBytes = encryptedDataHolder.toByteArray();

        // Initialize the cipher to decrypt the data with the Key and algorithm parameters
        cipher.init(Cipher.DECRYPT_MODE, getKey(), new GCMParameterSpec(128, iv));
        encryptedDataHolder.close();
        encryptedIV.close();

        //Perform the decryption and return the decrypted data
        return new String(cipher.doFinal(encryptedDataBytes), "UTF-8");
    }
}