package com.example.diegoalvarez.duoauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddItem extends AppCompatActivity {

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

        editTextPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                boolean canAddItem;
                if (editTextPassword.getText().toString().length()>16) {
                    canAddItem=false;
                } else {
                    canAddItem=true;
                }

                if (canAddItem) {
                    buttonSave.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            saveUserInformation();
                        }
                    });
                } else {
                    editTextPassword.setError("Password must be 16 characters or less");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void saveUserInformation(){
        String app = editTextApp.getText().toString().trim();
        String userName = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        //Instantiate new instance of class
        AESHomeEncryption aes_home = new AESHomeEncryption();

        //AES Key generated around passphrase "duoauth1234"
        char [] key = new char[] {0x51,0x9e,0x4f,0x26,0xf3,0x8b,0x84,0x4C,0xC8,0x13,0x04,0xb6,0xa9,0x2D,0x28,0x59};

        Log.i("key length", "KEYLENGTH -> "+key.length);



        /**
         * Encrypt the password
         */
        //byte[] encryptedPassword = encryption.encryptAES(password);
        //byte[] encrypted_data = rsa_encryption.encryptRSA(password);
        byte [] encryptedPass = aes_home.AES_Encrypt(password, key);
        String encryptedPassword = new String(Base64.encode(encryptedPass, 1));

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

        if (!TextUtils.isEmpty(app)) {

            String id = databaseReference.push().getKey();

            UserInput userInput = new UserInput(id, app, userName, encryptedPassword);
            databaseReference.child(user.getUid()).child(id).setValue(userInput);
            Toast.makeText(this, "Sent to database", Toast.LENGTH_LONG).show();
            editTextApp.setText("");
            editTextUserName.setText("");
            editTextPassword.setText("");

        } else {
            Toast.makeText(this, "Please enter an app name", Toast.LENGTH_LONG).show();
        }



    }




}

