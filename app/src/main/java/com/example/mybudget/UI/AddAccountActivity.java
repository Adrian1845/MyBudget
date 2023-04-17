package com.example.mybudget.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mybudget.R;
import com.example.mybudget.classes.Account;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddAccountActivity extends AppCompatActivity {
    private FirebaseDatabase db;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference userRef;

    private int n;
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
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get db
        db = FirebaseDatabase.getInstance();
        //get user's entry
        assert currentFirebaseUser != null;
        //get user's id as main node
        userRef = db.getReference(currentFirebaseUser.getUid());
        Log.i("userRef", String.valueOf(userRef));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
    }

    public void randomAcc(View view){
        //we create a dialog to make sure the user wants to create a random account
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Delete account");
        builder.setMessage("Are you sure you want to create a random Account?");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if they confirm
                        //we create a new Account and add it to firebase
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                n = 1;
                                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                                {
                                    //get Account class only
                                    Account ac = dataSnapshot.getValue(Account.class);
                                    //check if all id's are completed or there's one missing to fill
                                    assert ac != null;
                                    if(n == ac.getId()){
                                        n++;
                                    }
                                    else{
                                        break;
                                    }
                                }

                                Account a = new Account(n, 0);
                                //get account ID as reference and push the account to db
                                userRef.child(String.valueOf(a.getId())).setValue(a).addOnSuccessListener(success -> showMessage("New account created " + a.getIban()))
                                        .addOnFailureListener(failure -> showMessage("Error while creating new account"));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                //if fails (it shouldn't)
                                Log.i( "randomAcc", String.valueOf(error.toException()));
                            }
                        });
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if they cancel
                showMessage("Do not create random account");
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}