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
                            try {
                                saveUserInformation();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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

    private void saveUserInformation() throws Exception {
        String app = editTextApp.getText().toString().trim();
        String userName = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        //Instantiate new instance of class
        //AESHomeEncryption aes_home = new AESHomeEncryption();
        Encryption encryption = new Encryption();


        /**
         * Encrypt user information being sent to the database
         *
         * All information is converted to base64 for output to Firebase console since some of the bytes cannot be represented by ASCII characters.
         *
         * Pushing these unrepresentable characters to the database would result in us being unable to retrieve the original data for decryption.
         */
        byte[] encryptedPasswordByteArray = encryption.encryptAES(password);
        String encryptedPassword = Base64.encodeToString(encryptedPasswordByteArray, 1);
        byte[] encryptedUserNameByteArray = encryption.encryptAES(userName);
        String encryptedUserName = Base64.encodeToString(encryptedUserNameByteArray, 1);
        byte[] encryptedAppByteArray = encryption.encryptAES(app);
        String encryptedApp = Base64.encodeToString(encryptedAppByteArray, 1);


        /**
         * Send user information to the database
         */
        if (!TextUtils.isEmpty(app)) {
            String id = databaseReference.push().getKey();
            UserInput userInput = new UserInput(id, encryptedApp, encryptedUserName, encryptedPassword);
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

