package com.example.kharcha.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.kharcha.database.entity.Expense;

import java.util.List;

@Dao
public interface ExpenseDAO {
    @Insert
    long addExpense(Expense expense);

    @Update
    void updateExpense(Expense expense);

    @Delete
    void deleteExpense(Expense expense);

    @Query("SELECT * FROM expenses ORDER BY expense_id DESC")
    LiveData<List<Expense>> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE title = :title AND amount = :amount AND date = :date LIMIT 1")
    Expense findDuplicateExpense(String title, String amount, String date);
}
