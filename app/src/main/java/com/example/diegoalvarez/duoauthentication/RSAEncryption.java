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


/**
 * Created by Hayden Conley on 4/4/18.
 *
 * This class implements the encryption and decryption of the RSA Algorithm with a fixed key length of 2048.
 */

public class RSAEncryption extends AppCompatActivity {

 //Algorithm to use
    public static final String ALGORITHM = "RSA/ECB/OAEPWithSHA-1AndMGF1Padding";

    //Get instance of Android KeyStore
    public static final String ANDROID_KEY_STORE = "AndroidKeyStore";

    //Alias for key retrieval
    private static final String KEY_ALIAS = "DuoAuthenticationRSAKey";

    //Array to hold encrypted bytes of data
    private byte[] encrypted_data;

    //Initialization vector
    public byte[] iv;

    //Place to store encryption keys
    private KeyStore keys;

    public RSAEncryption() {
    }

    /**
     * Creates a cipher and encrypts the string passed according to that cipher.
     *
     * @param plaintext - the string you want to encrypt
     * @return - byte array of encrypted text
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
    public byte[] encryptRSA(final String plaintext)
        throws Exception {
            // Create new private key for alias if it doesn't exist already
            generatePrivateKey();

            //Initiate new rsa cipher
            final Cipher rsa = Cipher.getInstance(ALGORITHM);

            // Set rsa cipher to encrypt mode
            rsa.init(Cipher.ENCRYPT_MODE, getPrivateKey());

            //get the iv
            iv = rsa.getIV();

            // Encrypt data
            byte[] rsaEncryptedData = rsa.doFinal(plaintext.getBytes("UTF-8"));


            //Return fully encrypted data with iv appended to ciphertext
            return appendIvToData(rsaEncryptedData, iv);

    }


    /**
     * Creates the RSA key to encrypt data and stores the key in the keystore under the alias
     *
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     * @throws InvalidAlgorithmParameterException
     * @throws KeyStoreException
     * @throws IOException
     * @throws CertificateException
     */

    @NonNull
    private void generatePrivateKey() throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException, KeyStoreException, IOException, CertificateException {

        //Retrieve keystore and load
         keys = KeyStore.getInstance(ANDROID_KEY_STORE);
         keys.load(null);

         //If current alias not found in keystore, make one.
        if(!keys.containsAlias(KEY_ALIAS)){
            final KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEY_STORE);

            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_ECB).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP).setKeySize(2048).build());

            Key key = keyGenerator.generateKey();

            keys.setKeyEntry(KEY_ALIAS, key, null, null);
        }
    }

    /**
     * Returns the encryption key from the Keystore under the alias
     *
     * @return - the private encryption key
     * @throws UnrecoverableKeyException
     * @throws NoSuchAlgorithmException
     * @throws KeyStoreException
     */
    private Key getPrivateKey() throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
        return keys.getKey(KEY_ALIAS, null);
    }

    /**
     * Returns the initialization vector for the encryption class
     *
     * @return iv - the initialization vector
     */
    byte[] getInitVector() {
        return iv;
    }


    /**
     * Adds the initialization vector to the end of the encrypted byte array for use in decrypting
     *
     * @param encryptedData - the encrypted string
     * @param iv            - initialization vector used to encrypt the string
     * @return - the encrypted data and iv combined into one byte array
     * @throws Exception
     */
    private byte[] appendIvToData(byte[] encryptedData, byte[] iv) throws Exception {
        //Open new byte output stream
        ByteArrayOutputStream appendedData = new ByteArrayOutputStream( );

        //Write encrypted data and iv to output stream
        appendedData.write( encryptedData );
        appendedData.write( iv );

        //Close stream
        appendedData.close();


        // Return byte array
        return appendedData.toByteArray( );
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
    public String decryptAES(final byte[] encryptedDataWithIV)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException, CertificateException {

        // Return the current keystore
        keys = KeyStore.getInstance(ANDROID_KEY_STORE);
        keys.load(null);

        // Get an instance of the cipher according to your encryption algorithm
        final Cipher rsa = Cipher.getInstance(ALGORITHM);

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
        rsa.init(Cipher.DECRYPT_MODE, getPrivateKey());

        encryptedDataHolder.close();
        encryptedIV.close();
        //Perform the decryption and return the decrypted data
        return new String(rsa.doFinal(encryptedData), "UTF-8");
    }
}
