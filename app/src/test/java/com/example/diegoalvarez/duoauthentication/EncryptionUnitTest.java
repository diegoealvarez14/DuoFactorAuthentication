package com.example.diegoalvarez.duoauthentication;
import android.nfc.Tag;
import android.util.Log;

import junit.framework.*;

import static junit.framework.Assert.assertTrue;

public class EncryptionUnitTest extends Encryption{
    protected String password1 = "0123456789abcdef";

    @org.junit.Test
    public void testPassword1() throws Exception {
        byte[] result;
        result = encryptText(password1);
        Log.d("Password Test Result", result.toString());
        assertTrue(!result.equals(password1));



    }
}
