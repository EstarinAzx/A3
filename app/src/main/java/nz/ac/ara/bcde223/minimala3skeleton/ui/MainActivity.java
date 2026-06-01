package nz.ac.ara.bcde223.minimala3skeleton.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

        // targetSdk 36 forces edge-to-edge: pad the content below the system bars
        // so the level name clears the status-bar notch, on top of the 16dp design padding.
        final int basePad = dpToPx(16);
        ViewCompat.setOnApplyWindowInsetsListener(rootView, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(basePad + bars.left, basePad + bars.top,
                    basePad + bars.right, basePad + bars.bottom);
            return insets;
        });

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

        int iconPx = dpToPx(CELL_DP - 24);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                FrameLayout cell = new FrameLayout(this);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.width = sizePx;
                lp.height = sizePx;
                lp.setMargins(marginPx, marginPx, marginPx, marginPx);
                lp.rowSpec = GridLayout.spec(row);
                lp.columnSpec = GridLayout.spec(col);
                cell.setLayoutParams(lp);

                Color color = viewModel.getColorAt(row, col);
                cell.setBackgroundColor(backgroundFor(color));
                int inkColor = textColorFor(color);

                if (!viewModel.isBlankAt(row, col)) {
                    addCellContent(cell, row, col, inkColor, iconPx);
                }

                boardGrid.addView(cell);
            }
        }
    }

    /**
     * Draws the square's shape icon, then (when the eyeball stands here) a
     * direction token on top, plus an optional goal marker. The shape is always
     * drawn so every square shows its shape, even under the eyeball.
     */
    private void addCellContent(FrameLayout cell, int row, int col, int inkColor, int iconPx) {
        int shapeRes = shapeDrawable(viewModel.getShapeAt(row, col));
        if (shapeRes != 0) {
            ImageView icon = new ImageView(this);
            icon.setImageResource(shapeRes);
            icon.setColorFilter(inkColor);
            icon.setLayoutParams(new FrameLayout.LayoutParams(iconPx, iconPx, Gravity.CENTER));
            cell.addView(icon);
        }

        if (viewModel.isEyeballAt(row, col)) {
            int tokenPx = dpToPx(34);
            TextView eye = new TextView(this);
            eye.setText(arrowFor(viewModel.getEyeballDirection()));
            eye.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            eye.setTypeface(null, Typeface.BOLD);
            eye.setTextColor(0xFFFFFFFF);
            eye.setGravity(Gravity.CENTER);
            eye.setBackgroundResource(R.drawable.eyeball_token);
            eye.setLayoutParams(new FrameLayout.LayoutParams(tokenPx, tokenPx, Gravity.CENTER));
            cell.addView(eye);
        }

        if (viewModel.hasGoalAt(row, col)) {
            TextView goal = new TextView(this);
            goal.setText("*");
            goal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            goal.setTypeface(null, Typeface.BOLD);
            goal.setTextColor(inkColor);
            goal.setPadding(0, dpToPx(2), dpToPx(4), 0);
            goal.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP | Gravity.END));
            cell.addView(goal);
        }
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

    private static int shapeDrawable(Shape shape) {
        switch (shape) {
            case DIAMOND:   return R.drawable.shape_diamond;
            case CROSS:     return R.drawable.shape_cross;
            case STAR:      return R.drawable.shape_star;
            case FLOWER:    return R.drawable.shape_flower;
            case LIGHTNING: return R.drawable.shape_lightning;
            case BLANK:
            default:        return 0;
        }
    }
}
