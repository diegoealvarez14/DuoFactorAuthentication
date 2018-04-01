package com.example.diegoalvarez.duoauthentication;
import android.nfc.Tag;
import android.util.Log;

import junit.framework.*;

import static junit.framework.Assert.assertTrue;

public class EncryptionUnitTest extends Encryption {
    protected String password1;

    protected void setPassword1(){
        password1 = "Hello World";
    }

    @org.junit.Test
    public void testPassword1(){
        byte[] result;

        try {
            result = encryptText(password1);
            Log.d("Password Test Result", result.toString());
            assertTrue(!result.toString().equals(password1));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
