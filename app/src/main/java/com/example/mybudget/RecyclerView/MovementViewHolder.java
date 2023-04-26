package com.example.mybudget.RecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybudget.R;

public class MovementViewHolder extends RecyclerView.ViewHolder {
    private TextView txt_quantity;
    private TextView txt_mType;
    private TextView txt_mDate;

    private MovementAdapterList mal;

    public MovementViewHolder(@NonNull View itemView, MovementAdapterList mal) {
        super(itemView);
        txt_quantity = itemView.findViewById(R.id.txt_quantity);
        txt_mType = itemView.findViewById(R.id.txt_mType);
        txt_mDate = itemView.findViewById(R.id.txt_mDate);
        mal = mal;
    }

    public TextView getTxt_quantity() {
        return txt_quantity;
    }

    public void setTxt_quantity(TextView txt_quantity) {
        this.txt_quantity = txt_quantity;
    }

    public TextView getTxt_mType() {
        return txt_mType;
    }

    public void setTxt_mType(TextView txt_mType) {
        this.txt_mType = txt_mType;
    }

    public TextView getTxt_mDate() {
        return txt_mDate;
    }

    public void setTxt_mDate(TextView txt_mDate) {
        this.txt_mDate = txt_mDate;
    }
}
