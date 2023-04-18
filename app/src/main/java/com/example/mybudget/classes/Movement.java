package com.example.mybudget.classes;

public class Movement {
    public int qty;
    public String date;
    public String type;

    public Movement(int qty, String date, String type) {
        this.qty = qty;
        this.date = date;
        this.type = type;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
