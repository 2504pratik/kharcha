package com.example.kharcha;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.kharcha.database.entity.Expense;

import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {

    private final ExpenseRepository repository;
    private final LiveData<List<Expense>> allExpenses;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        repository = new ExpenseRepository(application);
        allExpenses = repository.getAllExpenses();
    }

    public void insert(Expense expense) {
        repository.addExpense(expense);
    }

    public void update(Expense expense) {
        repository.updateExpense(expense);
    }

    public void delete(Expense expense) {
        repository.deleteExpense(expense);
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }
}
