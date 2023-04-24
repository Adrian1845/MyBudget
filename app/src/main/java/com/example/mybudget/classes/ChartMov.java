package com.example.mybudget.classes;

public class ChartMov {
    public String type;
    public Integer qty;

    public ChartMov(String type, Integer qty) {
        this.type = type;
        this.qty = qty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }
}
