package screen;

import java.awt.event.KeyEvent;
import java.util.Random;

import engine.Cooldown;
import engine.Core;
import engine.FileManager;
import engine.PermanentState;
import sound.SoundPlay;
import sound.SoundType;

public class StoreScreen extends Screen {

    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;

    private static final int COST_SHAPE = 100;
    private static final int COST_COLOR = 100;
    private static final int COST_BULLET = 100;
    private static final int COST_BGM = 100;
    private static final int COST_UAS = 100;


    /** Time between changes in user selection. */
    private Cooldown selectionCooldown;
    private PermanentState permanentState = PermanentState.getInstance();

    private SoundPlay soundPlay = SoundPlay.getInstance();

    private int menuCode = 0;
    private int focusReroll = 0;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public StoreScreen (final int width, final int height, final int fps) {
        super(width, height, fps);

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
                    || inputManager.isKeyDown(KeyEvent.VK_W)) {
                if (focusReroll == 0)
                    previousMenuItem();
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                    || inputManager.isKeyDown(KeyEvent.VK_S)) {
                if (focusReroll == 0)
                    nextMenuItem();
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_RIGHT) // 뽑기 버튼으로 가기
                    || inputManager.isKeyDown(KeyEvent.VK_D)) {
                if (menuCode != 5) focusReroll = 1;
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_LEFT) // 메뉴 선택으로 되돌아가기
                    || inputManager.isKeyDown(KeyEvent.VK_A)) {
                if (menuCode != 5) focusReroll = 0;
                this.selectionCooldown.reset();
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE)){
                if (menuCode == 5)
                    this.isRunning = false;
                else {
                    if (focusReroll == 0)
                        focusReroll = 1;
                    else { // reroll
                        rerollItem();
                    }
                }
                soundPlay.play(SoundType.menuClick);
                this.selectionCooldown.reset();
            }
        }
    }



    /**
     * Shifts the focus to the next menu item.
     */
    private void nextMenuItem() {
        if (menuCode == 5)
            menuCode = 0;
        else
            menuCode++;
        soundPlay.play(SoundType.menuSelect);
    }

    /**
     * Shifts the focus to the previous menu item.
     */
    private void previousMenuItem() {
        if (menuCode == 0)
            menuCode = 5;
        else
            menuCode--;
        soundPlay.play(SoundType.menuSelect);
    }

    private void rerollItem() {
        if (menuCode == 0){ // ship shape
            if (permanentState.getCoin() >= COST_SHAPE) {
                int x = new Random().nextInt(3);
                while (permanentState.getShipShape() == x)
                    x = new Random().nextInt(3);

                permanentState.setShipShape(x);
                permanentState.setCoin(-COST_SHAPE);
            }
        }
        else if (menuCode == 1){ // ship color
            if (permanentState.getCoin() >= COST_COLOR) {
                int x = new Random().nextInt(3);
                while (permanentState.getShipColor() == x)
                    x = new Random().nextInt(3);

                permanentState.setShipColor(x);
                permanentState.setCoin(-COST_COLOR);
            }
        }
        else if (menuCode == 2){ // bullet effect
            if (permanentState.getCoin() >= COST_BULLET) {
                int x = new Random().nextInt(3) + 1;
                while (permanentState.getBulletSFX() == x)
                    x = new Random().nextInt(3) + 1;

                permanentState.setBulletSFX(x);
                permanentState.setCoin(-COST_BULLET);
            }
        }else if (menuCode == 3) { // BGM
            if (permanentState.getCoin() >= COST_BGM) {
                int x = new Random().nextInt(3) + 1;
                while (permanentState.getBGM() == x)
                    x = new Random().nextInt(3) + 1;
                permanentState.setBGM(x);
                permanentState.setCoin(-COST_BGM);
            }
        } else if (menuCode == 4) { // uaship
            if (permanentState.getCoin() >= COST_UAS) {
                if(permanentState.getUAShip()<6){
                    int x = permanentState.getUAShip()+1;
                    permanentState.setUAShip(x);
                    FileManager.setUaShipNum(true);
                    permanentState.setCoin(-COST_UAS);
                }
            }
        }else {
            logger.info("error menu code");
        }
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawStoreTitle(this);
        drawManager.drawStoreMenu(this, menuCode, focusReroll);
        if (menuCode < 5)
            drawManager.drawStoreGacha(this, menuCode, focusReroll);
        drawManager.drawCoin(this, permanentState.getCoin());

        drawManager.completeDrawing(this);
    }
}
