package com.example.toastinterval;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private Spinner intervalSpinner;
    private Button startBtn;
    private Button stopBtn;

    // Main-thread handler for timed, UI-safe tasks
    private final Handler handler = new Handler(Looper.getMainLooper()); // [12]
    private final Random random = new Random();

    // Pool of different messages
    private final List<String> messages = Arrays.asList( // [14]
            "Keep going!",
            "Hydrate and thrive!",
            "Code. Test. Repeat.",
            "You’ve got this!",
            "A tiny toast of joy!",
            "Focus + Flow",
            "Small steps, big gains",
            "Smile—it helps!"
    );

    private int lastIndex = -1;
    private long currentIntervalMs = TimeUnit.SECONDS.toMillis(30); // default demo interval [12]

    // Periodic toast task
    private final Runnable toastTask = new Runnable() {
        @Override
        public void run() {
            int idx;
            do {
                idx = random.nextInt(messages.size());
            } while (messages.size() > 1 && idx == lastIndex);
            lastIndex = idx;

            Toast.makeText(MainActivity.this, messages.get(idx), Toast.LENGTH_SHORT).show(); // [14]

            handler.postDelayed(this, currentIntervalMs); // reschedule [12]
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Montserrat via theme [15]

        intervalSpinner = findViewById(R.id.intervalSpinner);
        startBtn = findViewById(R.id.startBtn);
        stopBtn = findViewById(R.id.stopBtn);

        // 1) Spinner items
        String[] items = new String[]{
                "Every 3 seconds",
                "Every 5 seconds",
                "Every 7 seconds",
                "Every 10 seconds"
        };

// 2) Mapping selection to milliseconds
        startBtn.setOnClickListener(v -> {
            int pos = intervalSpinner.getSelectedItemPosition();
            switch (pos) {
                case 0:
                    currentIntervalMs = 3_000L;  // 3s
                    break;
                case 1:
                    currentIntervalMs = 5_000L;  // 5s
                    break;
                case 2:
                    currentIntervalMs = 7_000L;  // 7s
                    break;
                case 3:
                    currentIntervalMs = 10_000L; // 10s
                    break;
                default:
                    currentIntervalMs = 3_000L;
                    break;
            } // convert to ms and use in postDelayed [6][9]

            handler.removeCallbacks(toastTask);          // reset any previous schedule [1]
            handler.post(toastTask);                     // start immediately; task re-posts itself [2]
            Toast.makeText(MainActivity.this, "Toast interval started", Toast.LENGTH_SHORT).show(); // feedback [2]
        });


        stopBtn.setOnClickListener(v -> {
            handler.removeCallbacks(toastTask); // [12]
            Toast.makeText(MainActivity.this, "Toast interval stopped", Toast.LENGTH_SHORT).show(); // [14]
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(toastTask); // cleanup [12]
    }
}
