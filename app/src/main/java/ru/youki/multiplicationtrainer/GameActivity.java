package ru.youki.multiplicationtrainer;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private TextView questionText, timerText;
    private EditText answerInput;
    private Button submitButton;
    private int correctAnswers = 0, totalQuestions = 0;
    private int num1, num2, correctAnswer;
    private CountDownTimer timer;
    private int maxNumber;
    private Ringtone correctSound, wrongSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        questionText = findViewById(R.id.questionText);
        timerText = findViewById(R.id.timerText);
        answerInput = findViewById(R.id.answerInput);
        submitButton = findViewById(R.id.submitButton);

        Uri correctUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri wrongUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        correctSound = RingtoneManager.getRingtone(this, correctUri);
        wrongSound = RingtoneManager.getRingtone(this, wrongUri);

        int level = getIntent().getIntExtra("level", 0);
        if (level == 1) {
            maxNumber = 15;
            // Средний
        } else if (level == 2) {
            maxNumber = 20;
            // Сложный
        } else {
            maxNumber = 10;
            // Легкий
        }

        generateQuestion();
        startTimer();

        submitButton.setOnClickListener(v -> checkAnswer());
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

        totalQuestions++;
        int userAnswer = Integer.parseInt(input);
        if (userAnswer == correctAnswer) {
            correctAnswers++;
            Toast.makeText(this, "Правильно!", Toast.LENGTH_SHORT).show();
            if (correctSound != null) correctSound.play();
        } else {
            Toast.makeText(this, "Неправильно", Toast.LENGTH_SHORT).show();
            if (wrongSound != null) wrongSound.play();
        }
        generateQuestion();
    }

    private void startTimer() {
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText("Осталось: " + millisUntilFinished / 1000 + " сек");
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(GameActivity.this, ResultActivity.class);
                intent.putExtra("correct", correctAnswers);
                intent.putExtra("total", totalQuestions);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        }.start();
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