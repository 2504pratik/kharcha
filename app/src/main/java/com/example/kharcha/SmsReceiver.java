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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String message = smsMessage.getMessageBody();

                        // Filter based on sender if necessary
                        if (sender.contains("SBI")) {
                            Expense expense = parseSms(message);
                            if (expense != null) {
                                saveExpense(context, expense);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void processSms(Context context, String message) {
        Expense expense = parseSms(message);
        if (expense != null) {
            saveExpense(context, expense);
        }
    }

    private static Expense parseSms(String message) {
        // Pattern for amount (e.g., Rs.1000)
        Pattern amountPattern = Pattern.compile("Rs\\.(\\d+\\.\\d+|\\d+)");
        Matcher amountMatcher = amountPattern.matcher(message);
        String amount = amountMatcher.find() ? amountMatcher.group(1) : null;

        // Pattern for date (e.g., 17Aug24)
        Pattern datePattern = Pattern.compile("on\\s(\\d{2}[A-Za-z]{3}\\d{2})");
        Matcher dateMatcher = datePattern.matcher(message);
        String date = dateMatcher.find() ? dateMatcher.group(1) : null;

        // Pattern for sender (e.g., John)
        Pattern senderPattern = Pattern.compile("transfer\\sfrom\\s([A-Za-z]+)");
        Matcher senderMatcher = senderPattern.matcher(message);
        String sender = senderMatcher.find() ? senderMatcher.group(1) : null;

        // Check for credited or debited
        boolean isCredited = message.contains("credited");

        if (amount != null && date != null && sender != null) {
            Expense expense = new Expense();
            expense.setTitle("UPI Transaction");
            expense.setAmount(amount);
            expense.setPerson(sender);
            expense.setDate(formatDate(date));
            expense.setOwedByMe(!isCredited);
            return expense;
        }

        return null;
    }

    private static String formatDate(String dateStr) {
        // Convert date from "17Aug24" to your preferred format
        // This is just an example, adjust as needed
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMMyy", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
            Date date = inputFormat.parse(dateStr);
            return outputFormat.format(date);
        } catch (Exception e) {
            Log.e(TAG, "Error formatting date", e);
            return dateStr;
        }
    }

    private static void saveExpense(Context context, Expense expense) {
        ExpenseRepository repository = new ExpenseRepository((Application) context.getApplicationContext());
        repository.addExpense(expense);
    }
}