package com.example.mybudget.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

import java.util.ArrayList;


public class MainMenu extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    int n;

    private ArrayList<String> IBANs;
    private ArrayList<Account> idAccount;
    private TextView textview;

    private Spinner spn_acc;
    private ArrayAdapter<String> adapter;

    private FirebaseDatabase db;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference userRef;
    public MainMenu() {
    }

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
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get db
        db = FirebaseDatabase.getInstance();
        //get user's entry

        assert currentFirebaseUser != null;
        //get user's id as main node
        userRef = db.getReference(currentFirebaseUser.getUid());
        Log.i("userRef", String.valueOf(userRef));
        init();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        spn_acc = findViewById(R.id.spn_acc);


    }
    public void init(){

        //if there's not entry, create one
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check if user's UID (ID number) it is already on db, otherwise create new db entry
                //if it doesn't have children, create new account and push to db
                if (! snapshot.hasChildren()){
                    int n=1;
                    Account a = new Account(n, 0);
                    //get account ID as reference and push the account to db
                    userRef.child(String.valueOf(a.getId())).setValue(a).addOnSuccessListener(success ->showMessage("User's data is now on db"))
                            .addOnFailureListener(failure -> showMessage( "Error while registering user's date"));
                }
                else{
                    //we just use a function a declare a global variable to set the id
                    numAccount(new Callback() {
                        @Override
                        public void onCallback(int n) {
                            Log.d("numAccount",String.valueOf(n));
                        }

                        @Override
                        public void onSuccess(ArrayList<String> arrayList) {

                        }
                    }, userRef);
                }
                //retrieve IBANs for our spinner (and delete accounts)
                getIBAN(new Callback() {
                    @Override
                    public void onCallback(int n) {
                        Log.d("onCallback","Callback");
                    }

                    @Override
                    public void onSuccess(ArrayList<String> arrayList) {
                        //we set the spinner with the information (IBAN) we retrieved from firebase
                        if (spn_acc != null) {
                            spn_acc.setOnItemSelectedListener(MainMenu.this);
                            adapter = new ArrayAdapter<>(MainMenu.this, R.layout.item_spinner, IBANs);
                            spn_acc.setAdapter(adapter);
                        }
                    }
                }, userRef);

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
            }
            //if fails (it shouldn't)
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i( "init", String.valueOf(error.toException()));
            }
        });
    }
    private void getIBAN(@NonNull final Callback callback, DatabaseReference userRef) {
        //retrieve the IBANs of the user
        IBANs = new ArrayList<>();
        idAccount = new ArrayList<>();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check how many IBANs the user has and retrieves them
                //for every account we collect the IBAN and we set the account into another arrayList
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    //get Account class only
                    Account acIBAN = dataSnapshot.getValue(Account.class);
                    idAccount.add(acIBAN);
                    assert acIBAN != null;
                    IBANs.add(acIBAN.getIban());
                }
                //uses a callback for asynchronous web APIs
                callback.onSuccess(IBANs);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //if fails (it shouldn't)
                Log.i( "init", String.valueOf(error.toException()));
            }
        });
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //for evey account we check which account relates to the IBAN selected to retrieve the id
        for (Account id : idAccount){
            if(id.getIban().equalsIgnoreCase(adapterView.getItemAtPosition(i).toString())){
                n=id.getId();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.i("tipo_fruta",String.valueOf(n));
    }
    public void newAcc(View view){
        //change screen to AddAccountActivity window
        Intent intent = new Intent(MainMenu.this, AddAccountActivity.class);
        startActivity(intent);
    }
    public void delAcc(View view) {
        //we create a dialog to make sure the user wants to delete his account
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Delete account");
        builder.setMessage("Are you sure you want to delete your account? (this action cannot be reverted)");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //if they confirm
                        //we delete via id as our firebase is structured on the id's
                        userRef.child(String.valueOf(n)).removeValue();
                        //remove from the adapter the id-1 (arrays starts at 0 index)
                        IBANs.remove(n-1);
                        //communicate to the adapter that something has changed
                        adapter.notifyDataSetChanged();

                        showMessage("Account deleted");
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //if they cancel
                showMessage("Do not delete the account");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void log_out(View view) {
        //log out so you can enter with another user
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(MainMenu.this, "Log out successful", Toast.LENGTH_SHORT).show();
        //start main screen
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    // https://stackoverflow.com/questions/47847694/how-to-return-datasnapshot-value-as-a-result-of-a-method/47853774
    private interface Callback{
        //asynchronous handle
        void onCallback(int n);
        void onSuccess(ArrayList<String> arrayList);
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}