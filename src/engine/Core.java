package engine;

import java.util.ArrayList;

import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import entity.ItemPool;
import screen.GameScreen;
import screen.HighScoreScreen;
import screen.ScoreScreen;
import screen.Screen;
import screen.TitleScreen;
import screen.GameSaveScreen;
import screen.SettingScreen;
import screen.HelpScreen;
import screen.VolumeScreen;
import screen.StoreScreen;
import screen.PauseScreen;

/**
 * Implements core game logic.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class Core {

	/** Width of current screen. */
	private static final int WIDTH = 448;
	/** Height of current screen. */
	private static final int HEIGHT = 520;
	/** Max fps of current screen. */
	private static final int FPS = 60;

	/** Max lives. */
	private static final int MAX_LIVES = 3;
	/** Levels between extra life. */
	private static final int EXTRA_LIFE_FRECUENCY = 3;
	/** Total number of levels. */
	private static final int NUM_LEVELS = 9;
	/** Check click on Save & Exit button */
	private static boolean GO_MAIN;
	/** Difficulty settings for level 1. */
	private static final GameSettings SETTINGS_LEVEL_1 =

			new GameSettings(1, 4, 3, 30, 1600);
	/** Difficulty settings for level 2. */
	private static final GameSettings SETTINGS_LEVEL_2 =
			new GameSettings(2, 4, 4, 25, 1400);
	/** Difficulty settings for level 3. */
	private static final GameSettings SETTINGS_LEVEL_3 =
			new GameSettings(3, 5, 5, 20, 1100);
	/** Difficulty settings for level 4. */
	private static final GameSettings SETTINGS_LEVEL_4 =
			new GameSettings(4, 5, 6, 10, 800);
	/** Difficulty settings for level 5. */
	private static final GameSettings SETTINGS_LEVEL_5 =
			new GameSettings(5, 6, 7, 20, 700);
	/** Difficulty settings for level 6. */
	private static final GameSettings SETTINGS_LEVEL_6 =
			new GameSettings(6, 7, 7, 10, 600);
	/** Difficulty settings for level 7. */
	private static final GameSettings SETTINGS_LEVEL_7 =
			new GameSettings(7, 7, 8, 5, 500);
	/** add boss stage **/
	private static final GameSettings SETTINGS_boss_Stage =
			new GameSettings(8, 3, 3, 0, 100);

	private static final GameSettings SETTINGS_BOSS_Stage =
			new GameSettings(9, 5, 5, 1, 200);

	/** Frame to draw the screen on. */
	private static Frame frame;
	/** Screen currently shown. */
	private static Screen currentScreen;
	/** Difficulty settings list. */
	private static List<GameSettings> gameSettings;
	/** Application logger. */
	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());
	/** Logger handler for printing to disk. */
	private static Handler fileHandler;
	/** Logger handler for printing to console. */
	private static ConsoleHandler consoleHandler;


	/**
	 * Test implementation.
	 *
	 * @param args
	 *            Program args, ignored.
	 */
	public static void main(final String[] args) {
		try {
			LOGGER.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);
			LOGGER.setLevel(Level.ALL);

		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}

		frame = new Frame(WIDTH, HEIGHT);
		DrawManager.getInstance().setFrame(frame);
		int width = frame.getWidth();
		int height = frame.getHeight();

		gameSettings = new ArrayList<GameSettings>();
		gameSettings.add(SETTINGS_LEVEL_1);
		gameSettings.add(SETTINGS_LEVEL_2);
		gameSettings.add(SETTINGS_LEVEL_3);
		gameSettings.add(SETTINGS_LEVEL_4);
		gameSettings.add(SETTINGS_LEVEL_5);
		gameSettings.add(SETTINGS_BOSS_Stage);
		gameSettings.add(SETTINGS_LEVEL_6);
		gameSettings.add(SETTINGS_LEVEL_7);
		gameSettings.add(SETTINGS_boss_Stage);


		GameState gameState;
		PermanentState permanentState = PermanentState.getInstance();

		gameState = new GameState(1, 0, MAX_LIVES, 0, 0);

		int returnCode = 1;
		do {
			switch (returnCode) {
				case 1:
					// Main menu.
					currentScreen = new TitleScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " title screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing title screen.");
					break;
				case 2:
					// Game & score.
					GO_MAIN = true;
					do {
						// One extra live every few levels.
						boolean bonusLife = gameState.getLevel()
								% EXTRA_LIFE_FRECUENCY == 0
								&& gameState.getLivesRemaining() < MAX_LIVES;
						currentScreen = new GameScreen(gameState,
								gameSettings.get(gameState.getLevel() - 1),
								bonusLife, width, height, FPS);
						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
								+ " game screen at " + FPS + " fps.");
						frame.setScreen(currentScreen);
						LOGGER.info("Closing game screen.");

						gameState = ((GameScreen) currentScreen).getGameState();

						if (gameState.getScore() > 500)
							permanentState.setCoin(gameState.getScore() - 500); // earn coin


						if (gameState.getLivesRemaining() > 0) {
							currentScreen = new PauseScreen(width, height, FPS, gameState);
							returnCode = frame.setScreen(currentScreen);
						}

						if (gameState.getLivesRemaining() > 0 && gameState.getLevel() < NUM_LEVELS){
							LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
									+ " game save screen at " + FPS + " fps.");
							currentScreen = new GameSaveScreen(gameState, width, height, FPS);
							returnCode = frame.setScreen(currentScreen);
							LOGGER.info("Closing game save screen.");
							if (returnCode == 2) {
								getFileManager().Savefile(gameState);
								LOGGER.info("Complete Save.");
								GO_MAIN = false;
								gameState = new GameState(1, 0, MAX_LIVES, 0, 0);
								returnCode = 1;
								break;
							}
						}

						gameState = new GameState(gameState.getLevel() + 1,
								gameState.getScore(),
								gameState.getLivesRemaining(),
								gameState.getBulletsShot(),
								gameState.getShipsDestroyed()
						);
					} while (gameState.getLivesRemaining() > 0
							&& gameState.getLevel() <= NUM_LEVELS);
					if (!GO_MAIN)
						break;
					getFileManager().Savefile(new GameState(0, 0, 3, 0, 0));
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " score screen at " + FPS + " fps, with a score of "
							+ gameState.getScore() + ", "
							+ gameState.getLivesRemaining() + " lives remaining, "
							+ gameState.getBulletsShot() + " bullets shot and "
							+ gameState.getShipsDestroyed() + " ships destroyed.");
					currentScreen = new ScoreScreen(width, height, FPS, gameState);
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing score screen.");
					if (gameState.getLivesRemaining() == 0 || gameState.getLevel() == NUM_LEVELS + 1)
						gameState = new GameState(1, 0, MAX_LIVES, 0, 0);
					break;
				case 3:
					// High scores.
					currentScreen = new HighScoreScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " high score screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing high score screen.");
					break;
				default:
					break;

				case 4:
					// Store
				/* returnCode = 1;
				do {
					switch (returnCode) {
						case 1:
							// Main store
							currentScreen = new StoreScreen(width, height, FPS);
							LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
									+ " store screen at " + FPS + " fps.");
							returnCode = frame.setScreen(currentScreen);
							LOGGER.info("Closing store screen.");
							break;
						case 2:
							// Ship shape
							currentScreen = new ShipShapeScreen(width, height, FPS);
							LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
									+ " ship shape screen at " + FPS + " fps.");
							returnCode = frame.setScreen(currentScreen);
							LOGGER.info("Closing ship shape screen.");
							break;
						case 3:
							// Ship color
							currentScreen = new ShipColorScreen(width, height, FPS);
							LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
									+ " ship color screen at " + FPS + " fps.");
							returnCode = frame.setScreen(currentScreen);
							LOGGER.info("Closing ship color screen.");
							break;
						case 4:
							// Bullet effect
							currentScreen = new BulletEffectScreen(width, height, FPS);
							LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
									+ " bullet effect screen at " + FPS + " fps.");
							returnCode = frame.setScreen(currentScreen);
							LOGGER.info("Closing bullet effect screen.");
							break;
						case 5:
							// BGM
							currentScreen = new BGMScreen(width, height, FPS);
							LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
									+ " background music screen at " + FPS + " fps.");
							returnCode = frame.setScreen(currentScreen);
							LOGGER.info("Closing background music screen.");
							break;
						default:
							break;
					}
				} while (returnCode != 0);

				returnCode = 1; */
        
				currentScreen = new StoreScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " store screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing store screen.");
				break;

			case 5:
				// Load
				String save_info [] = getFileManager().loadInfo();
				gameState = new GameState(Integer.parseInt(save_info[0]), Integer.parseInt(save_info[1]), Integer.parseInt(save_info[2]), Integer.parseInt(save_info[3]), Integer.parseInt(save_info[4]));
				returnCode = 2;
				break;
				
			case 6:
				// Setting.
				currentScreen = new SettingScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " setting screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing setting screen.");
				break;
				
			case 7: //Help
				currentScreen = new HelpScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " setting screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing help screen.");
				break;
				
			case 8: //Volume //mainmenu 1014
				currentScreen = new VolumeScreen(width, height, FPS);
				LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
						+ " setting screen at " + FPS + " fps.");
				returnCode = frame.setScreen(currentScreen);
				LOGGER.info("Closing help screen.");
				break;

			}

		} while (returnCode != 0);

		fileHandler.flush();
		fileHandler.close();
		System.exit(0);
	}

	/**
	 * Constructor, not called.
	 */
	private Core() {

	}

	/**
	 * Controls access to the logger.
	 *
	 * @return Application logger.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Controls access to the drawing manager.
	 *
	 * @return Application draw manager.
	 */
	public static DrawManager getDrawManager() {
		return DrawManager.getInstance();
	}

	/**
	 * Controls access to the input manager.
	 *
	 * @return Application input manager.
	 */
	public static InputManager getInputManager() {
		return InputManager.getInstance();
	}

	/**
	 * Controls access to the file manager.
	 *
	 * @return Application file manager.
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}

	/**
	 * Controls creation of new cooldowns.
	 *
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @return A new cooldown.
	 */
	public static Cooldown getCooldown(final int milliseconds) {
		return new Cooldown(milliseconds);
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 *
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @param variance
	 *            Variation in the cooldown duration.
	 * @return A new cooldown with variance.
	 */
	public static Cooldown getVariableCooldown(final int milliseconds,
											   final int variance) {
		return new Cooldown(milliseconds, variance);
	}
}