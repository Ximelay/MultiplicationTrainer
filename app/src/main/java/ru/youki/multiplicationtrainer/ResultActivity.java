package ru.youki.multiplicationtrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView resultText = findViewById(R.id.resultText);
        Button replayButton = findViewById(R.id.replayButton);

        int correct = getIntent().getIntExtra("correct", 0);
        int total = getIntent().getIntExtra("total", 0);
        double percentage = total > 0 ? (correct * 100.0 / total) : 0;

        // Сохранение лучшего результата
        SharedPreferences prefs = getSharedPreferences("MultiplicationTrainer", MODE_PRIVATE);
        float bestPercentage = prefs.getFloat("bestPercentage", 0);
        if (percentage > bestPercentage && total > 0) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("bestPercentage", (float) percentage);
            editor.apply();
            bestPercentage = (float) percentage;
        }

        resultText.setText("Правильных ответов: " + correct + " из " + total +
                "\nПроцент успеха: " + String.format("%.1f%%", percentage) +
                "\nЛучший результат: " + String.format("%.1f%%", bestPercentage));

        replayButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, GameActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });
    }
}