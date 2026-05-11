package nz.ac.ara.bcde223.minimala3skeleton;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import nz.ac.ara.bcde223.minimala3skeleton.model.Game;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
    }

    @Test
    void initialScoreIsZero() {
        assertEquals(0, game.getScore());
    }

    @Test
    void increaseScoreIncrementsByOne() {
        game.increaseScore();
        assertEquals(1, game.getScore());
    }

    @Test
    void increaseScoreMultipleTimes() {
        game.increaseScore();
        game.increaseScore();
        assertEquals(2, game.getScore());
    }

    @Test
    void resetScoreSetsScoreToZero() {
        game.increaseScore();
        game.resetScore();
        assertEquals(0, game.getScore());
    }
}
