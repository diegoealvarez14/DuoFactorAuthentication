package com.example.diegoalvarez.duoauthentication;

/**
 * Created by Tony Nguyen on 3/21/2018.
 * Source: https://www.androidauthority.com/how-to-add-fingerprint-authentication-to-your-android-app-747304/
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.Manifest;
import android.os.Build;
import android.os.CancellationSignal;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

<<<<<<< HEAD

    private CancellationSignal cancel;
=======
    private CancellationSignal cancellationSignal;
>>>>>>> 53ec4f83b5878d0edd85767c2d12db82169c0ecb
    private Context context;

    public FingerprintHandler(Context mContext) {
        context = mContext;
    }


    // Start fingerprint authentication.
    public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

        cancel = new CancellationSignal();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        manager.authenticate(cryptoObject, cancel, 0, this, null);
    }

    // Used for error when trying to use fingerprint.
    @Override
    public void onAuthenticationError(int errMsgId, CharSequence errString) {

        Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
    }

    // Authentication failed
    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
        Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
    }

<<<<<<< HEAD
    // Open password manager after authenticated.
=======
>>>>>>> 53ec4f83b5878d0edd85767c2d12db82169c0ecb
    @Override
    public void onAuthenticationSucceeded(
            FingerprintManager.AuthenticationResult result) {

        Toast.makeText(context, "Success!", Toast.LENGTH_LONG).show();
        context.startActivity(new Intent(context, PasswordManager.class));
    }

}