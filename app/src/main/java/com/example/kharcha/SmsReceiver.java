package com.example.kharcha;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.example.kharcha.database.entity.Expense;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "SMS received");
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String message = smsMessage.getMessageBody();

                        // Process the SMS if it's from a banking service
                        if (isBankingSms(message)) {
                            processSms(context, message);
                        }
                    }
                }
            }
        }
    }

    private boolean isBankingSms(String message) {
        String lowerCaseMessage = message.toLowerCase();
        return lowerCaseMessage.contains("a/c") ||
                lowerCaseMessage.contains("account") ||
                lowerCaseMessage.contains("credited") ||
                lowerCaseMessage.contains("debited") ||
                lowerCaseMessage.contains("upi user");
    }

    void processSms(Context context, String message) {
        Expense expense = parseSms(message);
        if (expense != null) {
            saveExpense(context, expense);
        }
    }

    private Expense parseSms(String message) {
        String amount = extractAmount(message);
        String date = extractDate(message);
        String person = extractPerson(message);
        boolean isCredited = isCredited(message);

        if (amount != null && date != null) {
            Expense expense = new Expense();
            expense.setTitle("UPI");
            expense.setAmount(amount);
            expense.setPerson(person != null ? person : "UPI Transaction");
            expense.setDate(formatDate(date));
            expense.setOwedByMe(isCredited);
            return expense;
        }

        return null;
    }

    private String extractAmount(String message) {
        if (message.toLowerCase().contains("debited")) {
            // Regex for 'debited by' pattern
            Pattern debitedPattern = Pattern.compile("debited by (\\d+(?:\\.\\d+)?)");
            Matcher matcher = debitedPattern.matcher(message);
            return matcher.find() ? matcher.group(1) : null;
        } else if (message.toLowerCase().contains("credited")) {
            // Regex for 'credited by' pattern
            Pattern creditedPattern = Pattern.compile("credited by (?:Rs\\.)?\\s*(\\d+(?:\\.\\d+)?)");
            Matcher matcher = creditedPattern.matcher(message);
            return matcher.find() ? matcher.group(1) : null;
        }
        return "Not Valid";
    }

    private String extractDate(String message) {
        Pattern pattern = Pattern.compile("\\b(\\d{2}[A-Za-z]{3}\\d{2})\\b");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String date = matcher.group(1);
            Log.d(TAG, "Extracted date: " + date);
            return date;
        }
        Log.d(TAG, "No date found in the message");
        return null;
    }

    private String extractPerson(String message) {
        // Pattern to match names that may include spaces, hyphens, and dots
        Pattern pattern = Pattern.compile("(?i)(?:from|to)\\s([A-Z][A-Za-z.\\s-]+?)(?:\\s(?:Ref|UPI|VPA|transfer|A/C|X\\d+|\\d{10,12}|\\(UPI\\))|\\.?$)");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String name = Objects.requireNonNull(matcher.group(1)).trim();
            // Remove any trailing punctuation
            name = name.replaceAll("[.\\s]+$", "");
            return name;
        }
        return null;
    }

    private boolean isCredited(String message) {
        return message.toLowerCase().contains("credited");
    }

    private String formatDate(String dateStr) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyy", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMMM d", Locale.US);
            Date date = inputFormat.parse(dateStr);
            assert date != null;
            return outputFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date", e);
            return dateStr;
        }
    }

    private void saveExpense(Context context, Expense expense) {
        ExpenseRepository repository = new ExpenseRepository((Application) context.getApplicationContext());
        repository.addExpense(expense);
        Log.d(TAG, "Expense saved: " + expense.getTitle() + " - " + expense.getAmount());
    }
}