package com.example.mybudget.Classes;

public class Movement {
    public int id;
    public int qty;
    public String date;
    public String type;

    public Movement(int id, int qty, String date, String type) {
        this.id = id;
        this.qty = qty;
        this.date = date;
        this.type = type;
    }


    public Movement() {
        this.id = 0;
        this.qty = 1;
        this.date = "1";
        this.type = "comida";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
