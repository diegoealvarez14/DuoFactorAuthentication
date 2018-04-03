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
 * Created by Tony Nguyen on 3/21/2018.
 *
 * This class contains methods to encrypt and decrypt strings,
 * generates and retrieves the key to encrypt with AES 256.
 */

public class Encryption extends AppCompatActivity {

    // The type of encryption you want to use to encrypt data
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    // Used for getting an instance of the android KeyStore
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    // The name of the alias used to retrieve the encryption key
    private static final String KEY_ALIAS = "DuoFactorAuthenticationKey";

    // Array to hold the encrypted data in bytes
    private byte[] encryption;

    // Initiliazation Vector
    public byte[] iv;

    // Android Keystore where encryption keys are stored
    private KeyStore keyStore;

    public Encryption() {
    }

    /**
     * Creates a cipher and encrypts the string passed according to that cipher.
     *
     * @param textToEncrypt - the string you want to encrypt
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
    public byte[] encryptText(final String textToEncrypt)
            throws Exception {

        //Makes sure that if no key exists yet it will create one with specified alias and store in KeyStore
        generateSecretKey();

        // Get an instance of the cipher according to the algorithm you want to use
        final Cipher cipher = Cipher.getInstance(ALGORITHM);


        //set the cipher to Encrypt mode and call method
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());

        //generate an initialization vector to randomize encryption and avoid repeated occurrences
        iv = cipher.getIV();

        // Convert String input to a sequence of bytes, then encrypt the data according to the
        // encryption algorithm specified in the cipher
        byte[] encryptedData = cipher.doFinal(textToEncrypt.getBytes("UTF-8"));

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
    private void generateSecretKey() throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException, IOException, CertificateException {

        //Retrieve the current KeyStore
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);

        //If the current alias is not recognized by the KeyStore it generates a new key and stores it in the keystore.
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            // Get an instance of KeyGenerator for the specified algorithm
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(128) // Variable to change for using regular AES, AES_128, or AES_256
                    .build());

            // Generate a key
            Key k = keyGenerator.generateKey();

            // Store the key in the keystore
            keyStore.setKeyEntry(KEY_ALIAS, k, null, null);
        }
    }

    /**
     * Returns the encryption key from the Keystore under the alias
     *
     * @return - the encryption key
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    private Key getSecretKey() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return keyStore.getKey(KEY_ALIAS, null);
    }

    /**
     * Returns the initialization vector for the encryption class
     *
     * @return - iv - the initialization vector
     */
    byte[] getIv() {
        return iv;
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

        // Convert stream to byte array and return
        return combinedBytes.toByteArray();
    }


    /**
     * Decrypts a string of encrypted data
     *
     * @param encryptedDataWithIV -  the byte array with the encrypted string and IV at the end
     * @return - the decrypted string
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
    public String decryptText(final byte[] encryptedDataWithIV)
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
        encryptedIV.write(encryptedDataWithIV, encryptedDataWithIV.length - 12, 12);
        //Convert the stream to a byte array
        iv = encryptedIV.toByteArray();

        //Create a stream to hold the encrypted Data
        ByteArrayOutputStream encryptedDataHolder = new ByteArrayOutputStream();
        //Write everything but the last 12 bytes of the byte array to the stream
        encryptedDataHolder.write(encryptedDataWithIV, 0, encryptedDataWithIV.length - 12);
        //Convert the stream to a byte array for decryption
        byte[] encryptedData = encryptedDataHolder.toByteArray();

        // Initialize the cipher to decrypt the data with the Key and algorithm parameters
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), new GCMParameterSpec(128, iv));

        //Perform the decryption and return the decrypted data
        return new String(cipher.doFinal(encryptedData), "UTF-8");
    }
}
