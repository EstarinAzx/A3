package nz.ac.ara.bcde223.minimala3skeleton.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import nz.ac.ara.bcde223.minimala3skeleton.R;
import nz.ac.ara.bcde223.minimala3skeleton.model.Color;
import nz.ac.ara.bcde223.minimala3skeleton.model.Direction;
import nz.ac.ara.bcde223.minimala3skeleton.model.Message;
import nz.ac.ara.bcde223.minimala3skeleton.model.Shape;
import nz.ac.ara.bcde223.minimala3skeleton.viewmodel.GameViewModel;

/**
 * Connects the on-screen controls to {@link GameViewModel}: it draws the board,
 * forwards button presses to the model via the view-model, refreshes the board
 * after each attempt, and reports invalid moves / a win to the player. No game
 * rules live here — the Activity only displays what the model reports.
 */
public class MainActivity extends AppCompatActivity {

    private static final int CELL_DP = 64;
    private static final int CELL_MARGIN_DP = 2;
    private static final String LEVEL_ASSET = "level1.txt";

    private GameViewModel viewModel;
    private View rootView;
    private TextView levelNameText;
    private TextView statusText;
    private GridLayout boardGrid;
    private Button upButton;
    private Button downButton;
    private Button leftButton;
    private Button rightButton;
    private Button resetButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new GameViewModel();

        rootView = findViewById(R.id.main);
        levelNameText = findViewById(R.id.levelNameText);
        statusText = findViewById(R.id.statusText);
        boardGrid = findViewById(R.id.boardGrid);

        upButton = findViewById(R.id.upButton);
        downButton = findViewById(R.id.downButton);
        leftButton = findViewById(R.id.leftButton);
        rightButton = findViewById(R.id.rightButton);
        resetButton = findViewById(R.id.resetButton);

        try {
            viewModel.loadLevel(readAsset(LEVEL_ASSET));
        } catch (IOException e) {
            statusText.setText("Error loading level file (" + LEVEL_ASSET + ").");
            return;
        }

        upButton.setOnClickListener(v -> handleMove(Direction.UP));
        downButton.setOnClickListener(v -> handleMove(Direction.DOWN));
        leftButton.setOnClickListener(v -> handleMove(Direction.LEFT));
        rightButton.setOnClickListener(v -> handleMove(Direction.RIGHT));
        resetButton.setOnClickListener(v -> resetGame());

        updateView();
    }

    private void handleMove(Direction direction) {
        Message message = viewModel.tryMove(direction);
        if (message != Message.OK) {
            Snackbar.make(rootView, describe(message), Snackbar.LENGTH_SHORT).show();
        }
        updateView();
        if (viewModel.isWon()) {
            showWinDialog();
        }
    }

    private void resetGame() {
        viewModel.reset();
        setMoveButtonsEnabled(true);
        updateView();
    }

    private void updateView() {
        levelNameText.setText(viewModel.getLevelName());
        statusText.setText(viewModel.getStatusText());
        renderBoard();
        if (viewModel.isWon()) {
            setMoveButtonsEnabled(false);
        }
    }

    /** Rebuilds the board as a grid of coloured, labelled cells. */
    private void renderBoard() {
        boardGrid.removeAllViews();
        int rows = viewModel.getRows();
        int cols = viewModel.getCols();
        boardGrid.setRowCount(rows);
        boardGrid.setColumnCount(cols);

        int sizePx = dpToPx(CELL_DP);
        int marginPx = dpToPx(CELL_MARGIN_DP);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                TextView cell = new TextView(this);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.width = sizePx;
                lp.height = sizePx;
                lp.setMargins(marginPx, marginPx, marginPx, marginPx);
                lp.rowSpec = GridLayout.spec(row);
                lp.columnSpec = GridLayout.spec(col);
                cell.setLayoutParams(lp);

                cell.setGravity(Gravity.CENTER);
                cell.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                cell.setTypeface(null, Typeface.BOLD);

                Color color = viewModel.getColorAt(row, col);
                cell.setBackgroundColor(backgroundFor(color));
                cell.setTextColor(textColorFor(color));
                cell.setText(labelFor(row, col));

                boardGrid.addView(cell);
            }
        }
    }

    private String labelFor(int row, int col) {
        if (viewModel.isEyeballAt(row, col)) {
            String arrow = arrowFor(viewModel.getEyeballDirection());
            return viewModel.hasGoalAt(row, col) ? arrow + "*" : arrow;
        }
        if (viewModel.isBlankAt(row, col)) {
            return "";
        }
        String shape = shapeLabel(viewModel.getShapeAt(row, col));
        return viewModel.hasGoalAt(row, col) ? shape + "*" : shape;
    }

    private void showWinDialog() {
        new AlertDialog.Builder(this)
                .setTitle("You win!")
                .setMessage("All goals completed. Play again?")
                .setCancelable(false)
                .setPositiveButton("Play again", (dialog, which) -> resetGame())
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void setMoveButtonsEnabled(boolean enabled) {
        upButton.setEnabled(enabled);
        downButton.setEnabled(enabled);
        leftButton.setEnabled(enabled);
        rightButton.setEnabled(enabled);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    /** Reads a UTF-8 text file bundled in {@code src/main/assets/}. */
    private String readAsset(String fileName) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getAssets().open(fileName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    private static String describe(Message message) {
        switch (message) {
            case BACKWARDS_MOVE:           return "Can't move backwards";
            case MOVING_OVER_BLANK:        return "Blocked — a blank square or the board edge is in the way";
            case MOVING_DIAGONALLY:        return "Can't move diagonally";
            case DIFFERENT_SHAPE_OR_COLOR: return "Can't move there — the next square must share this colour or shape";
            default:                       return "OK";
        }
    }

    private static int backgroundFor(Color color) {
        switch (color) {
            case RED:    return 0xFFE53935;
            case BLUE:   return 0xFF1E88E5;
            case YELLOW: return 0xFFFDD835;
            case GREEN:  return 0xFF43A047;
            case PURPLE: return 0xFF8E24AA;
            case BLANK:
            default:     return 0xFF2C2C2C;
        }
    }

    private static int textColorFor(Color color) {
        return color == Color.YELLOW ? 0xFF000000 : 0xFFFFFFFF;
    }

    private static String arrowFor(Direction direction) {
        switch (direction) {
            case UP:    return "▲";
            case DOWN:  return "▼";
            case LEFT:  return "◀";
            case RIGHT: return "▶";
            default:    return "?";
        }
    }

    private static String shapeLabel(Shape shape) {
        switch (shape) {
            case DIAMOND:   return "D";
            case CROSS:     return "C";
            case STAR:      return "S";
            case FLOWER:    return "F";
            case LIGHTNING: return "L";
            case BLANK:
            default:        return "";
        }
    }
}
