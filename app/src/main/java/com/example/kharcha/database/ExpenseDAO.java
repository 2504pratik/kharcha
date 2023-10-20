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
    public long addExpense(Expense expense);

    @Update
    public void updateExpense(Expense expense);

    @Delete
    public void deleteExpense(Expense expense);

    @Query("select * from expenses order by expense_id desc")
    public LiveData<List<Expense>> getAllExpenses();
}
