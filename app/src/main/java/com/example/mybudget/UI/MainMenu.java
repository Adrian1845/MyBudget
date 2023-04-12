package com.example.mybudget.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

import java.util.Random;

public class MainMenu extends AppCompatActivity {

    private FirebaseAuth mAuth;
    //for linking we shall use the user ID as the main branch and then subfields as the accounts
    @Override
    public void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
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
                if (!snapshot.hasChildren()){
                    Account a = new Account();
                    //et user's id as main node
                    DatabaseReference myRef = db.getReference(currentFirebaseUser.getUid());
                    //get account ID as reference and push the account to db
                    myRef.child(a.getId()).setValue(a).addOnSuccessListener(success -> Toast.makeText(getApplicationContext(), "User's data is now on db",Toast.LENGTH_LONG).show())
                            .addOnFailureListener(failure -> Toast.makeText(getApplicationContext(), "Error while registering user's date",Toast.LENGTH_LONG).show());
                }
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

}