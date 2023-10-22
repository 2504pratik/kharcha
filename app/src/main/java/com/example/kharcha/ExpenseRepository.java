package com.example.kharcha;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Pair;

import androidx.lifecycle.LiveData;

import com.example.kharcha.database.ExpenseDAO;
import com.example.kharcha.database.KharchaAppDB;
import com.example.kharcha.database.entity.Expense;

import java.util.List;

public class ExpenseRepository {
    private final ExpenseDAO expenseDAO;
    private final LiveData<List<Expense>> allExpenses;

    public ExpenseRepository(Application application) {
        KharchaAppDB db = KharchaAppDB.getInstance(application);
        expenseDAO = db.getExpenseDAO();
        allExpenses = expenseDAO.getAllExpenses();
    }

    public void addExpense(Expense expense) {
        new InsertExpenseAsyncTask(expenseDAO).execute(expense);
    }

    public void updateExpense(Expense expense) {
        new UpdateExpenseAsyncTask(expenseDAO).execute(expense);
    }

    public void deleteExpense(Expense expense) {
        new DeleteExpenseAsyncTask(expenseDAO).execute(expense);
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    private static class InsertExpenseAsyncTask extends AsyncTask<Expense,Void,Void> {
        private final ExpenseDAO expenseDAO;

        private InsertExpenseAsyncTask(ExpenseDAO expenseDAO) {
            this.expenseDAO = expenseDAO;
        }

        @Override
        protected Void doInBackground(Expense... expenses) {
            expenseDAO.addExpense(expenses[0]);
            return null;
        }
    }

    private static class UpdateExpenseAsyncTask extends AsyncTask<Expense,Void,Void> {
        private final ExpenseDAO expenseDAO;

        private UpdateExpenseAsyncTask(ExpenseDAO expenseDAO) {
            this.expenseDAO = expenseDAO;
        }

        @Override
        protected Void doInBackground(Expense... expenses) {
            expenseDAO.updateExpense(expenses[0]);
            return null;
        }
    }

    private static class DeleteExpenseAsyncTask extends AsyncTask<Expense,Void,Void> {
        private final ExpenseDAO expenseDAO;

        private DeleteExpenseAsyncTask(ExpenseDAO expenseDAO) {
            this.expenseDAO = expenseDAO;
        }

        @Override
        protected Void doInBackground(Expense... expenses) {
            expenseDAO.deleteExpense(expenses[0]);
            return null;
        }
    }
}
