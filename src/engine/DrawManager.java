
package engine;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import screen.GameScreen;
import screen.Screen;
import entity.Entity;
import entity.Ship;
import entity.Life;
import sound.SoundPlay;

/**
 * Manages screen drawing.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class DrawManager {

	/** Singleton instance of the class. */
	private static DrawManager instance;
	/** Current frame. */
	private static Frame frame;
	/** FileManager instance. */
	private static FileManager fileManager;
	/** Application logger. */
	private static Logger logger;
	/** Graphics context. */
	private static Graphics graphics;
	/** Buffer Graphics. */
	private static Graphics backBufferGraphics;
	/** Buffer image. */
	private static BufferedImage backBuffer;
	/** Normal sized font. */
	private static Font fontRegular;
	/** Normal sized font properties. */
	private static FontMetrics fontRegularMetrics;
	/** Big sized font. */
	private static Font fontBig;
	/** Big sized font properties. */
	private static FontMetrics fontBigMetrics;

	/** Sprite types mapped to their images. */
	private static Map<SpriteType, boolean[][]> spriteMap;

	/** Sprite types. */
	public static enum SpriteType {
		/** Player ship. */
		Ship,
		/** Destroyed player ship. */
		ShipDestroyed,
		/** Player bullet. */
		Bullet,
		Laser,
		/** Enemy bullet. */
		EnemyBullet,
		/** First enemy ship - first form. */
		EnemyShipA1,
		/** First enemy ship - second form. */
		EnemyShipA2,
		/** Second enemy ship - first form. */
		EnemyShipB1,
		/** Second enemy ship - second form. */
		EnemyShipB2,
		/** Third enemy ship - first form. */
		EnemyShipC1,
		/** Third enemy ship - second form. */
		EnemyShipC2,
		/** Bonus ship. */
		EnemyShipSpecial,
		/** Destroyed enemy ship. */
		EnemyShipdangerous,

		Explosion,
		/** dropped item */
		ItemDrop,
		/** Obtained item */
		ItemGet,
		/** shield for playership */
		Shield,
    
    /** Life shape */
		Life

	};

	/**
	 * Private constructor.
	 */
	private DrawManager() {
		fileManager = Core.getFileManager();
		logger = Core.getLogger();
		logger.info("Started loading resources.");

		try {
			spriteMap = new LinkedHashMap<SpriteType, boolean[][]>();

			spriteMap.put(SpriteType.Ship, new boolean[13][8]);
			spriteMap.put(SpriteType.ShipDestroyed, new boolean[13][8]);
			spriteMap.put(SpriteType.Bullet, new boolean[3][5]);
			spriteMap.put(SpriteType.Laser, new boolean[5][156]);
			spriteMap.put(SpriteType.EnemyBullet, new boolean[3][5]);
			spriteMap.put(SpriteType.EnemyShipA1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipA2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipB1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipB2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipC1, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipC2, new boolean[12][8]);
			spriteMap.put(SpriteType.EnemyShipSpecial, new boolean[16][7]);
			spriteMap.put(SpriteType.Explosion, new boolean[13][7]);
			spriteMap.put(SpriteType.ItemDrop, new boolean[9][9]);
			spriteMap.put(SpriteType.ItemGet, new boolean[5][5]);
			spriteMap.put(SpriteType.Shield, new boolean[13][1]);
			spriteMap.put(SpriteType.Life, new boolean[13][13]);
			spriteMap.put(SpriteType.EnemyShipdangerous, new boolean[16][7]);

			fileManager.readship();//read ship파일
			fileManager.loadSprite(spriteMap);
			logger.info("Finished loading the sprites.");

			// Font loading.
			fontRegular = fileManager.loadFont(14f);
			fontBig = fileManager.loadFont(24f);
			logger.info("Finished loading the fonts.");

		} catch (IOException e) {
			logger.warning("Loading failed.");
		} catch (FontFormatException e) {
			logger.warning("Font formating failed.");
		}
	}


	/**
	 * Returns shared instance of DrawManager.
	 * 
	 * @return Shared instance of DrawManager.
	 */
	protected static DrawManager getInstance() {
		if (instance == null)
			instance = new DrawManager();
		return instance;
	}

	/**
	 * Sets the frame to draw the image on.
	 * 
	 * @param currentFrame
	 *            Frame to draw on.
	 */
	public void setFrame(final Frame currentFrame) {
		frame = currentFrame;
	}

	/**
	 * First part of the drawing process. Initialices buffers, draws the
	 * background and prepares the images.
	 * 
	 * @param screen
	 *            Screen to draw in.
	 */
	public void initDrawing(final Screen screen) {
		backBuffer = new BufferedImage(screen.getWidth(), screen.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		graphics = frame.getGraphics();
		backBufferGraphics = backBuffer.getGraphics();

			backBufferGraphics.setColor(Color.BLACK);

		backBufferGraphics
				.fillRect(0, 0, screen.getWidth(), screen.getHeight());

		fontRegularMetrics = backBufferGraphics.getFontMetrics(fontRegular);
		fontBigMetrics = backBufferGraphics.getFontMetrics(fontBig);

		// drawBorders(screen);
		// drawGrid(screen);
	}

	/**
	 * Draws the completed drawing on screen.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 */
	public void completeDrawing(final Screen screen) {
		graphics.drawImage(backBuffer, frame.getInsets().left,
				frame.getInsets().top, frame);
	}

	/**
	 * Draws an entity, using the apropiate image.
	 * 
	 * @param entity
	 *            Entity to be drawn.
	 * @param positionX
	 *            Coordinates for the left side of the image.
	 * @param positionY
	 *            Coordinates for the upper side of the image.
	 */
	public void drawEntity(final Entity entity, final int positionX,
			final int positionY) {
		boolean[][] image = spriteMap.get(entity.getSpriteType());

		backBufferGraphics.setColor(entity.getColor());
		for (int i = 0; i < image.length; i++)
			for (int j = 0; j < image[i].length; j++)
				if (image[i][j])
					backBufferGraphics.drawRect(positionX + i * 2, positionY
							+ j * 2, 1, 1);
	}

	/**
	 * For debugging purpouses, draws the canvas borders.
	 * 
	 * @param screen
	 *            Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawBorders(final Screen screen) {
		backBufferGraphics.setColor(Color.GREEN);
		backBufferGraphics.drawLine(0, 0, screen.getWidth() - 1, 0);
		backBufferGraphics.drawLine(0, 0, 0, screen.getHeight() - 1);
		backBufferGraphics.drawLine(screen.getWidth() - 1, 0,
				screen.getWidth() - 1, screen.getHeight() - 1);
		backBufferGraphics.drawLine(0, screen.getHeight() - 1,
				screen.getWidth() - 1, screen.getHeight() - 1);
	}

	/**
	 * For debugging purpouses, draws a grid over the canvas.
	 * 
	 * @param screen
	 *            Screen to draw in.
	 */
	@SuppressWarnings("unused")
	private void drawGrid(final Screen screen) {
		backBufferGraphics.setColor(Color.darkGray);
		for (int i = 0; i < screen.getHeight() - 1; i += 2)
			backBufferGraphics.drawLine(0, i, screen.getWidth() - 1, i);
		for (int j = 0; j < screen.getWidth() - 1; j += 2)
			backBufferGraphics.drawLine(j, 0, j, screen.getHeight() - 1);
	}

	/**
	 * Draws current score on screen.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Current score.
	 */
	public void drawScore(final Screen screen, final int score) {
		backBufferGraphics.setFont(fontBig);

		backBufferGraphics.setColor(Color.cyan);
		String scoreString = String.format("%04d", score);
		backBufferGraphics.drawString(scoreString, screen.getWidth() - 250, 30);
	}

	/**
	 * Draws number of remaining lives on screen.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param lives
	 *            Current lives.
	 */
	public void drawLives(final Screen screen, final int lives) {
		Life remainLife = new Life(0, 0);
		for (int i = 0; i < lives; i++)
			drawEntity(remainLife, 45 + 30 * i, 6);
	}

	public void drawUAShips(final Screen screen, final int uasnums) {
		UAShip uaShip = new UAShip(0, 0);
		backBufferGraphics.setColor(Color.PINK);
		for (int i = 0; i < uasnums; i++)
			drawEntity(uaShip, 400 - 23 * i, 9);
	}

	public void drawShootCD(final Screen screen, final Ship ship) {
		double persent = 1/(double)ship.getShootingInterval()*(ship.getShootingInterval()-ship.getShootCD());
		int angle = (int)(360*persent);
		backBufferGraphics.setColor(Color.BLUE);
		backBufferGraphics.fillArc(14, 9, 21, 21, 90, -angle);
	}
	public void drawLasingCD(final Screen screen, final Ship ship) {
		double persent = 1/(double)ship.getLasingInterval()*(ship.getLasingInterval()-ship.getLasingCD());
		int angle = (int)(360*persent);
		Graphics2D g2 = (Graphics2D)backBufferGraphics;
		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(4.0f));
		g2.drawArc(13, 7, 24, 24, 90, -angle);
	}

	/**
	 * Draws a thick line from side to side of the screen.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param positionY
	 *            Y coordinate of the line.
	 */
	public void drawHorizontalLine(final Screen screen, final int positionY) {
		backBufferGraphics.setColor(Color.BLUE);
		backBufferGraphics.drawLine(0, positionY, screen.getWidth(), positionY);
		backBufferGraphics.drawLine(0, positionY + 1, screen.getWidth(),
				positionY + 1);
	}

	/**
	 * Draws game title.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawTitle(final Screen screen) {
		String titleString = "Invaders";
		String instructionsString =
				"select with w+s / arrows, confirm with space";

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 4);

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 6);
	}
	//mainmenu 1014
	public void drawSetting(final Screen screen) {
		String titleString = "Setting";
		String instructionsString =
				"select with w+s / arrows, confirm with space";

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 4);

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 6);
	}
	//mainmenu 1014
	public void drawVolume(final Screen screen, final int volume, final int option){
		String volumeString = "Bgm";
		String effectString = "Effect";
		String backString = "Back";
		
		if (option == 100)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, volumeString,
				screen.getHeight()/3);
		drawCenteredBigString(screen,"<        "+ SoundPlay.getInstance().getBgmVolume() +"        >",screen.getHeight()/3+fontRegularMetrics.getHeight() * 2);
		if (option == 101)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, effectString,
				screen.getHeight()/3 + fontBigMetrics.getHeight()*3);
		drawCenteredBigString(screen,"<        "+ SoundPlay.getInstance().getEffectVolume() +"        >",screen.getHeight()/3+fontRegularMetrics.getHeight() * 7);
		if (option == 6)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, backString,
				screen.getHeight()/3 + fontBigMetrics.getHeight() * 7);
		

	}

	public void drawVolumeTitle(final Screen screen){
		String titleString = "Volume";
		String instructionsString =
				"select with w+s / arrows, confirm with space";

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 4);

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, titleString, screen.getHeight() / 6);
	}

	/**
	 * Draws main menu.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param option
	 *            Option selected.
	 */
	public void drawMenu(final Screen screen, final int option) {
		String playString = "Play";
		String highScoresString = "High scores";
		String exitString = "exit";
		String setString = "setting";
		String loadString = "load";
		String storeString = "store";
		String helpString = "help";
				

		if (option == 2)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, playString,
				screen.getHeight()/3);
		if (option == 3)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, highScoresString, screen.getHeight()/3
				 + fontRegularMetrics.getHeight() * 2);
		if (option == 4)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, storeString, screen.getHeight()
				/ 3 + fontRegularMetrics.getHeight() * 4);
		if (option == 5)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, loadString, screen.getHeight() / 
				 3 + fontRegularMetrics.getHeight() * 6);
		if (option == 6)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, setString, screen.getHeight() / 
				 3 + fontRegularMetrics.getHeight() * 8);
		if (option == 7)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, helpString, screen.getHeight() / 
				 3 + fontRegularMetrics.getHeight() * 10);
		if (option == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, exitString, screen.getHeight() / 
				 3 + fontRegularMetrics.getHeight() * 12);
		
	}
	//mainmenu 1014
	public void drawSettingMenu(final Screen screen, final int option) {
		String VolumeString = "Sound";
		String BackString = "Back";

		if (option == 8)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, VolumeString,
				screen.getHeight()/3);
		if (option == 1)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, BackString, screen.getHeight()/3
				 + fontRegularMetrics.getHeight() * 2);
		
	}

	/**
	 * Draws main menu.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param option
	 *            Option selected.
	 */
	public void drawItemInfo(final Screen screen, final int option) {
		String bulletSpeedString = "Bullet Speed Up !";
		String pointUpString = "Point Up !";
		String machineString = "Machine Gun !";
		String shieldString = "Shield !";
		String speedUpString = "Speed Up !";
		String lifeString = "Extra Life !";

		if (option == 0) {
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, bulletSpeedString,
					screen.getHeight()*2/12);
		} else if (option == 1) {
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, pointUpString,
					screen.getHeight()*2/12);
		} else if (option == 2) {
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, machineString,
					screen.getHeight()*2/12);
		} else if (option == 3) {
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, shieldString,
					screen.getHeight()*2/12);
		} else if (option == 4) {
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, speedUpString,
					screen.getHeight()*2/12);
		} else if (option == 5) {
			backBufferGraphics.setColor(Color.WHITE);
			drawCenteredRegularString(screen, lifeString,
					screen.getHeight()*2/12);
		}

	}

	/**
	 * Draws game results.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param score
	 *            Score obtained.
	 * @param livesRemaining
	 *            Lives remaining when finished.
	 * @param shipsDestroyed
	 *            Total ships destroyed.
	 * @param accuracy
	 *            Total accuracy.
	 * @param isNewRecord
	 *            If the score is a new high score.
	 */
	public void drawResults(final Screen screen, final int score,
			final int livesRemaining, final int shipsDestroyed,
			final float accuracy, final boolean isNewRecord) {

		String scoreString = String.format("score %04d", score);
		String livesRemainingString = "lives remaining " + livesRemaining;
		String shipsDestroyedString = "enemies destroyed " + shipsDestroyed;
		String accuracyString = String
				.format("accuracy %.2f%%", accuracy * 100);

		int height = isNewRecord ? 4 : 2;

		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, scoreString, screen.getHeight()
				/ height);
		drawCenteredRegularString(screen, livesRemainingString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 2);
		drawCenteredRegularString(screen, shipsDestroyedString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 4);
		drawCenteredRegularString(screen, accuracyString, screen.getHeight()
				/ height + fontRegularMetrics.getHeight() * 6);
	}

	/**
	 * Draws interactive characters for name input.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param name
	 *            Current name selected.
	 * @param nameCharSelected
	 *            Current character selected for modification.
	 */
	public void drawNameInput(final Screen screen, final char[] name,
			final int nameCharSelected) {
		String newRecordString = "New Record!";
		String introduceNameString = "Introduce name:";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredRegularString(screen, newRecordString, screen.getHeight()
				/ 4 + fontRegularMetrics.getHeight() * 10);
		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, introduceNameString,
				screen.getHeight() / 4 + fontRegularMetrics.getHeight() * 12);

		// 3 letters name.
		int positionX = screen.getWidth()
				/ 2
				- (fontRegularMetrics.getWidths()[name[0]]
						+ fontRegularMetrics.getWidths()[name[1]]
						+ fontRegularMetrics.getWidths()[name[2]]
								+ fontRegularMetrics.getWidths()[' ']) / 2;

		for (int i = 0; i < 3; i++) {
			if (i == nameCharSelected)
				backBufferGraphics.setColor(Color.GREEN);
			else
				backBufferGraphics.setColor(Color.WHITE);

			positionX += fontRegularMetrics.getWidths()[name[i]] / 2;
			positionX = i == 0 ? positionX
					: positionX
							+ (fontRegularMetrics.getWidths()[name[i - 1]]
									+ fontRegularMetrics.getWidths()[' ']) / 2;

			backBufferGraphics.drawString(Character.toString(name[i]),
					positionX,
					screen.getHeight() / 4 + fontRegularMetrics.getHeight()
							* 14);
		}
	}

	public void drawPause (final Screen screen, final int score,
							final int livesRemaining, final int shipsDestroyed,
			final int coin, final boolean isNewRecord) {
		String scoreString = String.format("score %04d", score);
		String livesRemainingString = "lives remaining " + livesRemaining;
		String shipsDestroyedString = "enemies destroyed " + shipsDestroyed;
		String GetcoinString = "Get coins " + coin;

		int height = isNewRecord ? 4 : 2;

		backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, scoreString, screen.getHeight()
				/ height);
		drawCenteredRegularString(screen, livesRemainingString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 2);
		drawCenteredRegularString(screen, shipsDestroyedString,
				screen.getHeight() / height + fontRegularMetrics.getHeight()
						* 4);
		drawCenteredRegularString(screen, GetcoinString, screen.getHeight()
				/ height + fontRegularMetrics.getHeight() * 6);
	}

	/**
	 * Draws basic content of game over screen.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param acceptsInput
	 *            If the screen accepts input.
	 * @param isNewRecord
	 *            If the score is a new high score.
	 */
	
	public void PauseGame(final Screen screen, final boolean acceptsInput,
			final boolean isNewRecord) {
		String gameOverString = "Stage Clear";
		String continueOrExitString =
				"Press Space to play again";

		int height = isNewRecord ? 4 : 2;

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, gameOverString, screen.getHeight()
				/ height - fontBigMetrics.getHeight() * 2);

		if (acceptsInput)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, continueOrExitString,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10);
	}

	public void drawGameOver(final Screen screen, final boolean acceptsInput,
			final boolean isNewRecord) {
		String gameOverString = "Game Over";
		String continueOrExitString =
				"Press Space to play again, Escape to exit";

		int height = isNewRecord ? 4 : 2;

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, gameOverString, screen.getHeight()
				/ height - fontBigMetrics.getHeight() * 2);

		if (acceptsInput)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, continueOrExitString,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 10);
	}

	/**
	 * Draws high score screen title and instructions.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawHighScoreMenu(final Screen screen) {
		String highScoreString = "High Scores";
		String instructionsString = "Press Space to return";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, highScoreString, screen.getHeight() / 8);

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 5);
	}
	
	//Draw Setting
	public void drawSettingMenu(final Screen screen) {
		String volumeString = "VOLUME";
		String screenSizeString = "SCREEN SIZE";
		
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, volumeString, screen.getHeight() / 8);

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, screenSizeString,
				screen.getHeight() / 5);
		
	}
	public void drawHelpMenu(final Screen screen, final int page) {
		String helpString = "HELP";
		String right = "right:  >   (right arrow)";
		String left = "left:  <   (left arrow)";
		String shoot = "shoot:   spacebar";
		String spaceBar = "Press Space to return"; // 뒤로 가기
		String pageKey = "Press Number key to page"; // 페이지 변경
		
		String operationDescription = "Operation"; // 조작 키 
		
		String itemDescription = "Item Description"; // 아이템 설명 
		String item1 = "shield";
		String item1_1 = "get shield to protect bullet of one time "; // 아이템1 
		
		String item2 = "extra life"; // 아이템2
		String item2_1 = "get extra life (maximum 4) "; // 아이템2
		
		String item3 = "move speed up"; // 아이템3 
		String item3_1 = "move speed up of ship "; // 아이템3
		
		String item4 = "machinegun"; // 아이템4 
		String item4_1 = "decrease bullet interval "; // 아이템4 
		
		String item5 = "bullet speed up"; // 아이템5 
		String item5_1 = "increase bullet speed "; // 아이템5
		
		String item6 = "point up"; // 아이템6 
		String item6_1 = "increase kill point earned per enermy "; // 아이템6 
		
		
		
		
		
		
		String page_str = "page : "; // page 
		
		
		switch (page) {
			case 1:
				backBufferGraphics.setColor(Color.GREEN);
				drawCenteredRegularString(screen, operationDescription,
						screen.getHeight()/3);
			
				backBufferGraphics.setColor(Color.WHITE);
				drawCenteredRegularString(screen, right, screen.getHeight() / 3+20);
				backBufferGraphics.setColor(Color.WHITE);
				drawCenteredRegularString(screen, left, screen.getHeight() / 3 + 40);
				backBufferGraphics.setColor(Color.WHITE);
				drawCenteredRegularString(screen, shoot, screen.getHeight() / 3 + 60);
				
							
				
				
				break;
			case 2:
				backBufferGraphics.setColor(Color.GREEN);
				drawCenteredRegularString(screen, itemDescription,
						screen.getHeight()/3);
				
				backBufferGraphics.setColor(Color.ORANGE);
				backBufferGraphics.drawString(item1, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 -50);// 아이템1
				
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawString(item1_1, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 -30);// 아이템1
				
				backBufferGraphics.setColor(Color.ORANGE);
				backBufferGraphics.drawString(item2, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 -10);// 아이템2
				
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawString(item2_1, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 +10);// 아이템2
				
				backBufferGraphics.setColor(Color.ORANGE);
				backBufferGraphics.drawString(item3, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 +30);// 아이템3
				
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawString(item3_1, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 +50);// 아이템3
				
				backBufferGraphics.setColor(Color.ORANGE);
				backBufferGraphics.drawString(item4, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 +70);// 아이템4
				
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawString(item4_1, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 +90);// 아이템4
				
				backBufferGraphics.setColor(Color.ORANGE);
				backBufferGraphics.drawString(item5, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 +110);// 아이템5
				
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawString(item5_1, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 +130);// 아이템5
				
				backBufferGraphics.setColor(Color.ORANGE);
				backBufferGraphics.drawString(item6, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 +150);// 아이템6
				
				backBufferGraphics.setColor(Color.WHITE);
				backBufferGraphics.drawString(item6_1, screen.getWidth() / 2 - 170,
						screen.getHeight() / 2 +170);// 아이템6
				
				
	
				
			
				break;
		}
		
		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, helpString, screen.getHeight() / 8);
		
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, pageKey,
				screen.getHeight()/4);
		
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, spaceBar,
				screen.getHeight()/5);
		
		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, page_str + page, screen.getHeight() / 3 + 300);	
		
	}

	/**
	 * Draws high scores.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param highScores
	 *            List of high scores.
	 */
	public void drawHighScores(final Screen screen,
			final List<Score> highScores) {
		backBufferGraphics.setColor(Color.WHITE);
		int i = 0;
		String scoreString = "";
		
		for (Score score : highScores) {

			scoreString = String.format("%s        %04d        %.2f%%", score.getName(),
					score.getScore(), score.getPer() * 100);
			drawCenteredRegularString(screen, scoreString, screen.getHeight()
					/ 4 + fontRegularMetrics.getHeight() * (i + 1) * 2);
			i++;
		}
	}

	/**
	 * Draws store screen title and instructions.
	 *
	 * @param screen
	 *            Screen to draw on.
	 */
	public void drawStoreTitle(final Screen screen) {
		String StoreString = "Store";
		String instructionsString = "Confirm with space";

		backBufferGraphics.setColor(Color.GREEN);
		drawCenteredBigString(screen, StoreString, screen.getHeight() / 8);

		backBufferGraphics.setColor(Color.GRAY);
		drawCenteredRegularString(screen, instructionsString,
				screen.getHeight() / 5);
	}

	/**
	 * Draws store menu.
	 *
	 * @param screen
	 *            Screen to draw on.
	 * @param menu
	 *            Menu selected.
	 * @param focus
	 * 			  Gacha selected.
	 */
	public void drawStoreMenu(final Screen screen, final int menu, final int focus) {
		String shipShapeString = "ship shape";
		String shipColorString = "ship color";
		String bulletEffectString = "bullet effect";
		String BGMString = "BGM";
		String exitString = "exit";

		if (menu == 0 && focus == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(shipShapeString, screen.getWidth() / 2 - 170,
				screen.getHeight() /2);
		if (menu == 1 && focus == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(shipColorString, screen.getWidth() / 2 - 170,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 2);
		if (menu == 2 && focus == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(bulletEffectString, screen.getWidth() / 2 - 170,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 4);
		if (menu == 3 && focus == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(BGMString, screen.getWidth() / 2 - 170,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 6);
		if (menu == 4 && focus == 0)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		backBufferGraphics.drawString(exitString, screen.getWidth() / 2 - 170,
				screen.getHeight() / 2 + fontRegularMetrics.getHeight() * 8);
	}

	public void drawStoreGacha(final Screen screen, final int menu, final int focus) {
		String rerollString = "reroll!(100$)";
		String coinLackString = "Not enough coins!";
		PermanentState permanentState = PermanentState.getInstance();
		if (focus == 0)
			backBufferGraphics.setColor(Color.WHITE);
		else {
			backBufferGraphics.setColor(Color.GREEN);
			if (permanentState.getCoin() < 100) {
				backBufferGraphics.setColor(Color.RED);
				backBufferGraphics.drawString(coinLackString, screen.getWidth() / 2 + 20,
						screen.getHeight() / 2 + 180);
			}
		}
		backBufferGraphics.drawRect(screen.getWidth() / 2 + 50, screen.getHeight() / 2, 100, 100);
		backBufferGraphics.drawString(rerollString, screen.getWidth() / 2 + 100 - fontRegularMetrics.stringWidth(rerollString) / 2, screen.getWidth() / 2 + 180);

		if(menu < 2) { // shape, color
			try{
				fileManager.loadSprite_Temp(spriteMap);
				FileManager.setPlayerShipShape();
			}
			catch (IOException e){
				logger.warning("Loading failed.");
			}

			FileManager.setPlayerShipColor(permanentState.getShipColor());
			if (permanentState.getShipColor() == 0)
				drawEntity(new Ship(0, 0, FileManager.ChangeIntToColor()), screen.getWidth() / 2 + 89, screen.getHeight() / 2 + 42);
			if (permanentState.getShipColor() == 1)
				drawEntity(new Ship(0, 0, 0, FileManager.ChangeIntToColor()), screen.getWidth() / 2 + 89, screen.getHeight() / 2 + 42);
			if (permanentState.getShipColor() == 2)
				drawEntity(new Ship(0, 0, '0', FileManager.ChangeIntToColor()), screen.getWidth() / 2 + 89, screen.getHeight() / 2 + 42);
		}
		else if(menu == 2){ // bullet sfx
			backBufferGraphics.setFont(fontBig);
			backBufferGraphics.drawString(Integer.toString(permanentState.getBulletSFX()), screen.getWidth() / 2 + 96, screen.getHeight() / 2 + 60);
			backBufferGraphics.setFont(fontRegular);
		}
		else if(menu == 3){ // bgm
			backBufferGraphics.setFont(fontBig);
			backBufferGraphics.drawString(Integer.toString(permanentState.getBGM()), screen.getWidth() / 2 + 96, screen.getHeight() / 2 + 60);
			backBufferGraphics.setFont(fontRegular);
		}
	}

	public void drawCoin(final Screen screen, final int coin) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.setColor(Color.WHITE);
		String coinString = String.format("%d", coin);
		backBufferGraphics.drawString("Coin : ", screen.getWidth() - 140, 25);
		backBufferGraphics.drawString(coinString, screen.getWidth() - 11 * (coinString.length() + 1), 25);
	}

	/**
	 * Draws a centered string on regular font.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredRegularString(final Screen screen,
			final String string, final int height) {
		backBufferGraphics.setFont(fontRegular);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontRegularMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Draws a centered string on big font.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param string
	 *            String to draw.
	 * @param height
	 *            Height of the drawing.
	 */
	public void drawCenteredBigString(final Screen screen, final String string,
			final int height) {
		backBufferGraphics.setFont(fontBig);
		backBufferGraphics.drawString(string, screen.getWidth() / 2
				- fontBigMetrics.stringWidth(string) / 2, height);
	}

	/**
	 * Countdown to game start.
	 * 
	 * @param screen
	 *            Screen to draw on.
	 * @param level
	 *            Game difficulty level.
	 * @param number
	 *            Countdown number.
	 * @param bonusLife
	 *            Checks if a bonus life is received.
	 */
	public void drawCountDown(final Screen screen, final int level,
			final int number, final boolean bonusLife) {
		int rectWidth = screen.getWidth();
		int rectHeight = screen.getHeight() / 6;
		backBufferGraphics.setColor(Color.BLACK);
		backBufferGraphics.fillRect(0, screen.getHeight() / 2 - rectHeight / 2,
				rectWidth, rectHeight);
		backBufferGraphics.setColor(Color.GREEN);
		if (number >= 4)
			if (!bonusLife) {
				if (level == 8){
					drawCenteredBigString(screen, "Boss Stage",
							screen.getHeight() / 2
									+ fontBigMetrics.getHeight() / 3);
				}
				else {
					drawCenteredBigString(screen, "Level " + level,
							screen.getHeight() / 2
									+ fontBigMetrics.getHeight() / 3);
				}

			} else {
				drawCenteredBigString(screen, "Level " + level
						+ " - Bonus life!",
						screen.getHeight() / 2
						+ fontBigMetrics.getHeight() / 3);
			}
		else if (number != 0)
			drawCenteredBigString(screen, Integer.toString(number),
					screen.getHeight() / 2 + fontBigMetrics.getHeight() / 3);
		else
			drawCenteredBigString(screen, "GO!", screen.getHeight() / 2
					+ fontBigMetrics.getHeight() / 3);
	}

	public void drawStageClearScreen(final Screen screen, final int option){
		String continueString = "Continue";
		String saveString = "Save & Exit";

		if(option == 1)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, continueString,
				screen.getHeight() / 3 * 2);

		if(option == 2)
			backBufferGraphics.setColor(Color.GREEN);
		else
			backBufferGraphics.setColor(Color.WHITE);
		drawCenteredRegularString(screen, saveString,
				screen.getHeight() / 3 * 2 + fontRegularMetrics.getHeight() * 2);
	}

}
