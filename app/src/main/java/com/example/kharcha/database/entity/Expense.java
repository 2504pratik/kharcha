package com.example.kharcha.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "expenses")
public class Expense {
    // Variables
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "amount")
    private String amount;

    @ColumnInfo(name = "person")
    private String person;

    @ColumnInfo(name = "owedByMe")
    private Boolean owedByMe;

    @ColumnInfo(name = "date")
    private String date;

    @ColumnInfo(name = "expense_id")
    @PrimaryKey(autoGenerate = true)
    private int id;

    // Constructors
    @Ignore
    public Expense() {}

    public Expense(String title, String amount, String person, Boolean owedByMe, String date) {
        this.title = title;
        this.amount = amount;
        this.person = person;
        this.owedByMe = owedByMe;
        this.date = date;
    }

    // Getter and setter
    public String getTitle() {return title;}

    public void setTitle(String title) {this.title = title;}

    public String getAmount() {return amount;}

    public void setAmount(String amount) {this.amount = amount;}

    public String getPerson() {return person;}

    public void setPerson(String person) {this.person = person;}

    public Boolean getOwedByMe() {return owedByMe;}

    public void setOwedByMe(Boolean owedByMe) {this.owedByMe = owedByMe;}

    public String getDate() {return date;}

    public void setDate(String date) {this.date = date;}

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}
}
