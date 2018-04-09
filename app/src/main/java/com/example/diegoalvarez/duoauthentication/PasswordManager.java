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

import java.util.List;

public class PasswordManager extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private TextView textViewUserEmail;
    ListView listViewApplications;
    private DatabaseReference databaseReference;



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
        databaseReference  = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = firebaseAuth.getCurrentUser();

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

