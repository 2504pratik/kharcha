package com.example.kharcha;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class BarGraphView extends View {
    private Paint barPaint;
    private Paint textPaint;
    private RectF barRect;

    public BarGraphView(Context context) {
        super(context);
        init();
    }

    public BarGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        barPaint = new Paint();
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(getResources().getColor(R.color.white)); // Set text color to white
        textPaint.setTextSize(48); // Set text size (adjust as needed)

        barRect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int[] expenses = {100, 150, 200, 200, 300, 250, 180}; // Sample weekly expenses
        String[] days = {"S", "M", "T", "W", "T", "F", "S"};

        int dp8 = (int) (8 * getResources().getDisplayMetrics().density); // Convert dp to pixels
        float maxExpense = getMaxValue(expenses);
        float maxHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int totalSpacing = dp8 * (expenses.length - 1); // Total spacing between bars
        float barWidth = (getWidth() - getPaddingStart() - getPaddingEnd() - totalSpacing) / expenses.length;

        for (int i = 0; i < expenses.length; i++) {
            float barHeight = (expenses[i] / maxExpense) * maxHeight;
            float left = i * (barWidth + dp8) + getPaddingStart();
            float top = getHeight() - barHeight - getPaddingBottom();
            float right = left + barWidth;
            float bottom = getHeight() - getPaddingBottom();

            // Draw bars
            barPaint.setColor(getResources().getColor(R.color.secondary)); // Set bar color to secondary color
            barRect.set(left, top, right, bottom);
            canvas.drawRoundRect(barRect, 16, 16, barPaint);

            // Draw day abbreviation below the bar
            float textX = left + (barWidth / 2); // Center the text horizontally within the bar
            float textY = top + barHeight - 20; // Adjust the vertical position of text within the bar
            canvas.drawText(days[i], textX, textY, textPaint);
        }
    }

    private float getMaxValue(int[] values) {
        float max = values[0];
        for (int value : values) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}

