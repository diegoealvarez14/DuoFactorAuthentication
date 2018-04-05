package com.example.diegoalvarez.duoauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.util.Base64;

public class AddItem extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private EditText editTextApp, editTextUserName, editTextPassword;
    private Button buttonSave;
private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         user = firebaseAuth.getInstance().getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        editTextApp = (EditText) findViewById(R.id.appName);
        editTextUserName = (EditText) findViewById(R.id.userName);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonSave = (Button) findViewById(R.id.buttonSendToDB);


        buttonSave.setOnClickListener(this);


        /*
        final Button button = findViewById(R.id.button6);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(AddItem.this, "Successfully Sent To Database", Toast.LENGTH_SHORT).show();
            }
        });
        */
    }


    private void saveUserInformation() throws Exception {
        Encryption encryption = new Encryption();
        RSAEncryption rsa_encryption = new RSAEncryption();
        String app = editTextApp.getText().toString().trim();
        String userName = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //byte[] encryptedPassword = encryption.encryptAES(password);
        byte[] encrypted_data = rsa_encryption.encryptRSA(password);
        //Use this to decrypt the password. Had to take some extra steps because of converting: byte[] -> String -> byte[] complications

        //Could turn this to a method after retrieving data part is done.
        //AES byte[] decrypt = Base64.decode(new String(Base64.encode(encryptedPassword, 1)), 1);
        byte[] decrypt = Base64.decode(new String(Base64.encode(encrypted_data, 1)), 1);
        //String decryptedPassword = encryption.decryptAES(decrypt);
        //String decryptedPassword = encryption.decryptAES(decrypt);
        //Log.d(this.getLocalClassName(), "saveUserInformation() -> Decrypted Password1: " + decryptedPassword);

        //UserInput userInput = new UserInput(app, userName, encryptedPassword.toString());

        //databaseReference.child(user.getUid()).setValue(userInput);

        Toast.makeText(this, "Information Sent to Database...", Toast.LENGTH_LONG).show();
        startActivity(new Intent(this, PasswordManager.class));
    }

    @Override
    public void onClick(View view) {
        if (view == buttonSave) {
            try {
                saveUserInformation();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

