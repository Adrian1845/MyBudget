package com.example.mybudget.classes;

import java.util.Random;

public class Account {
    public int id;
    public int balance;

    public String iban;

    public Account(int id, int balance) {
        this.id = id;
        this.balance = balance;
        this.iban = generateNewAccount();
    }

    public Account(int id, int balance, String iban) {
        this.id = id;
        this.balance = balance;
        this.iban = iban;
    }

    public Account() {
        this.id = 0;
        this.balance = 0;
        this.iban = generateNewAccount();
    }

    public Account(int id) {
        this.id = id;
        this.balance = 0;
        this.iban = generateNewAccount();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    private String generateNewAccount() {
        //generate new account IBAN
        Random rand = new Random();
        StringBuilder card = new StringBuilder("ES");
        //generate 14 random numbers and concatenates them with prefix ES
        for (int i = 0; i < 14; i++)
        {
            int n = rand.nextInt(10);
            card.append(n);
        }
        //show account number IBAN
//        for (int i = 0; i < 16; i++)
//        {
//            if(i % 4 == 0){
//                System.out.print(" ");
//            }
//            System.out.print(card.charAt(i));
//        }
        return card.toString();
    }
}
