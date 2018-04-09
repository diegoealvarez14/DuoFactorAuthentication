package com.example.diegoalvarez.duoauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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

        buttonSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });
    }

    private void saveUserInformation(){
        String app = editTextApp.getText().toString().trim();
        String userName = editTextUserName.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

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

