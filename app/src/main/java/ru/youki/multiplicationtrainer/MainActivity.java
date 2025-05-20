package ru.youki.multiplicationtrainer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import com.google.android.material.button.MaterialButton;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Spinner levelSpinner = findViewById(R.id.levelSpinner);
        ArrayAdapter<CharSequence> levelAdapter = ArrayAdapter.createFromResource(this,
                R.array.levels_array, android.R.layout.simple_spinner_dropdown_item);
        levelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        levelSpinner.setAdapter(levelAdapter);

        Spinner modeSpinner = findViewById(R.id.modeSpinner);
        ArrayAdapter<CharSequence> modeAdapter = ArrayAdapter.createFromResource(this,
                R.array.modes_array, android.R.layout.simple_spinner_dropdown_item);
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);

        Spinner playerSpinner = findViewById(R.id.playerSpinner);
        ArrayAdapter<CharSequence> playerAdapter = ArrayAdapter.createFromResource(this,
                R.array.players_array, android.R.layout.simple_spinner_dropdown_item);
        playerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playerSpinner.setAdapter(playerAdapter);

        MaterialButton startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            intent.putExtra("level", levelSpinner.getSelectedItemPosition());
            intent.putExtra("mode", modeSpinner.getSelectedItemPosition() == 0 ? "game" : "training");
            intent.putExtra("players", playerSpinner.getSelectedItemPosition() == 0 ? 1 : 2);
            startActivity(intent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        MaterialButton statsButton = findViewById(R.id.statsButton);
        statsButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        MaterialButton achievementsButton = findViewById(R.id.achievementsButton);
        achievementsButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AchievementsActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}