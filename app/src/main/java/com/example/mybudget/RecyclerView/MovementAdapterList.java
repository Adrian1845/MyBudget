package com.example.mybudget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybudget.Classes.Movement;
import com.example.mybudget.R;

import java.util.ArrayList;

public class MovementAdapterList extends RecyclerView.Adapter<MovementViewHolder> {

    private Context context;
    private ArrayList<Movement> movements;
    private LayoutInflater inflate = null;

    public MovementAdapterList(Context context, ArrayList<Movement> movements) {
        this.context = context;
        this.movements = movements;
        this.inflate = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public MovementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = inflate.inflate(R.layout.item_movement,parent,false);
        return new MovementViewHolder(mItemView, this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MovementViewHolder holder, int position) {
        Movement m = this.getMovements().get(position);
        holder.getTxt_quantity().setText("Quantity: " + m.getQty());
        holder.getTxt_mType().setText("Type: " + m.getType());
        holder.getTxt_mDate().setText("Date: "+m.getDate());
    }

    @Override
    public int getItemCount() {
        return this.movements.size();
    }

    public ArrayList<Movement> getMovements() {
        return movements;
    }

    public void setMovements(ArrayList<Movement> movements) {
        this.movements = movements;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public LayoutInflater getInflate() {
        return inflate;
    }

    public void setInflate(LayoutInflater inflate) {
        this.inflate = inflate;
    }
}
