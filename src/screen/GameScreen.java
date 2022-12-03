package screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Timer;
import java.util.TimerTask;
import engine.*;
import entity.*;
import engine.Cooldown;
import engine.Core;
import engine.GameSettings;
import engine.GameState;
import entity.Bullet;
import entity.BulletPool;
import entity.EnemyShip;
import entity.EnemyShipFormation;
import entity.Entity;
import entity.Ship;
import entity.Shield;
import sound.*;

/**
 * Implements the game screen, where the action happens.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameScreen extends Screen {

	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Bonus score for each life remaining at the end of the level. */
	private static final int LIFE_SCORE = 100;
	/** Minimum time between bonus ship's appearances. */
	private static final int BONUS_SHIP_INTERVAL = 20000;
	/** Maximum variance in the time between bonus ship's appearances. */
	private static final int BONUS_SHIP_VARIANCE = 10000;
	/** Time until bonus ship explosion disappears. */
	private static final int BONUS_SHIP_EXPLOSION = 500;
	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	private static final int SEPARATION_LINE_HEIGHT = 40;
	/** Milliseconds during the screen display the item info. */
	private static final int ITEM_DISPLAY_TIME = 2000;

	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());

	/** Current game difficulty settings. */
	private GameSettings gameSettings;
	/** Current difficulty level number. */
	private int level;
	/** Formation of enemy ships. */
	private EnemyShipFormation enemyShipFormation;
	/** Player's ship. */
	private Ship ship;
	private Pointer pointer;
	/** Bonus enemy ship that appears sometimes. */
	private EnemyShip enemyShipSpecial;
	/** Dangerous enemy ship tahat appears sometimes. */
	private EnemyShip enemyShipDangerous;
	/** Minimum time between bonus ship appearances. */
	private Cooldown enemyShipSpecialCooldown;
	/** Minimum time between dangerous ship appearances. */
	private Cooldown enemyShipdangerousCooldown;
	/** Time until bonus ship explosion disappears. */
	private Cooldown enemyShipSpecialExplosionCooldown;
	/** Time until bangerous ship explosion disappears. */
	private Cooldown enemyShipdangerousExplosionCooldown;
	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;
	/** */
	private Cooldown itemInfoCooldown;
	/** Set of all bullets fired by on screen ships. */
	private Set<Bullet> bullets;
	private Set<Laser> lasers;
	private Set<UAShip> uaShips;
	/** Current score. */
	private int score;
	/** Player lives left. */
	public static int lives;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife;

	private ItemManager itemmanager;


	private Item item;

	private ItemPool itempool;

	private Set<Item> itemiterator;

	private Shield shield;

	private int past_countdown=4;

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param gameState
	 *                     Current game state.
	 * @param gameSettings
	 *                     Current game settings.
	 * @param bonusLife
	 *                     Checks if a bonus life is awarded this level.
	 * @param width
	 *                     Screen width.
	 * @param height
	 *                     Screen height.
	 * @param fps
	 *                     Frames per second, frame rate at which the game is run.
	 */
	public GameScreen(final GameState gameState,
			final GameSettings gameSettings, final boolean bonusLife,
			final int width, final int height, final int fps) {
		super(width, height, fps);

		this.gameSettings = gameSettings;
		this.bonusLife = bonusLife;
		this.level = gameState.getLevel();
		this.score = gameState.getScore();
		this.lives = gameState.getLivesRemaining();
		if (this.bonusLife)
			this.lives++;
		if(lives == 0){
			SoundPlay.getInstance().stopBgm();
		}
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();

		this.itemmanager = new ItemManager();

		if (this.itempool == null) {
			this.itempool = new ItemPool();
		}

	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				SoundPlay.getInstance().play(SoundType.inGameBGM);
			}
		};
		Timer timer = new Timer("Timer");
		long delay = 5800L;
		timer.schedule(task, delay);

		enemyShipFormation = new EnemyShipFormation(this.gameSettings);
		itemmanager.assignHasItem(enemyShipFormation);
		enemyShipFormation.attach(this);
		int playerShipShape = FileManager.getPlayerShipShape();
		this.pointer = new Pointer(this.width - 6, this.height - 18, FileManager.getUasnums());
		switch (playerShipShape) {
			case 0:
				this.ship = new Ship(this.width / 2, this.height - 30, FileManager.ChangeIntToColor());
				break;
			case 1:
				this.ship = new Ship(this.width / 2, this.height - 30, playerShipShape, FileManager.ChangeIntToColor());
				break;
			case 2:
				this.ship = new Ship(this.width / 2, this.height - 30, (char) ('0' + playerShipShape),
						FileManager.ChangeIntToColor());
				break;
		}
		// Appears each 10-30 seconds.
		this.enemyShipSpecialCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		// add dangerous Ship
		this.enemyShipdangerousCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipdangerousCooldown.reset();
		this.enemyShipdangerousExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		///////////////////////////////////
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<Bullet>();
		this.lasers = new HashSet<Laser>();
		this.uaShips = new HashSet<UAShip>();
		this.itemiterator = new HashSet<Item>();
		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();

		this.itemInfoCooldown = Core.getCooldown(ITEM_DISPLAY_TIME);
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		SoundPlay.getInstance().stopBgm();
		this.score += LIFE_SCORE * (this.lives - 1);
		this.logger.info("Screen cleared with a score of " + this.score); // 정상 출력
		return this.returnCode;
	}
	public static boolean moveUp = false;
	private static boolean canLaunch = false;
	private static boolean gameStart = false;
	public static void isPressedUp(boolean b){
		if(gameStart)
			moveUp = b;
	}
	public static void isReleasedUp(boolean b){
		if(gameStart)
			canLaunch = b;
	}
	private static void isGameStart(){gameStart = true;}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		if (this.inputDelay.checkFinished() && !this.levelFinished) {

			isGameStart();
			if (!this.ship.isDestroyed()) {
				boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
						|| inputManager.isKeyDown(KeyEvent.VK_D);
				boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
						|| inputManager.isKeyDown(KeyEvent.VK_A);

				boolean isRightBorder = this.ship.getPositionX()
						+ this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
				boolean isLeftBorder = this.ship.getPositionX()
						- this.ship.getSpeed() < 1;

				if (moveRight && !isRightBorder) {
					this.ship.moveRight();
					if (shield != null)
						shield.moveRight();
				}
				if (moveLeft && !isLeftBorder) {
					this.ship.moveLeft();
					if (shield != null)
						shield.moveLeft();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
					if(this.ship.shoot(this.bullets, FileManager.getPlayerShipShape()))
						this.bulletsShot++;
				}
				if (inputManager.isKeyDown(KeyEvent.VK_Q) && FileManager.getPlayerShipShape() == 2) {
					this.ship.lasing(this.lasers);
				}
			}

			boolean isUpBorder = this.pointer.getPositionY()
			< this.height - 440;
			boolean isDownBorder = this.pointer.getPositionY()
			> this.height - 20;

			if (moveUp && !isUpBorder)
				this.pointer.moveUp();

			if (canLaunch) {
				this.pointer.launch(this.uaShips);
				pointer.resetPosition();
				canLaunch = false;
			}

			if (this.enemyShipSpecial != null) {
				if (!this.enemyShipSpecial.isDestroyed())
					this.enemyShipSpecial.move(2, 0);
				else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
					this.enemyShipSpecial = null;

			}
			if (this.enemyShipSpecial == null
					&& this.enemyShipSpecialCooldown.checkFinished()) {
				this.enemyShipSpecial = new EnemyShip();
				this.enemyShipSpecialCooldown.reset();
				this.logger.info("A special ship appears");
			}
			if (this.enemyShipSpecial != null
					&& this.enemyShipSpecial.getPositionX() > this.width) {
				this.enemyShipSpecial = null;
				this.logger.info("The special ship has escaped");
			}

			/** add dangerousShip */
			if (this.enemyShipDangerous != null) {
				if (!this.enemyShipDangerous.isDestroyed())
					this.enemyShipDangerous.move(1, 0);
				else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
					this.enemyShipDangerous = null;

			}
			if (this.enemyShipDangerous == null
					&& this.enemyShipdangerousCooldown.checkFinished()) {
				this.enemyShipDangerous = new EnemyShip(Color.BLUE);
				this.enemyShipdangerousCooldown.reset();
				this.logger.info("A dangerous ship appears");
			}
			if (this.enemyShipDangerous != null
					&& this.enemyShipDangerous.getPositionX() > this.width) {
				this.lives--;
				this.enemyShipDangerous = null;
				this.logger.info("The dangerous ship has escaped and you has lost lives");
			}

			this.ship.update();
			this.pointer.update();
			this.enemyShipFormation.update();
			this.enemyShipFormation.shoot(this.bullets);

		}
		for (Item item : this.itemiterator) {
			if (item != null) {
				manageGetItem(item);
			}
		}
		manageCollisions_uaship();
		manageCollisions();
		manageCollisions_lasing();
		cleanItems();
		cleanBullets();
		cleanLasers();
		cleanUaShips();
		draw();
		if ((this.enemyShipFormation.isEmpty() || this.lives == 0)
				&& !this.levelFinished) {
			this.levelFinished = true;
			this.screenFinishedCooldown.reset();
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished())
			this.isRunning = false;

		if(this.enemyShipFormation.isEmpty()){
			sound.SoundPlay.getInstance().play(SoundType.roundClear);
			this.isRunning = false;
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);
		for(Item item : this.itemiterator) {
			if (item != null) {
				drawManager.drawEntity(item, item.getPositionX(),
						item.getPositionY());
			}
		}
		drawManager.drawEntity(this.ship, this.ship.getPositionX(),
				this.ship.getPositionY());

		if(this.pointer.getUasnums() > 0)
			drawManager.drawEntity(this.pointer, this.pointer.getPositionX(),
				this.pointer.getPositionY());
		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
					this.enemyShipSpecial.getPositionX(),
					this.enemyShipSpecial.getPositionY());

		if (this.enemyShipDangerous != null)
			drawManager.drawEntity(this.enemyShipDangerous,
					this.enemyShipDangerous.getPositionX(),
					this.enemyShipDangerous.getPositionY());
		if (shield != null) {
			drawManager.drawEntity(shield, shield.getPositionX(), shield.getPositionY());
		}

		if (itempool.getItem() != null && this.shield != null &&
				itempool.getItem().getItemType() == Item.ItemType.ShieldItem) {
			drawManager.drawEntity(shield, shield.getPositionX(),
					shield.getPositionY());
		}

		enemyShipFormation.draw();

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(),
					bullet.getPositionY());

		for (Laser laser : this.lasers)
			drawManager.drawEntity(laser, laser.getPositionX(),
					laser.getPositionY());

		for (UAShip uaShip : this.uaShips)
			drawManager.drawEntity(uaShip, uaShip.getPositionX(),
					uaShip.getPositionY());
		// Interface.
		drawManager.drawScore(this, this.score);
		drawManager.drawUAShips(this, this.pointer.getUasnums());
		drawManager.drawLives(this, this.lives);
		int playerShipShape = FileManager.getPlayerShipShape();
		if(playerShipShape == 2)
			drawManager.drawLasingCD(this, this.ship);
		drawManager.drawShootCD(this, this.ship);
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);

		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY
					- (System.currentTimeMillis()
							- this.gameStartTime))
					/ 1000);
			drawManager.drawCountDown(this, this.level, countdown,
					this.bonusLife);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height
					/ 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height
					/ 12);
			if ((past_countdown > countdown)) {
				ExecutorService executorService = Executors.newCachedThreadPool();
				executorService.submit(() -> {
					past_countdown = countdown;
					if(countdown==0) sound.SoundPlay.getInstance().play(SoundType.roundStart);
					if(countdown>0) sound.SoundPlay.getInstance().play(SoundType.roundCounting);
				});
				executorService.shutdown();
			}
		}

		if(!this.itemInfoCooldown.checkFinished()){
			drawManager.drawItemInfo(this, this.returnCode);
		}

		drawManager.completeDrawing(this);
	}

	/**
	 * Cleans bullets that go off screen.
	 */
	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
					|| bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	private void cleanUaShips() {
		Set<UAShip> recyclable = new HashSet<UAShip>();
		for (UAShip u : this.uaShips) {
			u.update();
			if (u.getPositionX() < 0) {
				recyclable.add(u);
			}
		}
		this.uaShips.removeAll(recyclable);
	}

	private void cleanLasers() {
		Set<Laser> recyclable = new HashSet<Laser>();
		for (Laser laser : this.lasers) {
			laser.update();
			if (laser.getPositionY() < SEPARATION_LINE_HEIGHT)
				recyclable.add(laser);
		}
		this.lasers.removeAll(recyclable);
	}

	/**
	 * Manages collisions between bullets and ships.
	 */
	private void manageCollisions() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets)
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished) {
					recyclable.add(bullet);


					if (shield == null && !this.ship.isDestroyed()) {
						SoundPlay.getInstance().play(SoundType.hit);
						this.ship.destroy();
						this.lives--;
						this.logger.info("Hit on player ship, " + this.lives
								+ " lives remaining.");
							this.clearItem();

					} else if (!this.ship.isDestroyed()) {
						shield = null;
					}
				}
			} else {
				for (EnemyShip enemyShip : this.enemyShipFormation)
					if (!enemyShip.isDestroyed()
							&& checkCollision(bullet, enemyShip)) {
						SoundPlay.getInstance().play(SoundType.enemyKill);
						this.score += enemyShip.getPointValue();
						this.shipsDestroyed++;

						if (enemyShip.getItemType() != null) {
							enemyShip.itemDrop(itemiterator);
							for (Item item : this.itemiterator)
								if (item != null)
									item.setSprite();
						}

						this.enemyShipFormation.destroy(enemyShip);
						recyclable.add(bullet);
					}
				if (this.enemyShipSpecial != null
						&& !this.enemyShipSpecial.isDestroyed()
						&& checkCollision(bullet, this.enemyShipSpecial)) {
					SoundPlay.getInstance().play(SoundType.bonusEnemyKill);
					this.score += this.enemyShipSpecial.getPointValue();

					this.shipsDestroyed++;
					this.enemyShipSpecial.destroy();
					this.enemyShipSpecialExplosionCooldown.reset();
					recyclable.add(bullet);
				}
				if (this.enemyShipDangerous != null
						&& !this.enemyShipDangerous.isDestroyed()
						&& checkCollision(bullet, this.enemyShipDangerous)) {
					SoundPlay.getInstance().play(SoundType.bonusEnemyKill);
					this.score += this.enemyShipDangerous.getPointValue();
					this.shipsDestroyed++;
					this.enemyShipDangerous.destroy();
					this.enemyShipdangerousExplosionCooldown.reset();
					recyclable.add(bullet);
				}
			}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}
	private void manageCollisions_lasing() {
		Set<Laser> recyclable = new HashSet<Laser>();
		for (Laser laser : this.lasers)
				for (EnemyShip enemyShip : this.enemyShipFormation) {
					if (!enemyShip.isDestroyed()
							&& checkCollision_lasing(laser, enemyShip)) {
						SoundPlay.getInstance().play(SoundType.enemyKill);
						this.score += enemyShip.getPointValue();
						this.shipsDestroyed++;


						if (enemyShip.getItemType() != null) {
							enemyShip.itemDrop(itemiterator);
							for (Item item : this.itemiterator)
								if (item != null)
									item.setSprite();
						}

						this.enemyShipFormation.destroy(enemyShip);
						recyclable.add(laser);
					}
					if (this.enemyShipSpecial != null
							&& !this.enemyShipSpecial.isDestroyed()
							&& checkCollision_lasing(laser, this.enemyShipSpecial)) {
						SoundPlay.getInstance().play(SoundType.bonusEnemyKill);
						this.score += this.enemyShipSpecial.getPointValue();

						this.shipsDestroyed++;
						this.enemyShipSpecial.destroy();
						this.enemyShipSpecialExplosionCooldown.reset();
						recyclable.add(laser);
					}
					if (this.enemyShipDangerous != null
							&& !this.enemyShipDangerous.isDestroyed()
							&& checkCollision_lasing(laser, this.enemyShipDangerous)) {
						SoundPlay.getInstance().play(SoundType.bonusEnemyKill);
						this.score += this.enemyShipDangerous.getPointValue();
						this.shipsDestroyed++;
						this.enemyShipDangerous.destroy();
						this.enemyShipdangerousExplosionCooldown.reset();
						recyclable.add(laser);
					}
				}
	}
	private void manageCollisions_uaship() {
		Set<UAShip> recyclable = new HashSet<UAShip>();
		for (UAShip uaShip : this.uaShips){
				for (EnemyShip enemyShip : this.enemyShipFormation)
					if (!enemyShip.isDestroyed()
							&& checkCollision_uaship(uaShip, enemyShip)) {
						SoundPlay.getInstance().play(SoundType.enemyKill);
						this.score += enemyShip.getPointValue();
						this.shipsDestroyed++;

						if (enemyShip.getItemType() != null) {
							enemyShip.itemDrop(itemiterator);
							for (Item item : this.itemiterator)
								if (item != null)
									item.setSprite();
						}
						this.enemyShipFormation.destroy(enemyShip);
						uaShip.collision();
						if(uaShip.isDestroy())
							recyclable.add(uaShip);
					}
				if (this.enemyShipSpecial != null
						&& !this.enemyShipSpecial.isDestroyed()
						&& checkCollision_uaship(uaShip, this.enemyShipSpecial)) {
					SoundPlay.getInstance().play(SoundType.bonusEnemyKill);
					this.score += this.enemyShipSpecial.getPointValue();

					this.shipsDestroyed++;
					this.enemyShipSpecial.destroy();
					this.enemyShipSpecialExplosionCooldown.reset();
					uaShip.collision();
					if(uaShip.isDestroy())
						recyclable.add(uaShip);
				}
				if (this.enemyShipDangerous != null
						&& !this.enemyShipDangerous.isDestroyed()
						&& checkCollision_uaship(uaShip, this.enemyShipDangerous)) {
					SoundPlay.getInstance().play(SoundType.bonusEnemyKill);
					this.score += this.enemyShipDangerous.getPointValue();
					this.shipsDestroyed++;
					this.enemyShipDangerous.destroy();
					this.enemyShipdangerousExplosionCooldown.reset();
					uaShip.collision();
					if(uaShip.isDestroy())
						recyclable.add(uaShip);
				}
			}
		this.uaShips.removeAll(recyclable);
	}
	/**
	 * Checks if two entities are colliding.
	 * 
	 * @param a
	 *          First entity, the bullet.
	 * @param b
	 *          Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final Entity a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}
	private boolean checkCollision_lasing(final Entity a, final Entity b) {
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int distanceX = Math.abs(centerAX - centerBX);

		return distanceX < maxDistanceX;
	}
	private boolean checkCollision_uaship(final UAShip a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);
		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 * 
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return new GameState(this.level, this.score, this.lives,
				this.bulletsShot, this.shipsDestroyed);
	}

	private void manageGetItem(Item item) {
		if (checkCollision(item, this.ship) && !this.levelFinished) {

			itempool.add(item);
			item.setSprite();

			if (item.getIsget() == false &&
					itempool.getItem().getItemType() == Item.ItemType.BulletSpeedItem) {

				this.returnCode = 0;
				this.clearItem();//효과초기화
				this.clearPointUp();
				this.itemInfoCooldown.reset();

				this.ship.setBulletSpeed(2 * ship.getBulletSpeed());

			}
			else if (item.getIsget() == false &&
					itempool.getItem().getItemType() == Item.ItemType.PointUpItem) {

				this.returnCode = 1;
				this.clearItem();//효과 초기화
				this.itemInfoCooldown.reset();
				for (EnemyShip enemyShip : this.enemyShipFormation)
					enemyShip.setPointValue(2 * enemyShip.getPointValue());
			}
			else if (item.getIsget() == false &&
					itempool.getItem().getItemType() == Item.ItemType.MachineGun) {

				LOGGER.info("Obtained MachineGun");

				this.clearItem();//효과 초기화

				this.ship.setShootingInterval(0.1 * this.ship.getShootingInterval());

			}
			else if (item.getIsget() == false &&
					itempool.getItem().getItemType() == Item.ItemType.ShieldItem) {

				this.returnCode = 2;
				this.clearItem();
				this.clearPointUp();
				this.itemInfoCooldown.reset();
				shield = new Shield(this.ship.getPositionX() - 2, this.ship.getPositionY() - 6, this.ship);

			}
			else if (item.getIsget() == false &&
					itempool.getItem().getItemType() == Item.ItemType.SpeedUpItem) {

				this.returnCode = 3;
				this.clearItem();//효과 초기화
				this.clearPointUp();
				this.itemInfoCooldown.reset();
				this.ship.setShipSpeed(2 * this.ship.getSpeed());
			}
//			else if (item.getIsget() == false &&
//					itempool.getItem().getItemType() == Item.ItemType.EnemyShipSpeedItem) {
//
//				this.returnCode = 4;
//				this.clearItem();//효과 초기화
//				this.clearPointUp();
//				this.itemInfoCooldown.reset();
//				this.enemyShipFormation.setMovementSpeed(5 * this.enemyShipFormation.getMovementSpeed());
//
//			}
			else if (item.getIsget() == false &&
					itempool.getItem().getItemType() == Item.ItemType.ExtraLifeItem) {

				this.clearItem();
				this.clearPointUp();
				if (this.lives < 4) {
					this.lives++;
					this.returnCode = 5;
					this.itemInfoCooldown.reset();
				}
				else
					LOGGER.warning("생명 4개 초과");
			}

			item.isGet(true);
		}
	}
	/**
	 * initalize obtained item's effects.
	 */
	public void clearItem(){
		ship.setInitState();
		shield = null;
	}
	/**
	 * Removing item that already obtained, or already get out of the gamescreen.
	 */
	private void cleanItems() {
		Set<Item> recyclable = new HashSet<Item>();
		for (Item item : this.itemiterator) {
			item.update();
			if (item.getPositionY() < SEPARATION_LINE_HEIGHT
					|| item.getPositionY() > this.height)
				recyclable.add(item);
		}
		this.itemiterator.removeAll(recyclable);
	}
	/**
	 * initalize pointupitem effects.
	 */
	public void clearPointUp(){
		for (EnemyShip enemyShip : this.enemyShipFormation)
			enemyShip.setInitPointValue();
	}
}