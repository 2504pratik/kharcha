package com.example.kharcha;

import android.app.Application;

public class KharchaApplication extends Application {

    private ExpenseRepository expenseRepository;

    @Override
    public void onCreate() {
        super.onCreate();
        expenseRepository = new ExpenseRepository(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (expenseRepository != null) {
            expenseRepository.shutdownExecutor();
        }
    }

    public ExpenseRepository getExpenseRepository() {
        return expenseRepository;
    }
}