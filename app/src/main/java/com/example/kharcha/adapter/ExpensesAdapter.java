package com.example.kharcha.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kharcha.R;
import com.example.kharcha.database.entity.Expense;

import java.util.ArrayList;
import java.util.List;

public class ExpensesAdapter extends ListAdapter<Expense ,ExpensesAdapter.MyViewHolder> {

    public ExpensesAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Expense> DIFF_CALLBACK = new DiffUtil.ItemCallback<Expense>() {
        @Override
        public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getAmount().equals(newItem.getAmount()) &&
                    oldItem.getPerson().equals(newItem.getPerson()) &&
                    oldItem.getDate().equals(newItem.getDate());
        }
    };

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
        Expense expense = getItem(position);
        int colorId = expense.getOwedByMe() ? R.color.greenSoft : R.color.redSoft;

        holder.title.setText(expense.getTitle());
        holder.person.setText(expense.getPerson());
        holder.date.setText(expense.getDate());
        holder.amount.setText(expense.getAmount());

        holder.amount.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),colorId));
    }

    public Expense getExpenseAt(int position) {
        return getItem(position);
    }
}
