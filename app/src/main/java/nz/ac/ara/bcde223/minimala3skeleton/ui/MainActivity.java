package nz.ac.ara.bcde223.minimala3skeleton.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import nz.ac.ara.bcde223.minimala3skeleton.R;
import nz.ac.ara.bcde223.minimala3skeleton.model.Direction;
import nz.ac.ara.bcde223.minimala3skeleton.model.Message;
import nz.ac.ara.bcde223.minimala3skeleton.viewmodel.GameViewModel;

public class MainActivity extends AppCompatActivity {

    private GameViewModel viewModel;
    private TextView statusText;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new GameViewModel();

        rootView = findViewById(R.id.main);
        statusText = findViewById(R.id.statusText);

        findViewById(R.id.upButton).setOnClickListener(v -> handleMove(Direction.UP));
        findViewById(R.id.downButton).setOnClickListener(v -> handleMove(Direction.DOWN));
        findViewById(R.id.leftButton).setOnClickListener(v -> handleMove(Direction.LEFT));
        findViewById(R.id.rightButton).setOnClickListener(v -> handleMove(Direction.RIGHT));

        updateView();
    }

    private void handleMove(Direction direction) {
        Message message = viewModel.tryMove(direction);
        Snackbar.make(rootView, message.toString(), Snackbar.LENGTH_SHORT).show();
        updateView();
    }

    private void updateView() {
        statusText.setText(viewModel.getStatusText());
    }
}
