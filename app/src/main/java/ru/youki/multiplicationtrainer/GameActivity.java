package ru.youki.multiplicationtrainer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private TextView questionText, timerText, playerText;
    private EditText answerInput;
    private Button submitButton;
    private ProgressBar timerProgress;
    private int correctAnswers = 0, totalQuestions = 0;
    private int correctAnswersPlayer2 = 0, totalQuestionsPlayer2 = 0;
    private int num1, num2, correctAnswer;
    private CountDownTimer timer;
    private int maxNumber;
    private Ringtone correctSound, wrongSound;
    private String mode;
    private int players;
    private boolean isPlayer1Turn = true;
    private int consecutiveCorrect = 0;
    private int totalAnswered = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);

        questionText = findViewById(R.id.questionText);
        timerText = findViewById(R.id.timerText);
        playerText = findViewById(R.id.playerText);
        answerInput = findViewById(R.id.answerInput);
        submitButton = findViewById(R.id.submitButton);
        timerProgress = findViewById(R.id.timerProgress);

        Uri correctUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri wrongUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        correctSound = RingtoneManager.getRingtone(this, correctUri);
        wrongSound = RingtoneManager.getRingtone(this, wrongUri);

        int level = getIntent().getIntExtra("level", 0);
        mode = getIntent().getStringExtra("mode");
        players = getIntent().getIntExtra("players", 1);
        if (mode == null) mode = "game"; // Защита от null

        switch (level) {
            case 1:
                maxNumber = 15;
                break;
            case 2:
                maxNumber = 20;
                break;
            default:
                maxNumber = 10;
                break;
        }

        if (players == 2) {
            playerText.setText("Игрок 1");
        } else {
            playerText.setText("");
        }

        generateQuestion();
        if (mode.equals("game")) {
            startTimer();
            timerProgress.setMax(60);
            timerProgress.setProgress(60);
        } else {
            timerText.setText("Режим обучения");
            submitButton.setText("Далее");
            timerProgress.setVisibility(ProgressBar.GONE);
        }

        submitButton.setOnClickListener(v -> checkAnswer());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void generateQuestion() {
        Random rand = new Random();
        num1 = rand.nextInt(maxNumber) + 1;
        num2 = rand.nextInt(maxNumber) + 1;
        correctAnswer = num1 * num2;
        questionText.setText(num1 + " × " + num2 + " = ");
        answerInput.setText("");
    }

    private void checkAnswer() {
        String input = answerInput.getText().toString();
        if (input.isEmpty()) {
            Toast.makeText(this, "Введите ответ", Toast.LENGTH_SHORT).show();
            return;
        }

        int userAnswer = Integer.parseInt(input);
        if (isPlayer1Turn || players == 1) {
            totalQuestions++;
        } else {
            totalQuestionsPlayer2++;
        }
        totalAnswered++;

        if (userAnswer == correctAnswer) {
            if (isPlayer1Turn || players == 1) {
                correctAnswers++;
            } else {
                correctAnswersPlayer2++;
            }
            consecutiveCorrect++;
            Toast.makeText(this, "Правильно!", Toast.LENGTH_SHORT).show();
            if (correctSound != null) correctSound.play();
            checkAchievements();
        } else {
            consecutiveCorrect = 0;
            String message = mode.equals("training") ?
                    "Неправильно, правильный ответ: " + correctAnswer : "Неправильно";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            if (wrongSound != null) wrongSound.play();
            if (mode.equals("training")) {
                return; // В режиме обучения не генерируем новый вопрос
            }
        }

        if (players == 2) {
            isPlayer1Turn = !isPlayer1Turn;
            playerText.setText(isPlayer1Turn ? "Игрок 1" : "Игрок 2");
        }

        generateQuestion();
    }

    private void startTimer() {
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsLeft = (int) (millisUntilFinished / 1000);
                timerText.setText("Осталось: " + secondsLeft + " сек");
                timerProgress.setProgress(secondsLeft);
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                intent.putExtra("correct", correctAnswers);
                intent.putExtra("total", totalQuestions);
                intent.putExtra("correct2", correctAnswersPlayer2);
                intent.putExtra("total2", totalQuestionsPlayer2);
                intent.putExtra("level", getIntent().getIntExtra("level", 0));
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }.start();
    }

    private void checkAchievements() {
        SharedPreferences prefs = getSharedPreferences("MultiplicationTrainer", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int level = getIntent().getIntExtra("level", 0);

        if (consecutiveCorrect >= 10 && !prefs.getBoolean("ach_10_consecutive", false)) {
            editor.putBoolean("ach_10_consecutive", true);
            Toast.makeText(this, "Достижение: 10 правильных подряд!", Toast.LENGTH_LONG).show();
        }

        if (level == 2 && totalQuestions >= 5 && correctAnswers == totalQuestions &&
                !prefs.getBoolean("ach_perfect_hard", false)) {
            editor.putBoolean("ach_perfect_hard", true);
            Toast.makeText(this, "Достижение: 100% на сложном уровне!", Toast.LENGTH_LONG).show();
        }

        if (totalAnswered >= 50 && !prefs.getBoolean("ach_50_questions", false)) {
            editor.putBoolean("ach_50_questions", true);
            Toast.makeText(this, "Достижение: Ответить на 50 вопросов!", Toast.LENGTH_LONG).show();
        }

        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (correctSound != null) {
            correctSound.stop();
        }
        if (wrongSound != null) {
            wrongSound.stop();
        }
    }
}