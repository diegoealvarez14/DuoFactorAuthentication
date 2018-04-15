package com.example.diegoalvarez.duoauthentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

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


        listViewApplications.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
               UserInput userInput = userInputsList.get(i);

               updateDialog(userInput.getId(), userInput.getAppName(), userInput.getUserName(), userInput.getPassword());

                return false;
            }
        });
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
    private void updateDialog(final String entryId, final String appName, final String userName, final String pass) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.update_entries,null);

        dialogBuilder.setView(dialogView);

        final EditText editTextAppName = (EditText) dialogView.findViewById(R.id.editTextAppName);
        final EditText editTextUserName = (EditText) dialogView.findViewById(R.id.editTextUserName);
        final EditText editTextPass = (EditText) dialogView.findViewById(R.id.editTextPass);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdate);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDelete);

        dialogBuilder.setTitle("Update Your Information");

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                String applicationName = editTextAppName.getText().toString().trim();
                String applicationUserName = editTextUserName.getText().toString().trim();
                String applicationPass = editTextPass.getText().toString().trim();

                if (TextUtils.isEmpty(applicationName)) {
                    editTextAppName.setError("Name Required");
                    return;
                }
                if (TextUtils.isEmpty(applicationUserName)) {
                    editTextUserName.setError("User Name Required");
                    return;
                }
                if (TextUtils.isEmpty(applicationPass)) {
                    editTextPass.setError("Password Required");
                    return;
                }
                updateInformation (entryId, applicationName, applicationUserName, applicationPass);
                alertDialog.dismiss();
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteInformation(entryId);
            }
        });
    }

    private boolean deleteInformation(String id) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid()).child(id);
        databaseReference.removeValue();
        Toast.makeText(this, "Entry Deleted", Toast.LENGTH_SHORT).show();
        return true;
    }

    private boolean updateInformation(String entryId, String appName, String userName, String pass) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(user.getUid()).child(entryId);

        UserInput userInput = new UserInput(entryId, appName, userName, pass);
        databaseReference.setValue(userInput);
        Toast.makeText(this,"User Information Updated Successfully", Toast.LENGTH_LONG).show();
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

