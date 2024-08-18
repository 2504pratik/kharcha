package com.example.kharcha;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.kharcha.adapter.ExpensesAdapter;
import com.example.kharcha.database.entity.Expense;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_EXPENSE_REQUEST = 1;
    public static final int EDIT_EXPENSE_REQUEST = 2;

    private ExpenseViewModel viewModel;
    private ExpenseRepository repository;

    private double totalNegativeMoney = 0;
    private double totalPositiveMoney = 0;

    private static final int SMS_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Kharcha);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = ((KharchaApplication) getApplication()).getExpenseRepository();

        // Setting user name
        TextView userName = findViewById(R.id.nameTv);

        // Profile Image
        ImageView profileImg = findViewById(R.id.profileImg);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            // Get the user's display name
            String displayName = user.getDisplayName();

            // Extract the first name from the display name
            String firstName;
            if (displayName != null && !displayName.isEmpty()) {
                String[] nameParts = displayName.split("\\s+");
                firstName = nameParts[0];
                userName.setText(firstName);
            }

            // Get the user's profile URL
            Uri profileUri = user.getPhotoUrl();

            // To make the image circular
            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CircleCrop());

            // Fetch the profile image and add to imageview
            if (profileUri != null) {
                Glide.with(this)
                        .load(profileUri)
                        .apply(requestOptions)
                        .into(profileImg);
            }
        }

        // Request SMS permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
        } else {
            readPastWeekSMS();
        }

        // Recyclerview
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        ExpensesAdapter adapter = new ExpensesAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this).get(ExpenseViewModel.class);
        // Update recyclerview
        TextView negativeMoney = findViewById(R.id.negativeMoney);
        TextView positiveMoney = findViewById(R.id.positiveMoney);
        viewModel.getAllExpenses().observe(this, expenses -> {
            adapter.submitList(expenses);

            // Calculate total expenses
            totalNegativeMoney = 0;
            totalPositiveMoney = 0;
            for (Expense expense : expenses) {
                // Validate amount input
                if (!isNumeric(expense.getAmount())) {
                    Toast.makeText(this, "Invalid amount input", Toast.LENGTH_SHORT).show();
                    return;
                }
                double amount = Double.parseDouble(expense.getAmount());

                if (expense.getOwedByMe()) {
                    totalPositiveMoney += amount;
                } else {
                    totalNegativeMoney -= amount;
                }
            }

            // Update the TextViews with total expenses
            negativeMoney.setText(String.valueOf(totalNegativeMoney));
            positiveMoney.setText(String.valueOf(totalPositiveMoney));
        });

        // Expense add button
        ImageView addBtn = findViewById(R.id.addBtn);
        addBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditExpense.class);
            startActivityForResult(intent,ADD_EXPENSE_REQUEST);
        });

        // List to save latest deleted expense
        final Expense[] deletedExpense = {new Expense()};

        // Swipe functionality of recyclerview card or item
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                switch (direction){
                    case ItemTouchHelper.LEFT:
                        // to delete the expense and restore it through undo
                        final int deletedIndex = position;
                        final Expense deletedItem = adapter.getExpenseAt(deletedIndex);

                        // Delete the item from the ViewModel
                        deletedExpense[0] = deletedItem;
                        viewModel.delete(deletedItem);

                        // Show Snack bar with Undo option
                        Snackbar.make(recyclerView, "An expense deleted", Snackbar.LENGTH_LONG)
                                .setAction("Undo", v -> {
                                    // Restore the deleted item
                                    repository.addExpense(deletedItem);

                                    // Update the list using submitList to trigger a proper diff update
                                    List<Expense> currentList = new ArrayList<>(adapter.getCurrentList());
                                    currentList.add(deletedIndex, deletedItem);
                                    adapter.submitList(currentList);

                                    // Show a message indicating the expense has been restored
                                    Toast.makeText(MainActivity.this, "Expense restored", Toast.LENGTH_SHORT).show();
                                })
                                .show();
                        break;
                    case ItemTouchHelper.RIGHT:
                        // to edit the expense
                        Expense expense = adapter.getExpenseAt(position);
                        Intent intent = new Intent(MainActivity.this, AddEditExpense.class);
                        intent.putExtra(AddEditExpense.EXTRA_ID,expense.getId());
                        intent.putExtra(AddEditExpense.EXTRA_TITLE,expense.getTitle());
                        intent.putExtra(AddEditExpense.EXTRA_AMOUNT,expense.getAmount());
                        intent.putExtra(AddEditExpense.EXTRA_PERSON,expense.getPerson());
                        intent.putExtra(AddEditExpense.EXTRA_BOOLEAN,expense.getOwedByMe());
                        intent.putExtra(AddEditExpense.EXTRA_DATE,expense.getDate());
                        startActivityForResult(intent, EDIT_EXPENSE_REQUEST);
                        break;
                }
            }

            // For background while swiping a recyclerview item
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.redSoft))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.greenSoft))
                        .addSwipeRightActionIcon(R.drawable.ic_edit)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted", Toast.LENGTH_SHORT).show();
                readPastWeekSMS();
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readPastWeekSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_CODE);
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        long oneWeekAgo = calendar.getTimeInMillis();

        Uri uri = Uri.parse("content://sms/inbox");
        String[] projection = new String[]{"address", "body", "date"};
        String selection = "date > ?";
        String[] selectionArgs = new String[]{String.valueOf(oneWeekAgo)};

        try (Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, "date DESC")) {
            if (cursor != null && cursor.moveToFirst()) {
                SmsReceiver smsReceiver = new SmsReceiver();
                do {
                    String sender = cursor.getString(cursor.getColumnIndexOrThrow("address"));
                    String message = cursor.getString(cursor.getColumnIndexOrThrow("body"));

                    if (sender.contains("SBI")) {
                        smsReceiver.processSms(this, message);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("MainActivity", "Error reading SMS", e);
        }
    }

    // Adding new expense or updating an expense
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EXPENSE_REQUEST && resultCode == RESULT_OK) {
            // For adding an expense
            assert data != null;
            String title = data.getStringExtra(AddEditExpense.EXTRA_TITLE);
            String amount = data.getStringExtra(AddEditExpense.EXTRA_AMOUNT);
            String date = data.getStringExtra(AddEditExpense.EXTRA_DATE);
            String person = data.getStringExtra(AddEditExpense.EXTRA_PERSON);
            Boolean owedByMe = data.getBooleanExtra(AddEditExpense.EXTRA_BOOLEAN,true);

            Expense expense = new Expense(title,amount,person,owedByMe,date);
            viewModel.insert(expense);

            Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_EXPENSE_REQUEST && resultCode == RESULT_OK) {
            // For editing an expense
            assert data != null;
            int id = data.getIntExtra(AddEditExpense.EXTRA_ID,-1);

            if( id == -1) {
                Toast.makeText(this, "Expense can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String title = data.getStringExtra(AddEditExpense.EXTRA_TITLE);
            String amount = data.getStringExtra(AddEditExpense.EXTRA_AMOUNT);
            String date = data.getStringExtra(AddEditExpense.EXTRA_DATE);
            String person = data.getStringExtra(AddEditExpense.EXTRA_PERSON);
            Boolean owedByMe = data.getBooleanExtra(AddEditExpense.EXTRA_BOOLEAN,true);

            Expense expense = new Expense(title,amount,person,owedByMe,date);
            expense.setId(id);
            viewModel.update(expense);

            Toast.makeText(this, "Expense updated", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_EXPENSE_REQUEST && resultCode == RESULT_CANCELED) {
            // Handle the case when editing is canceled
            Toast.makeText(this, "Edit canceled", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "Expense not added", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isNumeric(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}