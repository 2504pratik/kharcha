package com.example.kharcha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kharcha.adapter.ExpensesAdapter;
import com.example.kharcha.database.entity.Expense;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddEditExpense extends AppCompatActivity {
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_AMOUNT = "EXTRA_AMOUNT";
    public static final String EXTRA_PERSON = "EXTRA_PERSON";
    public static final String EXTRA_DATE = "EXTRA_DATE";
    public static final String EXTRA_BOOLEAN = "EXTRA_BOOLEAN";

    private TextInputEditText titleInputET;
    private TextInputEditText amountInputET;
    private TextInputEditText personInputET;
    private TextView selectedDate;
    private Switch owedBySwitch;
    private long selectedDateInMillis = MaterialDatePicker.todayInUtcMilliseconds();

    Expense expense = new Expense();
    ExpensesAdapter adapter = new ExpensesAdapter();

    private boolean dataChanged = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Text Input Fields
        TextInputLayout titleInputLayout = findViewById(R.id.titleInputLayout);
        titleInputET = titleInputLayout.findViewById(R.id.titleTextField);

        TextInputLayout amountInputLayout = findViewById(R.id.amountInputLayout);
        amountInputET = amountInputLayout.findViewById(R.id.amountTextField);

        TextInputLayout personInputLayout = findViewById(R.id.personInputLayout);
        personInputET = personInputLayout.findViewById(R.id.personTextField);

        // Add TextChangedListeners to your input fields to detect changes
        titleInputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        amountInputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        personInputET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataChanged = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        // Date picker
        ImageView datePicker = findViewById(R.id.calendar);
        selectedDate = findViewById(R.id.selectedDate);

        datePicker.setOnClickListener(v -> {
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Choose a date")
                    .setSelection(selectedDateInMillis)
                    .build();

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                selectedDateInMillis = selection;
                String dateSelected = new SimpleDateFormat("MMM d", Locale.getDefault()).format(new Date(selection));
                selectedDate.setText(dateSelected);
            });

            materialDatePicker.show(getSupportFragmentManager(),"tag");
        });

        // Switch
        owedBySwitch = findViewById(R.id.owedSwitch);


        // Cancel Button
        ImageView cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED); // Set result as canceled
            finish(); // Finish the activity
        });

        // Save button
        MaterialButton saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(v -> {
            String date = selectedDate.getText().toString();
            saveExpense(date);
        });

        // Edit an expense part
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            int id = intent.getIntExtra(EXTRA_ID, -1);
            if (id != -1) {
                titleInputET.setText(intent.getStringExtra(EXTRA_TITLE));
                amountInputET.setText(intent.getStringExtra(EXTRA_AMOUNT));
                personInputET.setText(intent.getStringExtra(EXTRA_PERSON));
                selectedDate.setText(intent.getStringExtra(EXTRA_DATE));
                owedBySwitch.setChecked(intent.getBooleanExtra(EXTRA_BOOLEAN,true));
            }
        }
    }

    // Saving the expense
    private void saveExpense(String selectedDate) {
        String titleInput = titleInputET.getText().toString();
        expense.setTitle(titleInput);

        String amountInput = amountInputET.getText().toString();
        // Validate amount input
        if (!isNumeric(amountInput)) {
            Toast.makeText(this, "Invalid amount input", Toast.LENGTH_SHORT).show();
            return;
        }
        expense.setAmount(amountInput);

        String personInput = personInputET.getText().toString();
        expense.setPerson(personInput);

        expense.setDate(selectedDate);

        Boolean owedByMe = owedBySwitch.isChecked();
        expense.setOwedByMe(owedByMe);

        if (titleInput.trim().isEmpty() || amountInput.trim().isEmpty() || personInput.trim().isEmpty()) {
            Toast.makeText(this, "Enter the required input", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE,titleInput);
        data.putExtra(EXTRA_AMOUNT,amountInput);
        data.putExtra(EXTRA_PERSON,personInput);
        data.putExtra(EXTRA_DATE,selectedDate);
        data.putExtra(EXTRA_BOOLEAN,owedByMe);

        int id = getIntent().getIntExtra(EXTRA_ID,-1);
        if(id != -1) {
            data.putExtra(EXTRA_ID,id);
            adapter.notifyDataSetChanged();
        }

        setResult(RESULT_OK,data);
        finish();
    }

    // Validation method for numeric input
    private boolean isNumeric(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}