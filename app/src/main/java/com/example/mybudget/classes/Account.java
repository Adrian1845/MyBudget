package com.example.mybudget.classes;

import java.util.Random;

public class Account {
    public String id;
    public int balance;

    public Account(int balance) {
        this.id = generateNewAccount();
        this.balance = balance;
    }

    public Account() {
        this.id = generateNewAccount();
        this.balance = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    private String generateNewAccount() {
        //generate new account IBAN
        Random rand = new Random();
        String card = "ES";
        //generate 14 random numbers and concatenates them with prefix ES
        for (int i = 0; i < 14; i++)
        {
            int n = rand.nextInt(10) + 0;
            card += Integer.toString(n);
        }
        //show account number IBAN
//        for (int i = 0; i < 16; i++)
//        {
//            if(i % 4 == 0){
//                System.out.print(" ");
//            }
//            System.out.print(card.charAt(i));
//        }
        return card;
    }
}
