package com.example.diegoalvarez.duoauthentication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class EmailVerification extends AppCompatActivity implements View.OnClickListener{

    private Button buttonSignIn;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonRefresh;

    private FirebaseAuth firebaseAuth;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        firebaseAuth = FirebaseAuth.getInstance();


        progressDialog = new ProgressDialog(this);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonSignIn = (Button) findViewById(R.id.buttonSignin);
        buttonRefresh = (Button) findViewById(R.id.buttonRefresh);

        buttonSignIn.setOnClickListener(this);
        buttonRefresh.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        userLogin();
                    }
                });
            }
        });



    }

    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean emailVerified = user.isEmailVerified();

        if (TextUtils.isEmpty(email)) {
            //email is null
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            //password is null
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }
if (emailVerified) {

    progressDialog.setMessage("Signing in...");
    progressDialog.show();

    firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            progressDialog.dismiss();

            if (task.isSuccessful()) {
                finish();
                startActivity(new Intent(getApplicationContext(), FingerprintActivity.class));

            }
        }
    });
} else {
    Toast.makeText(this, "Please verify your email... Status: " + emailVerified, Toast.LENGTH_SHORT).show();

}



    }

    @Override
    public void onClick(View view) {
        if (view==buttonSignIn) {
            userLogin();
            //startActivity(new Intent(this, PasswordManager.class));

        }
    }
}
