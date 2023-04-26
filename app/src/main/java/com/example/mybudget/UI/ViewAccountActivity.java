package com.example.mybudget.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybudget.R;
import com.example.mybudget.Classes.ChartMov;
import com.example.mybudget.Classes.Movement;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class ViewAccountActivity extends AppCompatActivity {
    public static final String EXTRA_ID_ACCOUNT = "com.example.mybudget.id_account";
    public static final String EXTRA_BALANCE = "com.example.mybudget.balance";
    private int id;
    private FirebaseDatabase db;
    private FirebaseUser currentFirebaseUser;
    private DatabaseReference userRef;
    public static PieChart pieChart;
    private TextView txt_vBalance;

    private ArrayList<Integer> MY_COLORS = new ArrayList<>(); /*{Color.parseColor("#9750EF"),
            Color.parseColor("#8CF479"),
            Color.parseColor("#EF9050"),
            Color.parseColor("#3DBDEF"),
            Color.parseColor("#F13E67"),
            Color.parseColor("#50EF85")};*/
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
        Log.i("userRef", String.valueOf(userRef));
        Intent intent = getIntent();

        if(intent != null){
            id = (Integer) intent.getSerializableExtra(MainMenu.EXTRA_ID);
        }
        //get user's id as main node
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //get db
        db = FirebaseDatabase.getInstance();
        //get user's entry
        assert currentFirebaseUser != null;
        userRef = db.getReference(currentFirebaseUser.getUid()).child(String.valueOf(id));
        //init_acc method to start screen with data
        init_acc();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);

        pieChart = findViewById(R.id.pieChart);
        txt_vBalance = findViewById(R.id.txt_vBalance);
    }

        private void init_acc() {
        //start screen with all necessary data
            //get balance
            getBalance(new Callback(){

                @Override
                public void onPiechart(ArrayList<PieEntry> pieArray) {
                }

                @Override
                public void onBalance(long balance) {
                    //set balance
                    txt_vBalance.setText(String.valueOf(balance));
                }
            });
            createChart(new Callback(){

                @Override
                public void onPiechart(ArrayList<PieEntry> pieArray) {
                    //reload chart to assign data
                    //set the array
                    randomColors(pieArray.size());
                    PieDataSet pieDataSet = new PieDataSet(pieArray,"");
                    pieDataSet.setColors(MY_COLORS);
                    pieDataSet.setValueTextColor(R.color.black);
                    pieDataSet.setValueTextSize(32f);

                    PieData pieData = new PieData(pieDataSet);
                    pieChart.setData(pieData);
                    pieChart.animate();
                    pieChart.setEntryLabelColor(R.color.black);
                    pieChart.getDescription().setEnabled(false);

                    //reload chart
                    pieChart.notifyDataSetChanged();
                    pieChart.invalidate();
                }

                @Override
                public void onBalance(long balance) {
                }
            });
        }

    private void randomColors(int size) {
        // create object of Random class
        for(int i=0; i<size;i++){
            Random obj = new Random();
            int rand_num = obj.nextInt(0xffffff + 1);
            // format it as hexadecimal string and print
            String colorCode = String.format("#%06x", rand_num);
            MY_COLORS.add(Color.parseColor(colorCode));
        }

    }

    private void getBalance(Callback callback) {
        //retrieve balance data
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //check balance field and retrieve data
                callback.onBalance((Long) snapshot.child("balance").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i( "getBalance", String.valueOf(error.toException()));
            }
        });
    }

    private void createChart(@NonNull final Callback callback) {
        //retrieve data and create the chart
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //get all the movements from the account
                ArrayList<PieEntry> pieArray = new ArrayList<>();
                ArrayList<Movement> arrayMov = new ArrayList<>();
                for(DataSnapshot snapshotMov : snapshot.child("movements").getChildren()){
                    arrayMov.add(snapshotMov.getValue(Movement.class));
                }
                //create new charMov object to assign to the chart
                if(arrayMov.size()!=0){
                    ArrayList<ChartMov> arrayChartMov = new ArrayList<>();
                    int qty;
                    String type;
                    boolean check;
                    //for every mov retrieve quantity and type
                    for (Movement mov : arrayMov) {
                        qty = mov.getQty();
                        type = mov.getType();
                        check=false;
                        if(qty<0){
                            //check if type is already in the chart
                            for (ChartMov cm : arrayChartMov) {
                                if (cm.getType().equalsIgnoreCase(type)) {
                                    cm.setQty(cm.getQty() + qty);
                                    check=true;
                                    break;
                                }
                            }
                            //if it's not
                            if(!check) {
                                ChartMov c = new ChartMov(type,qty *= -1);
                                arrayChartMov.add(c);
                            }
                        }
                    }
                    //add the chartMov to the chart
                    for (ChartMov cm: arrayChartMov) {
                        pieArray.add(new PieEntry(cm.getQty(),cm.getType()));
                    }
                }
                //call callback
                Log.d("PieArray", "size:" +pieArray.size());
                callback.onPiechart(pieArray);
            }

            //if fails (it shouldn't)
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i( "createChart", String.valueOf(error.toException()));
            }
        });
    }
    public void newMov(View view) {
        //change screen to NewMovementActivity
        Intent intent = new Intent(this, NewMovementActivity.class);
        intent.putExtra(EXTRA_ID_ACCOUNT,id);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra(EXTRA_BALANCE,Integer.valueOf(txt_vBalance.getText().toString()));
        startActivity(intent);
    }
    public void viewMov(View view) {
        //change screen to ViewMovementActivity
        Intent intent = new Intent(this, ViewMovementActivity.class);
        intent.putExtra(EXTRA_ID_ACCOUNT,id);
        startActivity(intent);
    }



    private interface Callback{
        //asynchronous handle
        void onPiechart(ArrayList<PieEntry> pieArray);
        void onBalance(long balance);
    }
}