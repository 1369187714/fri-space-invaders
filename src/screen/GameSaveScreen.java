package screen;

import java.awt.event.KeyEvent;

import engine.Cooldown;
import engine.Core;
import engine.FileManager;
import engine.GameState;

public class GameSaveScreen extends Screen {


    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;

    /** Time between changes in user selection. */
    private Cooldown selectionCooldown;

    /** Current score */
    private int score;

    /** Current lives remaining */
    private int lives;

    private int uasnums;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width  Screen width.
     * @param height Screen height.
     * @param fps    Frames per second, frame rate at which the game is run.
     */
    public GameSaveScreen(final GameState gameState, final int width, final int height, final int fps) {
        super(width, height, fps);

        this.score = gameState.getScore();
        this.lives = gameState.getLivesRemaining();
        this.uasnums = FileManager.getUasnums();

        // Defaults to play.
        this.returnCode = 1;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public final int run() {
        super.run();

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        draw();
        if (this.selectionCooldown.checkFinished()
                && this.inputDelay.checkFinished()) {
            if (inputManager.isKeyDown(KeyEvent.VK_UP)
                    || inputManager.isKeyDown(KeyEvent.VK_W)
                    ||inputManager.isKeyDown(KeyEvent.VK_DOWN)
                    || inputManager.isKeyDown(KeyEvent.VK_S)) {
                nextMenuItem();
                this.selectionCooldown.reset();
            }

            if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
                this.isRunning = false;
        }
    }

    /**
     * Shifts the focus to the next menu item.
     */
    private void nextMenuItem() {
        if (this.returnCode == 1)
            this.returnCode = 2;
        else if (this.returnCode == 2)
            this.returnCode = 1;
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawScore(this, this.score);
        drawManager.drawLives(this, this.lives);
        drawManager.drawUAShips(this, this.uasnums);
        drawManager.drawHorizontalLine(this, 39);

        drawManager.drawCenteredBigString(this, "Stage clear", this.getHeight() / 3);

        drawManager.drawStageClearScreen(this, this.returnCode);

        drawManager.completeDrawing(this);
    }

}
