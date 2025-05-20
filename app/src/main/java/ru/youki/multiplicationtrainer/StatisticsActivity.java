package ru.youki.multiplicationtrainer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class StatisticsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_statistics);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        TextView bestStatsText = findViewById(R.id.bestStatsText);
        TextView avgStatsText = findViewById(R.id.avgStatsText);
        RecyclerView historyRecyclerView = findViewById(R.id.historyRecyclerView);

        SharedPreferences prefs = getSharedPreferences("MultiplicationTrainer", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("gameHistory", "[]");
        Type type = new TypeToken<List<GameResult>>(){}.getType();
        List<GameResult> history = gson.fromJson(json, type);
        if (history == null) history = new ArrayList<>();

        float bestEasy = prefs.getFloat("bestPercentage_0", 0);
        float bestMedium = prefs.getFloat("bestPercentage_1", 0);
        float bestHard = prefs.getFloat("bestPercentage_2", 0);
        bestStatsText.setText(String.format("Легкий: %.1f%%\nСредний: %.1f%%\nСложный: %.1f%%",
                bestEasy, bestMedium, bestHard));

        double totalPercentage = 0;
        int count = 0;
        for (GameResult result : history) {
            totalPercentage += result.percentage;
            count++;
        }
        avgStatsText.setText(count > 0 ? String.format("%.1f%%", totalPercentage / count) : "0.0%");

        HistoryAdapter adapter = new HistoryAdapter(history);
        historyRecyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private String getLevelName(int level) {
        switch (level) {
            case 1:
                return "Средний";
            case 2:
                return "Сложный";
            default:
                return "Легкий";
        }
    }

    private static class GameResult {
        String date;
        int level;
        double percentage;
    }

    private static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private final List<GameResult> history;

        HistoryAdapter(List<GameResult> history) {
            this.history = history;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            GameResult result = history.get(position);
            holder.text1.setText(String.format("Дата: %s, Уровень: %s", result.date, getLevelName(result.level)));
            holder.text2.setText(String.format("Процент: %.1f%%", result.percentage));
        }

        @Override
        public int getItemCount() {
            return history.size();
        }

        private String getLevelName(int level) {
            switch (level) {
                case 1:
                    return "Средний";
                case 2:
                    return "Сложный";
                default:
                    return "Легкий";
            }
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;

            ViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}