package com.example.mybudget.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    private EditText edt_iban;
    private EditText edt_balance;
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

        edt_iban = findViewById(R.id.edt_iban);
        edt_balance = findViewById(R.id.edt_balance);
    }
    public void newAcc(View view){
        //create new account with custom data
        //check if balance and iban are suitable for use
        //check if they are empty
        if( ! edt_balance.getText().toString().isEmpty() || ! edt_iban.getText().toString().isEmpty()) {
            //check if balance is less than 0
            if(Integer.valueOf(edt_balance.getText().toString())<0){
                edt_balance.setError("Balance cannot be less than 0");
                return;
            }
            String iban = edt_iban.getText().toString();
            //check if IBAN is less than 16 positions
            if (!(iban.length() <16)){
                //run through IBAN to check chars
                char c;
                for(int i=0; i<iban.length(); i++){
                    c=iban.charAt(i);
                    //if first 2 chars are not letters
                    if(i<2 && !Character.isLetter(c)){
                        edt_iban.setError("First 2 positions have to be letters. Ex: ES");
                        return;
                    }
                    //if last 14 chars are not digits
                    if(i>=2 && !Character.isDigit(c)){
                        edt_iban.setError("Positions 2-16 have to be digits");
                        return;
                    }
                }
            }
            else{
                edt_iban.setError("IBAN has to be 2 letters and 14 digits");
                return;
            }
            //we create a dialog to make sure the user wants to create a new account
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("New account");
            builder.setMessage("Are you sure you want to create a new Account?");
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

                                    Account a = new Account(n, Integer.valueOf(edt_balance.getText().toString()), edt_iban.getText().toString());
                                    //get account ID as reference and push the account to db
                                    userRef.child(String.valueOf(a.getId())).setValue(a).addOnSuccessListener(success -> showMessage("New account created " + a.getIban()))
                                            .addOnFailureListener(failure -> showMessage("Error while creating new account"));
                                    edt_balance.setText("");
                                    edt_iban.setText("");
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    //if fails (it shouldn't)
                                    Log.i( "newAcc", String.valueOf(error.toException()));
                                }
                            });
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //if they cancel
                    showMessage("Do not create new account");
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else{
            if(edt_balance.getText().toString().isEmpty()){
                edt_balance.setError("Incorrect balance");
            }
            if(edt_iban.getText().toString().isEmpty()){
                edt_iban.setError("Incorrect IBAN");
            }
        }
    }
    public void randomAcc(View view){
        //we create a dialog to make sure the user wants to create a random account
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Random account");
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