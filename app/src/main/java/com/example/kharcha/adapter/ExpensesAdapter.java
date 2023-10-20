package com.example.kharcha.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kharcha.R;
import com.example.kharcha.database.entity.Expense;

import java.util.List;

public class ExpensesAdapter extends RecyclerView.Adapter<ExpensesAdapter.MyViewHolder> {

    // Variables
    private List<Expense> expenses;

    // ViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView person;
        private TextView date;
        private TextView amount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.title = itemView.findViewById(R.id.title);
            this.person = itemView.findViewById(R.id.person);
            this.date = itemView.findViewById(R.id.date);
            this.amount = itemView.findViewById(R.id.amount);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Expense expense = expenses.get(position);

        holder.title.setText(expense.getTitle());
        holder.person.setText(expense.getPerson());
        holder.date.setText(expense.getDate());
        holder.amount.setText(expense.getAmount());
    }

    @Override
    public int getItemCount() {
        if (expenses == null) {
            return 0;
        }
        return expenses.size();
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
        notifyDataSetChanged();
    }

    public Expense getExpenseAt(int position) {
        return expenses.get(position);
    }
}
