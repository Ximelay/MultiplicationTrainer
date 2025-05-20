package ru.youki.multiplicationtrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result);

        TextView resultText = findViewById(R.id.resultText);
        MaterialButton replayButton = findViewById(R.id.replayButton);

        int correct = getIntent().getIntExtra("correct", 0);
        int total = getIntent().getIntExtra("total", 0);
        int correct2 = getIntent().getIntExtra("correct2", 0);
        int total2 = getIntent().getIntExtra("total2", 0);
        int level = getIntent().getIntExtra("level", 0);
        double percentage = total > 0 ? (correct * 100.0 / total) : 0;
        double percentage2 = total2 > 0 ? (correct2 * 100.0 / total2) : 0;

        SharedPreferences prefs = getSharedPreferences("MultiplicationTrainer", MODE_PRIVATE);
        float bestPercentage = prefs.getFloat("bestPercentage_" + level, 0);
        if (percentage > bestPercentage && total > 0) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putFloat("bestPercentage_" + level, (float) percentage);
            editor.apply();
            bestPercentage = (float) percentage;
        }

        saveGameResult(level, percentage, total);

        String resultString = "Игрок 1:\nПравильных ответов: " + correct + " из " + total +
                "\nПроцент успеха: " + String.format("%.1f%%", percentage) +
                "\nЛучший результат (уровень " + getLevelName(level) + "): " + String.format("%.1f%%", bestPercentage);
        if (total2 > 0) {
            resultString += "\n\nИгрок 2:\nПравильных ответов: " + correct2 + " из " + total2 +
                    "\nПроцент успеха: " + String.format("%.1f%%", percentage2);
        }

        resultText.setText(resultString);

        replayButton.setOnClickListener(v -> {
            Intent intent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void saveGameResult(int level, double percentage, int total) {
        if (total == 0) return;
        SharedPreferences prefs = getSharedPreferences("MultiplicationTrainer", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("gameHistory", "[]");
        Type type = new TypeToken<List<GameResult>>(){}.getType();
        List<GameResult> history = gson.fromJson(json, type);
        if (history == null) history = new ArrayList<>();

        GameResult result = new GameResult();
        result.date = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
        result.level = level;
        result.percentage = percentage;
        history.add(0, result);

        if (history.size() > 5) history = history.subList(0, 5);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("gameHistory", gson.toJson(history));
        editor.apply();
    }

    private String getLevelName(int level) {
        if (level == 1) {
            return "Средний";
        } else if (level == 2) {
            return "Сложный";
        }
        return "Легкий";
    }

    private static class GameResult {
        String date;
        int level;
        double percentage;
    }
}