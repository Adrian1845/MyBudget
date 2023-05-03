package com.example.mybudget.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mybudget.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

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

        edt_email= findViewById(R.id.edt_email);
        edt_pass= findViewById(R.id.edt_pass);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    public void login(View view) {
        //check if the email and password fields are empty and return if true
        if(edt_email.getText().toString().isEmpty()){
            edt_email.setError("Incorrect email");
            return;
        }
        if(edt_pass.getText().toString().isEmpty()){
            edt_pass.setError("Incorrect password");
            return;
        }

        //take data from fields
        String email = String.valueOf(edt_email.getText());
        String password = String.valueOf(edt_pass.getText());
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.i("login", "correct log in");
                        showMessage("Logged in successfully");
                        //updateUI(user);
                        Intent intent = new Intent(MainActivity.this, MainMenu.class);
                        startActivity(intent);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.i("login", "error while log in", task.getException());
                        showMessage( "Log in has failed");
                        // updateUI(null);
                    }
                });
    }
    public void register(View view) {
        //check if the email and password fields are empty and return if true
        if(edt_email.getText().toString().isEmpty()){
            edt_email.setError("Incorrect email");
            return;
        }
        if(edt_pass.getText().toString().isEmpty()){
            edt_pass.setError("Incorrect password");
            return;
        }
        //take data from fields
        String email = String.valueOf(edt_email.getText()).trim();
        String password = String.valueOf(edt_pass.getText());
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.i("firebase1", "user sign up correct");
                showMessage("user signed up successfully");
                //updateUI(user);
                Intent intent = new Intent(MainActivity.this, MainMenu.class);
                startActivity(intent);
            } else {
                // If sign in fails, display a message to the user.
                Log.i("firebase1", "user was unable to register", task.getException());
                showMessage("unable to register the user");
                //  updateUI(null);
            }
        });
    }

    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}