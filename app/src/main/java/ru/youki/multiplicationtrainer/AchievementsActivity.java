package ru.youki.multiplicationtrainer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_achievements);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView achievementsRecyclerView = findViewById(R.id.achievementsRecyclerView);

        SharedPreferences prefs = getSharedPreferences("MultiplicationTrainer", MODE_PRIVATE);
        List<Achievement> achievements = new ArrayList<>();
        achievements.add(new Achievement("10 правильных подряд", "ach_10_consecutive",
                prefs.getBoolean("ach_10_consecutive", false)));
        achievements.add(new Achievement("100% на сложном уровне", "ach_perfect_hard",
                prefs.getBoolean("ach_perfect_hard", false)));
        achievements.add(new Achievement("Ответить на 50 вопросов", "ach_50_questions",
                prefs.getBoolean("ach_50_questions", false)));

        AchievementsAdapter adapter = new AchievementsAdapter(achievements);
        achievementsRecyclerView.setAdapter(adapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private static class Achievement {
        String name;
        String key;
        boolean achieved;

        Achievement(String name, String key, boolean achieved) {
            this.name = name;
            this.key = key;
            this.achieved = achieved;
        }
    }

    private static class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.ViewHolder> {
        private final List<Achievement> achievements;

        AchievementsAdapter(List<Achievement> achievements) {
            this.achievements = achievements;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_achievement, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Achievement achievement = achievements.get(position);
            holder.nameText.setText(achievement.name);
            holder.statusIcon.setImageResource(
                    achievement.achieved ? R.drawable.ic_check : R.drawable.ic_pending);
        }

        @Override
        public int getItemCount() {
            return achievements.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            ImageView statusIcon;

            ViewHolder(View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.achievementName);
                statusIcon = itemView.findViewById(R.id.achievementStatus);
            }
        }
    }
}