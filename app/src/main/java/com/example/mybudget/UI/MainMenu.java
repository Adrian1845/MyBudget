package com.example.mybudget.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybudget.R;
import com.example.mybudget.classes.Account;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainMenu extends AppCompatActivity {
    int n;
    private TextView textview;
    //for linking we shall use the user ID as the main branch and then subfields as the accounts
    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
        else{
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        textview= (TextView) findViewById(R.id.textView);
        init();

    }
    public void init(){
        //check if user's UID (ID number) it is already on db, otherwise create new db entry
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get db
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        //get user's entry

        assert currentFirebaseUser != null;
        DatabaseReference userRef = db.getReference(currentFirebaseUser.getUid());
        Log.i("userRef", String.valueOf(userRef));
        //if there's not entry, create one
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if it doesn't have children, create new account and push to db
                if ( ! snapshot.hasChildren()){
                    int n=1;
                    //et user's id as main node
                    DatabaseReference myRef = db.getReference(currentFirebaseUser.getUid());
                    Account a = new Account(n, 0);
                    //get account ID as reference and push the account to db
                    myRef.child(String.valueOf(a.getId())).setValue(a).addOnSuccessListener(success -> Toast.makeText(getApplicationContext(), "User's data is now on db",Toast.LENGTH_LONG).show())
                            .addOnFailureListener(failure -> Toast.makeText(getApplicationContext(), "Error while registering user's date",Toast.LENGTH_LONG).show());
                }
                else{
                    //we just use a function a declare a global variable to set the id
                    numAccount(n -> Log.d("numAccount",String.valueOf(n))
                            ,userRef);

                }
            }
            //if fails (it shouldn't)
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i( "init", String.valueOf(error.toException()));
            }
        });
    }

    private void numAccount(@NonNull final Callback callback, DatabaseReference userRef) {
        //checks how many accounts a user has to set the id correctly
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check how many accounts the user has
                n = (int) snapshot.getChildrenCount();
                //uses a callback for asynchronous web APIs
                callback.onCallback(n);
                textview.setText(String.valueOf(n));
            }
            //if fails (it shouldn't)
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i( "init", String.valueOf(error.toException()));
            }
        });
    }

    public void log_out(View view) {
        //log out so you can enter with another user
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(MainMenu.this, "Log out successful", Toast.LENGTH_SHORT).show();
        //start main screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private interface Callback{
        //asynchronous handle
        void onCallback(int n);
    }
}