package com.example.kharcha;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kharcha.database.entity.Expense;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.sql.Date;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AddExpense extends AppCompatActivity {
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_AMOUNT = "EXTRA_AMOUNT";
    public static final String EXTRA_PERSON = "EXTRA_PERSON";
    public static final String EXTRA_DATE = "EXTRA_DATE";
    public static final String EXTRA_BOOLEAN = "EXTRA_BOOLEAN";

    private TextInputEditText titleInputET;
    private TextInputEditText amountInputET;
    private TextInputEditText personInputET;
    private ImageView datePicker;
    private TextView selectedDate;
    private Switch owedBySwitch;
    private long selectedDateInMillis = MaterialDatePicker.todayInUtcMilliseconds();

    Expense expense = new Expense();

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

        // Date picker
        datePicker = findViewById(R.id.calendar);
        selectedDate = findViewById(R.id.selectedDate);

        datePicker.setOnClickListener(v -> {
            MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Choose a date")
                    .setSelection(selectedDateInMillis)
                    .build();

            materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                selectedDateInMillis = selection;
                String date = new SimpleDateFormat("MMM d", Locale.getDefault()).format(new Date(selection));
                selectedDate.setText(date);
            });

            materialDatePicker.show(getSupportFragmentManager(),"tag");
        });

        // Switch
        owedBySwitch = findViewById(R.id.owedSwitch);

        // Cancel Button
        ImageView cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> finish());

        // Save button
        MaterialButton saveBtn = findViewById(R.id.saveBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = selectedDate.getText().toString();
                saveExpense(date);
            }
        });
    }

    private void saveExpense(String selectedDate) {
        String titleInput = titleInputET.getText().toString();
        expense.setTitle(titleInput);

        String amountInput = amountInputET.getText().toString();
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

        setResult(RESULT_OK,data);
        finish();
    }
}