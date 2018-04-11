package com.example.diegoalvarez.duoauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PasswordManager extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    ListView listViewApplications;
    private DatabaseReference databaseReference;
    FirebaseUser user;
    List<UserInput> userInputsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_manager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, SigninActivity.class));
        }
        user = firebaseAuth.getCurrentUser();

        databaseReference  = FirebaseDatabase.getInstance().getReference(user.getUid());

        userInputsList = new ArrayList<>();
        listViewApplications = (ListView) findViewById(R.id.listViewApplications);

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);

        textViewUserEmail.setText("Welcome " + user.getEmail());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


/*
        String [] items = getResources().getStringArray(R.array.passwordManager);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1, items);

        ListView lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userInputsList.clear();

                for(DataSnapshot applicationSnapshot : dataSnapshot.getChildren()) {
                    UserInput userInput = applicationSnapshot.getValue(UserInput.class);
                    userInputsList.add(userInput);
                }

                //check the main activity on this
                InputList adapter = new InputList(PasswordManager.this, userInputsList);
                listViewApplications.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_addItem:
                Intent intent = new Intent(PasswordManager.this, AddItem.class);
                startActivity(intent);
                return true;

            case R.id.action_logOut:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(this, SigninActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}

