package com.example.kharcha.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.kharcha.database.entity.Expense;

@Database(entities = {Expense.class}, version = 1)
public abstract class KharchaAppDB extends RoomDatabase {

    private static KharchaAppDB instance;
    public abstract ExpenseDAO getExpenseDAO();

    public static synchronized KharchaAppDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    KharchaAppDB.class,"kharcha_db")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallBack)
                    .build();
        }
        return instance;
    }

    private static final RoomDatabase.Callback roomCallBack = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDBAsyncTask(instance).execute();
        }
    };

    private static class PopulateDBAsyncTask extends AsyncTask<Void, Void,Void> {
        private final ExpenseDAO expenseDAO;

        private PopulateDBAsyncTask(KharchaAppDB db) {
            expenseDAO = db.getExpenseDAO();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            expenseDAO.addExpense(new Expense("title","amount","person",true,"date"));
            return null;
        }
    }
}
