package com.example.mybudget.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.mybudget.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

public class ViewAccountActivity extends AppCompatActivity {
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_account);
        Intent intent = getIntent();

        if(intent != null){
            id = (Integer) intent.getSerializableExtra(MainMenu.EXTRA_ID);
        }

        PieChart pieChart = findViewById(R.id.pieChart);
        ArrayList<PieEntry> visitors = new ArrayList<>();
        visitors.add(new PieEntry(300,"COMIDA"));
        visitors.add(new PieEntry(400,"LUZ, AGUA, GAS"));
        visitors.add(new PieEntry(300,"OCIO"));
        visitors.add(new PieEntry(600,"ALQUILER"));
        visitors.add(new PieEntry(50,"GYM"));

        PieDataSet pieDataSet = new PieDataSet(visitors,"SPENDINGS");
        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.animate();
    }
}