package com.example.diegoalvarez.duoauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


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

        databaseReference  = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getInstance().getCurrentUser();

       // databaseReference = FirebaseDatabase.getInstance().getReference();
        editTextApp = (EditText) findViewById(R.id.appName);
        editTextUserName = (EditText) findViewById(R.id.userName);
        editTextPassword = (EditText) findViewById(R.id.password);
        buttonSave = (Button) findViewById(R.id.buttonSendToDB);

        buttonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
    }


    private void saveUserInformation() throws Exception {
        //Instantiate new instance of class
        Encryption encryption = new Encryption();
        RSAEncryption rsa_encryption = new RSAEncryption();
        AESHomeEncryption aes_home = new AESHomeEncryption();

        String app = editTextApp.getText().toString().trim();
        String userName = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //AES Key generated around passphrase "duoauth1234"
        String keyString = "519E4F26F38B844CC81304B6A92D2859";
        char [] key = keyString.toCharArray();

        /**
         * Encrypt the password
         */
        //byte[] encryptedPassword = encryption.encryptAES(password);
        //byte[] encrypted_data = rsa_encryption.encryptRSA(password);
        byte[] encryptedPass = aes_home.AES_Encrypt(password, key);

        /**
         * Decrypt the encrypted text in the database
         */
        //Use this to decrypt the password. Had to take some extra steps because of converting: byte[] -> String -> byte[] complications

        //Could turn this to a method after retrieving data part is done.
        //AES byte[] decrypt = Base64.decode(new String(Base64.encode(encryptedPassword, 1)), 1);
        //po  byte[] decrypt = Base64.decode(new String(Base64.encode(encryptedPass, 1)), 1);
        //String decryptedPassword = encryption.decryptAES(decrypt);
        //String decryptedPassword = encryption.decryptAES(decrypt);
        //Log.d(this.getLocalClassName(), "saveUserInformation() -> Decrypted Password1: " + decryptedPassword);

        UserInput userInput = new UserInput(app, userName, encryptedPass.toString());

        databaseReference.child(user.getUid()).setValue(userInput);

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
        if (!TextUtils.isEmpty(app)) {

            String id = databaseReference.push().getKey();

            UserInput userInput = new UserInput(app, userName, password);
            databaseReference.child(user.getUid()).child(id).setValue(userInput);

            Toast.makeText(this, "Sent to database", Toast.LENGTH_LONG).show();
        } else {
                Toast.makeText(this, "Please enter an app name", Toast.LENGTH_LONG).show();
            }
    }
}

