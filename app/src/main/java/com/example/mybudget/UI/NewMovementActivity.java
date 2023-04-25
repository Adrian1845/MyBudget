package com.example.mybudget.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mybudget.DatePicker.DatePickerFragment;
import com.example.mybudget.R;
import com.example.mybudget.classes.Movement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.GregorianCalendar;

public class NewMovementActivity extends AppCompatActivity {

    private int id;
    private int balance;
    private FirebaseDatabase db;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference userRef;
    private EditText edt_date;
    private EditText edt_qty;
    private EditText edt_type;
    private Button btn_submitMov;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.reload();
        } else {
            Toast.makeText(this, "You are not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        Log.i("userRef", String.valueOf(userRef));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_movement);
        Intent intent = getIntent();
        if (intent != null) {
            //get extra
            id = (Integer) intent.getSerializableExtra(ViewAccountActivity.EXTRA_ID_ACCOUNT);
            balance = (Integer) intent.getSerializableExtra(ViewAccountActivity.EXTRA_BALANCE);
        }
        //get user's id as main node
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get db
        db = FirebaseDatabase.getInstance();
        //get user's entry
        assert currentFirebaseUser != null;
        userRef = db.getReference(currentFirebaseUser.getUid()).child(String.valueOf(id));

        edt_qty = findViewById(R.id.edt_qty);
        edt_type = findViewById(R.id.edt_type);
        edt_date = findViewById(R.id.edt_date);
        btn_submitMov = findViewById(R.id.btn_submitMov);
        //set OnClickListeners
        edt_date.setOnClickListener(this::onClick);
        btn_submitMov.setOnClickListener(this::onClick);
    }


    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        //specify onClicks
        switch (view.getId()) {
            case R.id.edt_date:
                showDatePickerDialog();
                break;
            case R.id.btn_submitMov:
                submitMov();
                break;
        }
    }


    private void showDatePickerDialog() {
        //create new DatePickerFragment to select date
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            // +1 because January is zero
            final String selectedDate = day + "/" + (month + 1) + "/" + year;
            edt_date.setText(selectedDate);
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void submitMov() {
        //add movement to firebase
        if (edt_qty.getText().toString().isEmpty()) {
            //if its empty
            edt_qty.setError("Incorrect quantity");
            return;
        }
        if (edt_type.getText().toString().isEmpty()) {
            //if its empty
            edt_type.setError("Incorrect type");
            return;
        }
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int n = 1;
                String date;
                for (DataSnapshot dataSnapshot : snapshot.child("movements").getChildren()) {
                    //get Movement class only
                    Movement mov = dataSnapshot.getValue(Movement.class);
                    //check if all id's are completed or there's one missing to fill
                    assert mov != null;
                    if (n == mov.getId()) {
                        n++;
                    } else {
                        break;
                    }
                }
                //autofill date field if it's empty with current date
                if(edt_date.getText().toString().isEmpty()){
                    GregorianCalendar cal = new GregorianCalendar();
                    int day  = cal.get(GregorianCalendar.DATE);
                    int month  = cal.get(GregorianCalendar.MONTH)+1;
                    int year = cal.get(GregorianCalendar.YEAR);
                    date = day+"/"+month+"/"+year;
                }
                else{
                    date = edt_date.getText().toString();
                }
                //create new movement and push to db
                Movement m = new Movement(n, Integer.parseInt(edt_qty.getText().toString()), date, edt_type.getText().toString());
                userRef.child("movements").child(String.valueOf(n)).setValue(m).addOnSuccessListener(success -> showMessage("New movement created"))
                        .addOnFailureListener(failure -> showMessage("Error while submitting new movement"));
                //update balance field
                userRef.child("balance").setValue(balance+Integer.parseInt(edt_qty.getText().toString()));
                showMessage("Balance updated");

                edt_date.setText("");
                edt_qty.setText("");
                edt_type.setText("");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i( "submitMov", String.valueOf(error.toException()));
            }
        });
    }
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}