package com.example.kharcha;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kharcha.adapter.ExpensesAdapter;
import com.example.kharcha.database.entity.Expense;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_EXPENSE_REQUEST = 1;
    private ExpenseViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        ExpensesAdapter adapter = new ExpensesAdapter();
        recyclerView.setAdapter(adapter );

        viewModel = ViewModelProviders.of(this).get(ExpenseViewModel.class);
        viewModel.getAllExpenses().observe(this, expenses -> {
            // Update recyclerview
            adapter.setExpenses(expenses );
        });

        ImageView editBtn = findViewById(R.id.editBtn);
        editBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this,AddExpense.class);
            startActivityForResult(intent,ADD_EXPENSE_REQUEST);
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                viewModel.delete(adapter.getExpenseAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "An expense deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EXPENSE_REQUEST && resultCode == RESULT_OK) {
            String title = data.getStringExtra(AddExpense.EXTRA_TITLE);
            String amount = data.getStringExtra(AddExpense.EXTRA_AMOUNT);
            String date = data.getStringExtra(AddExpense.EXTRA_DATE);
            String person = data.getStringExtra(AddExpense.EXTRA_PERSON);
            Boolean owedByMe = data.getBooleanExtra(AddExpense.EXTRA_BOOLEAN,true);

            Expense expense = new Expense(title,amount,person,owedByMe,date);
            viewModel.insert(expense);

            Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Expense not added", Toast.LENGTH_SHORT).show();
        }
    }
}