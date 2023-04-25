package com.example.mybudget.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.mybudget.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewMovementActivity extends AppCompatActivity {

    private int id;
    private FirebaseDatabase db;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference userRef;
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
        Log.i("userRef", String.valueOf(userRef));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movement);
        Intent intent = getIntent();
        if(intent != null){
            id = (Integer) intent.getSerializableExtra(ViewAccountActivity.EXTRA_ID_ACCOUNT);
        }
        //get user's id as main node
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get db
        db = FirebaseDatabase.getInstance();
        //get user's entry
        assert currentFirebaseUser != null;
        userRef = db.getReference(currentFirebaseUser.getUid()).child(String.valueOf(id));
    }
}