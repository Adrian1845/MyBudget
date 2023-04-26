package com.example.mybudget.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mybudget.Classes.Movement;
import com.example.mybudget.R;
import com.example.mybudget.RecyclerView.MovementAdapterList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewMovementActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int id;
    private FirebaseDatabase db;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference userRef;

    private RecyclerView rv_mov = null;
    private MovementAdapterList mal = null;
    private ArrayList<Movement> movements;
    private Spinner spn_filter;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> filters = new ArrayList<>();
    private Button btn_filter;
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
        //------------------------------------------//
        rv_mov = findViewById(R.id.rv_mov);
        spn_filter = findViewById(R.id.spn_filter);
        btn_filter = findViewById(R.id.btn_filter);

        movements = new ArrayList<>();
        mal = new MovementAdapterList(this, movements);
        rv_mov.setAdapter(mal);

        //set OnClickListeners
        //edt_date.setOnClickListener(this::onClick);
        btn_filter.setOnClickListener(this::onClick);
        getMov();
        //fill spinner
        filters.add("Quantity");
        filters.add("Type");
        filters.add("Date");

        if (spn_filter != null) {
            spn_filter.setOnItemSelectedListener(ViewMovementActivity.this);
            adapter = new ArrayAdapter<>(ViewMovementActivity.this, R.layout.item_spinner, filters);
            spn_filter.setAdapter(adapter);
        }
    }


    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        //specify onClicks
        switch (view.getId()) {
            case R.id.edt_date:
                //showDatePickerDialog();
                break;
            case R.id.btn_filter:
                applyFilters();
                break;
        }
    }

    private void applyFilters() {

    }

    private void getMov() {
        //retrieve movements
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check movements
                mal.getMovements().clear();
                for(DataSnapshot snapshotMov : snapshot.child("movements").getChildren()){
                    movements.add(snapshotMov.getValue(Movement.class));
                    mal.setMovements(movements);
                    mal.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //if it fails (it shouldn't)
                Log.i( "getMov", String.valueOf(error.toException()));
            }
        });
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            rv_mov.setLayoutManager(new GridLayoutManager(this,2));
        } else {
            // In portrait
            rv_mov.setLayoutManager(new LinearLayoutManager(this));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private interface Callback{
        //asynchronous handle
        void onFill(ArrayList<String> arrayList);
    }
}