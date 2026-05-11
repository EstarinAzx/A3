package nz.ac.ara.bcde223.minimala3skeleton.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import nz.ac.ara.bcde223.minimala3skeleton.R;
import nz.ac.ara.bcde223.minimala3skeleton.viewmodel.GameViewModel;

public class MainActivity extends AppCompatActivity {

    private GameViewModel viewModel;

    private TextView scoreText;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModel
        viewModel = new GameViewModel();

        // Bind UI
        scoreText = findViewById(R.id.scoreText);
        Button increaseButton = findViewById(R.id.increaseButton);
        resetButton = findViewById(R.id.resetButton);

        // Set listeners
        increaseButton.setOnClickListener(v -> {
            viewModel.onIncreaseScore();
            updateView();
        });

        resetButton.setOnClickListener(v -> {
            viewModel.onResetScore();
            updateView();
        });

        // Initial UI state
        updateView();
    }

    private void updateView() {
        scoreText.setText(viewModel.getScoreText());
        resetButton.setEnabled(viewModel.isResetEnabled());
    }
}