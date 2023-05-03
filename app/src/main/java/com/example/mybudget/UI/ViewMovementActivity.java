package com.example.mybudget.UI;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybudget.Classes.Movement;
import com.example.mybudget.DatePicker.DatePickerFragment;
import com.example.mybudget.R;
import com.example.mybudget.RecyclerView.MovementAdapterList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ViewMovementActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private int id;
    private DatabaseReference userRef;

    private RecyclerView rv_mov = null;
    private MovementAdapterList mal = null;
    private ArrayList<Movement> movements;
    private TextView txt_mQty;
    private EditText edt_mQty;

    private RadioGroup rg_type;
    private RadioButton rb_mIncome;
    private RadioButton rb_mWithdraw;
    private RadioGroup rg_compare;
    private RadioButton rb_mHigher;
    private RadioButton rb_mLower;
    private TextView txt_vType;
    private EditText edt_mType;
    private TextView txt_from;
    private EditText edt_from;
    private TextView txt_to;
    private EditText edt_to;
    private Button btn_reset;
    private ArrayList<String> filters = new ArrayList<>();

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
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get db
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        //get user's entry
        assert currentFirebaseUser != null;
        userRef = db.getReference(currentFirebaseUser.getUid()).child(String.valueOf(id));
        //------------------------------------------//
        rv_mov = findViewById(R.id.rv_mov);
        Spinner spn_filter = findViewById(R.id.spn_filter);
        Button btn_filter = findViewById(R.id.btn_filter);
        Button btn_rFilter = findViewById(R.id.btn_rFilter);

        txt_mQty = findViewById(R.id.txt_mQty);
        edt_mQty = findViewById(R.id.edt_mQty);
        rg_type = findViewById(R.id.rg_type);
        rg_compare = findViewById(R.id.rg_compare);
        rb_mIncome = findViewById(R.id.rb_mIncome);
        rb_mWithdraw = findViewById(R.id.rb_mWithdraw);
        rb_mHigher = findViewById(R.id.rb_mHigher);
        rb_mLower = findViewById(R.id.rb_mLower);

        txt_vType = findViewById(R.id.txt_vType);
        edt_mType = findViewById(R.id.edt_mType);

        txt_from = findViewById(R.id.txt_from);
        edt_from = findViewById(R.id.edt_from);
        txt_to = findViewById(R.id.txt_to);
        edt_to = findViewById(R.id.edt_to);
        btn_reset = findViewById(R.id.btn_reset);

        movements = new ArrayList<>();
        mal = new MovementAdapterList(this, movements);
        rv_mov.setAdapter(mal);

        //set OnClickListeners
        //edt_date.setOnClickListener(this::onClick);
        btn_filter.setOnClickListener(this::onClick);
        btn_rFilter.setOnClickListener(this::onClick);
        edt_from.setOnClickListener(this::onClick);
        edt_to.setOnClickListener(this::onClick);
        btn_reset.setOnClickListener(this::onClick);
        getMov();
        //fill spinner
        filters.add("Quantity");
        filters.add("Type");
        filters.add("Date");

        if (spn_filter != null) {
            spn_filter.setOnItemSelectedListener(ViewMovementActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ViewMovementActivity.this, R.layout.item_spinner, filters);
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
            case R.id.btn_rFilter:
                getMov();
                break;
            case R.id.edt_from:
                view.setTag(1);
                showDatePickerDialog(view);
                break;
            case R.id.edt_to:
                view.setTag(2);
                showDatePickerDialog(view);
                break;
            case R.id.btn_reset:
                reset();
                break;
        }
    }

    private void reset() {
        edt_from.setText("");
        edt_to.setText("");
    }

    private void showDatePickerDialog(View view) {
        //create new DatePickerFragment to select date
        DatePickerFragment newFragment = DatePickerFragment.newInstance((datePicker, year, month, day) -> {
            // +1 because January is zero
            final String selectedDate = day + "/" + (month + 1) + "/" + year;
            if((Integer.parseInt(view.getTag().toString()))==1){
                edt_from.setText(selectedDate);
            }
            else if((Integer.parseInt(view.getTag().toString()))==2){
                edt_to.setText(selectedDate);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    private void applyFilters() {
        //check filters
        ArrayList<Movement> filter = new ArrayList<>();
        movements = new ArrayList<>();
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    //check movements
                    mal.getMovements().clear();
                    for(DataSnapshot snapshotMov : snapshot.child("movements").getChildren()) {
                        movements.add(snapshotMov.getValue(Movement.class));
                    }
                    //check if filter qty is empty
                    if(! edt_mQty.getText().toString().isEmpty()) {
                        //for every movement
                        for(Movement m : movements){
                            int qty = m.getQty();
                            //if is income
                            if(rb_mIncome.isChecked()){
                                if(qty>0){
                                    //higher than
                                    if(rb_mHigher.isChecked()){
                                        if(qty>=Integer.parseInt(edt_mQty.getText().toString())){
                                            filter.add(m);
                                        }
                                    }
                                    //less than
                                    else if(rb_mLower.isChecked()){
                                        if(qty<=Integer.parseInt(edt_mQty.getText().toString())){
                                            filter.add(m);
                                        }
                                    }
                                }
                            }
                            //if is withdraw
                            else if(rb_mWithdraw.isChecked()){
                                int nQty=Integer.parseInt(edt_mQty.getText().toString());
                                if(qty<0){
                                    //higher than (spent more than)
                                    if(rb_mHigher.isChecked()){
                                        if(qty <= (nQty*=-1)){
                                            filter.add(m);
                                        }
                                    }
                                    //less than (spent less than)
                                    else if(rb_mLower.isChecked()){
                                        if(qty >= (nQty*=-1)){
                                            filter.add(m);
                                        }
                                    }
                                }
                            }
                            //equals
                            else{
                                if(qty==Integer.parseInt(edt_mQty.getText().toString())){
                                    filter.add(m);
                                }
                            }
                        }

                    }
                    else if (! edt_mType.getText().toString().isEmpty()) {
                        for(Movement m : movements) {
                            if(m.getType().equalsIgnoreCase(edt_mType.getText().toString())) {
                                filter.add(m);
                            }
                        }
                    }
                    else if (!edt_from.getText().toString().isEmpty() || !edt_to.getText().toString().isEmpty()) {
                        for(Movement m : movements) {
                            try {
                                if(dateFilter(m.getDate())) {
                                    filter.add(m);
                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    mal.setMovements(filter);
                    mal.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    //if it fails (it shouldn't)
                    Log.i( "getMov", String.valueOf(error.toException()));
                }
            });
        }

    private boolean dateFilter(String date) throws ParseException {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date filterDate = sdf.parse(String.valueOf(date));
        Date from;
        Date to;
        if(!edt_from.getText().toString().isEmpty() && !edt_to.getText().toString().isEmpty()) {
            from=sdf.parse(edt_from.getText().toString());
            to=sdf.parse(edt_to.getText().toString());
            return (filterDate.equals(from) || filterDate.equals(to)) ||
                    (filterDate.after(from) && filterDate.before(to));
        }
        else if(!edt_from.getText().toString().isEmpty() && edt_to.getText().toString().isEmpty()){
            from=sdf.parse(edt_from.getText().toString());
            return filterDate.equals(from) || filterDate.after(from);
        }
        else if(edt_from.getText().toString().isEmpty() && ! edt_to.getText().toString().isEmpty()){
            to=sdf.parse(edt_to.getText().toString());
            return filterDate.equals(to) || filterDate.before(to);
        }
        //compare if it's within last month
        return false;
    }

    private void getMov() {
        //retrieve movements
        movements = new ArrayList<>();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check movements
                mal.getMovements().clear();
                for (DataSnapshot snapshotMov : snapshot.child("movements").getChildren()) {
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
        //we check which filter is active
        if(adapterView.getItemAtPosition(i).toString().equalsIgnoreCase("Quantity")){
            txt_mQty.setVisibility(View.VISIBLE);
            edt_mQty.setVisibility(View.VISIBLE);
            rg_type.setVisibility(View.VISIBLE);
            rb_mIncome.setVisibility(View.VISIBLE);
            rb_mWithdraw.setVisibility(View.VISIBLE);
            rg_compare.setVisibility(View.VISIBLE);
            rb_mHigher.setVisibility(View.VISIBLE);
            rb_mLower.setVisibility(View.VISIBLE);

            txt_vType.setVisibility(View.GONE);
            edt_mType.setVisibility(View.GONE);
            edt_mType.setText("");

            txt_from.setVisibility(View.GONE);
            edt_from.setVisibility(View.GONE);
            edt_from.setText("");
            txt_to.setVisibility(View.GONE);
            edt_to.setVisibility(View.GONE);
            edt_to.setText("");
            btn_reset.setVisibility(View.GONE);
        }
        else if(adapterView.getItemAtPosition(i).toString().equalsIgnoreCase("Type")){
            txt_mQty.setVisibility(View.GONE);
            edt_mQty.setVisibility(View.GONE);
            edt_mQty.setText("");
            rg_type.setVisibility(View.GONE);
            rb_mIncome.setVisibility(View.GONE);
            rb_mIncome.setChecked(false);
            rb_mWithdraw.setVisibility(View.GONE);
            rb_mWithdraw.setChecked(false);
            rg_compare.setVisibility(View.GONE);
            rb_mHigher.setVisibility(View.GONE);
            rb_mHigher.setChecked(false);
            rb_mLower.setVisibility(View.GONE);
            rb_mLower.setChecked(false);

            txt_vType.setVisibility(View.VISIBLE);
            edt_mType.setVisibility(View.VISIBLE);

            txt_from.setVisibility(View.GONE);
            edt_from.setVisibility(View.GONE);
            edt_from.setText("");
            txt_to.setVisibility(View.GONE);
            edt_to.setVisibility(View.GONE);
            edt_to.setText("");
            btn_reset.setVisibility(View.GONE);
        }
        else if(adapterView.getItemAtPosition(i).toString().equalsIgnoreCase("Date")){
            txt_mQty.setVisibility(View.GONE);
            edt_mQty.setVisibility(View.GONE);
            edt_mQty.setText("");
            rg_type.setVisibility(View.GONE);
            rb_mIncome.setVisibility(View.GONE);
            rb_mIncome.setChecked(false);
            rb_mWithdraw.setVisibility(View.GONE);
            rb_mWithdraw.setChecked(false);
            rg_compare.setVisibility(View.GONE);
            rb_mHigher.setVisibility(View.GONE);
            rb_mHigher.setChecked(false);
            rb_mLower.setVisibility(View.GONE);
            rb_mLower.setChecked(false);

            txt_vType.setVisibility(View.GONE);
            edt_mType.setVisibility(View.GONE);
            edt_mType.setText("");

            txt_from.setVisibility(View.VISIBLE);
            edt_from.setVisibility(View.VISIBLE);
            txt_to.setVisibility(View.VISIBLE);
            edt_to.setVisibility(View.VISIBLE);
            btn_reset.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}