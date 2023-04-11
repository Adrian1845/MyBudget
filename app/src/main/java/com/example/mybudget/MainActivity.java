package com.example.mybudget;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private EditText edt_email;
    private EditText edt_pass;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
            Intent intent = new Intent(MainActivity.this, MainMenu.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edt_email= (EditText) findViewById(R.id.edt_email);
        edt_pass= (EditText) findViewById(R.id.edt_pass);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void login(View view) {
        //check if the email and password fields are empty and return if true
        if(edt_email.getText().toString().isEmpty() || edt_pass.getText().toString().isEmpty()){
            return;
        }
        else{
            //take data from fields
            String email = String.valueOf(edt_email.getText());
            String password = String.valueOf(edt_pass.getText());
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.i("firebasedb", "correct log in");
                                Toast.makeText(MainActivity.this, "Log in successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                //updateUI(user);
                                Intent intent = new Intent(MainActivity.this, MainMenu.class);
                                startActivity(intent);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.i("firebasedb", "error while log in", task.getException());
                                Toast.makeText(MainActivity.this, "Log in has failed", Toast.LENGTH_SHORT).show();
                                // updateUI(null);
                            }
                        }
                    });
        }

    }
    public void register(View view) {
        //check if the email and password fields are empty and return if true
        if(edt_email.getText().toString().isEmpty() || edt_pass.getText().toString().isEmpty()){
            return;
        }
        else{
            //take data from fields
            String email = String.valueOf(edt_email.getText()).trim();
            String password = String.valueOf(edt_pass.getText());
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.i("firebase1", "user sign up correct");
                        Toast.makeText(MainActivity.this, "user signed up successfully", Toast.LENGTH_SHORT).show();
                        //                            // updateUI(user);
                        Intent intent = new Intent(MainActivity.this, MainMenu.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.i("firebase1", "user was unable to register", task.getException());
                        Toast.makeText(MainActivity.this, "unable to register the user", Toast.LENGTH_SHORT).show();
                        //  updateUI(null);
                    }
                }
            });
        }

    }
}