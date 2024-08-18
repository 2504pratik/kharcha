package com.example.kharcha;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.kharcha.database.ExpenseDAO;
import com.example.kharcha.database.KharchaAppDB;
import com.example.kharcha.database.entity.Expense;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExpenseRepository {
    private final ExpenseDAO expenseDAO;
    private final LiveData<List<Expense>> allExpenses;
    private final ExecutorService executorService;

    public ExpenseRepository(Application application) {
        KharchaAppDB db = KharchaAppDB.getInstance(application);
        expenseDAO = db.getExpenseDAO();
        allExpenses = expenseDAO.getAllExpenses();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void addExpense(Expense expense) {
        executorService.execute(() -> {
            Expense existingExpense = expenseDAO.findDuplicateExpense(expense.getTitle(), expense.getAmount(), expense.getDate());
            if (existingExpense == null) {
                long id = expenseDAO.addExpense(expense);
                if (id == -1) {
                    System.out.println("Something went wrong");
                }
            }
        });
    }

    public void updateExpense(Expense expense) {
        executorService.execute(() -> expenseDAO.updateExpense(expense));
    }

    public void deleteExpense(Expense expense) {
        executorService.execute(() -> expenseDAO.deleteExpense(expense));
    }

    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    public void shutdownExecutor() {
        executorService.shutdown();
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
