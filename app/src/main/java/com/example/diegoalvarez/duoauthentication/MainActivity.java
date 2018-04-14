package com.example.diegoalvarez.duoauthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.app.KeyguardManager;

import android.hardware.fingerprint.FingerprintManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.KeyStore;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private TextView textViewSignin;
    TextView passStrength;
    ProgressBar progressBar;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    // Declare a string variable for the key we’re going to use in our fingerprint authentication
    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView textView;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null ){

            finish();
            startActivity(new Intent(getApplicationContext(), PasswordManager.class));


        }
        progressDialog = new ProgressDialog(this);
        buttonRegister = (Button) findViewById(R.id.buttonRegister);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        textViewSignin = (TextView) findViewById(R.id.textViewSignIn);
        passStrength = (TextView) findViewById(R.id.passStrength);
        buttonRegister.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String password = editTextPassword.getText().toString().trim();
                char ch;
                boolean hasCapital = false;
                boolean hasLower = false;
                boolean hasSpecial = false;
                boolean hasNumber = false;
                Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");
                Matcher matcher = pattern.matcher(password);

                if (matcher.matches()) {
                    editTextPassword.setError("Password must contain a special character");
                }
                for (int i=0; i<password.length(); i++) {
                    ch = password.charAt(i);
                    if (Character.isDigit(ch)) {
                        hasNumber=true;
                    } else if (Character.isUpperCase(ch)) {
                        hasCapital=true;
                    } else if (Character.isLowerCase(ch)) {
                        hasLower=true;
                    }
                }

                if (editTextPassword.getText().toString().length()<=7){
                    editTextPassword.setError("Password must be at least 8 characters long");
                }
                if (!hasLower) {
                    editTextPassword.setError("Password must contain a lower case letter");
                }
                if (!hasCapital) {
                    editTextPassword.setError("Password must contain a capital letter");
                }
                if (!hasNumber) {
                    editTextPassword.setError("Password must contain a number");
                }
                else {
                    passwordCalculation();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    protected void passwordCalculation(){
        String password = editTextPassword.getText().toString();

        int length = 0, uppercase = 0, lowercase = 0, digits = 0, specialChar = 0, requirements = 0;

        int justLetters = 0, justNumbers = 0, concurrentUpCase = 0, concurrentLowerCase = 0;

        length = password.length();
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i)))
                uppercase++;
            else if (Character.isLowerCase(password.charAt(i)))
                lowercase++;
            else if (Character.isDigit(password.charAt(i)))
                digits++;

            specialChar = length - uppercase - lowercase - digits;

        }
        for (int k = 0; k < password.length(); k++) {
            if (Character.isUpperCase(password.charAt(k))) {
                k++;
                if (k < password.length()) {
                    if (Character.isUpperCase(password.charAt(k))) {
                        concurrentUpCase++;
                        k--;
                    }
                }
            }
        }

        for (int l = 0; l < password.length(); l++) {
            if (Character.isLowerCase(password.charAt(l))) {
                l++;
                if (l < password.length()) {
                    if (Character.isLowerCase(password.charAt(l))) {
                        concurrentLowerCase++;
                        l--;
                    }
                }
            }
        }

        if (length > 8) {
            requirements++;
        }

        if (uppercase > 1) {
            requirements++;
        }

        if (lowercase > 1) {
            requirements++;
        }

        if (digits > 1) {
            requirements++;
        }

        if (specialChar > 1) {
            requirements++;
        }


        if (digits == 0 && specialChar == 0) {
            justLetters = 1;
        }

        if (lowercase == 0 && uppercase == 0 && specialChar == 0) {
            justNumbers = 1;
        }

        int Total = (length * 4) + ((length - uppercase) * 2)
                + ((length - lowercase) * 2) + (digits * 4) + (specialChar * 6)
                + (requirements * 2) - (justLetters * length*2)
                - (justNumbers * length*6) - (concurrentUpCase * 2) - (concurrentLowerCase * 2);


        if(Total<50){
            progressBar.setProgress(Total-20);
            progressBar.getProgressDrawable().setColorFilter(Color.rgb(255,45,0), PorterDuff.Mode.SRC_IN);
            passStrength.setTextColor(Color.rgb(255,45,0));
            passStrength.setText("Weak");

        }
        else if (Total>=50 && Total <65)
        {
            progressBar.setProgress(Total-20);
            progressBar.getProgressDrawable().setColorFilter(Color.rgb(252,158,2), PorterDuff.Mode.SRC_IN);
            passStrength.setTextColor(Color.rgb(252,158,2));
            passStrength.setText("Fair");
        }

        else if (Total>=65 && Total <85)
        {
            progressBar.setProgress(Total-20);
            progressBar.getProgressDrawable().setColorFilter(Color.rgb(227,222,67), PorterDuff.Mode.SRC_IN);
            passStrength.setTextColor(Color.rgb(227,222,67));
            passStrength.setText("Good");
        }
        else if (Total>=85 && Total <105)
        {
            progressBar.setProgress(Total-20);
            progressBar.getProgressDrawable().setColorFilter(Color.rgb(152,244,96), PorterDuff.Mode.SRC_IN);
            passStrength.setTextColor(Color.rgb(152,244,96));
            passStrength.setText("Strong");
        }

        else if (Total>=105)
        {
            progressBar.setProgress(Total-20);
            progressBar.getProgressDrawable().setColorFilter(Color.rgb(2,254,21), PorterDuff.Mode.SRC_IN);
            passStrength.setTextColor(Color.rgb(2,254,21));
            passStrength.setText("Very Strong");

        } if (Total==0) {
            progressBar.setProgress(0);
        }

    }
    private void registerUser() {


        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            //email is null
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //password is null
            Toast.makeText(this, "Please enter password ", Toast.LENGTH_SHORT).show();
            return;

        }

        progressDialog.setMessage("Register User...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    /*
                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                     */

                    finish();
                    startActivity(new Intent(getApplicationContext(), PasswordManager.class));
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Failed to register... try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view == buttonRegister) {
            registerUser();
        }
        if (view==textViewSignin) {
            startActivity(new Intent(this, SigninActivity.class));
        }


    }
}
