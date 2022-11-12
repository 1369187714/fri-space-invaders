package entity;
import java.awt.Color;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;

/**
 * Implements a enemy ship, to be destroyed by the player.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class EnemyShip extends Entity {
	//추가
	Color color_dead = Color.RED;
	private Item.ItemType itemtype;

	private static int hasItem ;
	/** Point value of a type A enemy. */
	//s


	/** Point value of a type A enemy. */
	private static final int A_TYPE_POINTS = 10;
	/** Point value of a type B enemy. */
	private static final int B_TYPE_POINTS = 20;
	/** Point value of a type C enemy. */
	private static final int C_TYPE_POINTS = 30;
	/** Point value of a bonus enemy. */
	private static final int BONUS_TYPE_POINTS = 100;

	private static final int boss_TYPE_POINTS = 200;
	/** Cooldown between sprite changes. */
	private Cooldown animationCooldown;
	/** Checks if the ship has been hit by a bullet. */
	private boolean isDestroyed;
	/** Values of the ship, in points, when destroyed. */
	private int pointValue;

	private final int ITEM_SPEED = 2;

	/** lives of the boss. */
	public int bossLives = 2;

	/**
	 * Constructor, establishes the ship's properties.
	 * 
	 * @param positionX
	 *            Initial position of the ship in the X axis.
	 * @param positionY
	 *            Initial position of the ship in the Y axis.
	 * @param spriteType
	 *            Sprite type, image corresponding to the ship.
	 */
	public EnemyShip(final int positionX, final int positionY,
			final SpriteType spriteType) {
		super(positionX, positionY, 12 * 2, 8 * 2, Color.WHITE);

		//추가
		this.itemtype = null;
		this.hasItem = 0;
		//


		this.spriteType = spriteType;
		this.animationCooldown = Core.getCooldown(500);
		this.isDestroyed = false;

		switch (this.spriteType) {
		case EnemyShipA1:
		case EnemyShipA2:
			this.pointValue = A_TYPE_POINTS;
			break;
		case EnemyShipB1:
		case EnemyShipB2:
			this.pointValue = B_TYPE_POINTS;
			break;
		case EnemyShipC1:
		case EnemyShipC2:
			this.pointValue = C_TYPE_POINTS;
			break;
		default:
			this.pointValue = boss_TYPE_POINTS;
			break;




		}
	}


	//추가
	public void setHasItem(int setHasItem){
		this.hasItem = setHasItem;
	}

	public int getHasItem(){
		return this.hasItem;
	}

	public void setItemType(Item.ItemType itemtype){
		this.itemtype = itemtype;
	}

	public Item.ItemType getItemType(){
		return this.itemtype;
	}

	public void itemDrop(final Set<Item> items){
		items.add(ItemIterator.drop(positionX + this.width/2 , positionY, this.getItemType()));

	}


	//추가


	/**
	 * Constructor, establishes the ship's properties for a special ship, with
	 * known starting properties.
	 */
	public EnemyShip() {
		super(-32, 60, 16 * 2, 7 * 2, Color.RED);

		this.spriteType = SpriteType.EnemyShipSpecial;
		this.isDestroyed = false;
		this.pointValue = BONUS_TYPE_POINTS;

	}
	public EnemyShip (Color color) {
		super(-32,60,16*2,7*2, color);

		this.spriteType = SpriteType.EnemyShipDangerous;
		this.isDestroyed = false;
		this.pointValue = BONUS_TYPE_POINTS;
	}
	/**
	 * Getter for the score bonus if this ship is destroyed.
	 * 
	 * @return Value of the ship.
	 */
	public final int getPointValue() {
		return this.pointValue;
	}

	public void setPointValue(int setpointvalue){
		this.pointValue = setpointvalue;
	}

	/**
	 * Moves the ship the specified distance.
	 * 
	 * @param distanceX
	 *            Distance to move in the X axis.
	 * @param distanceY
	 *            Distance to move in the Y axis.
	 */
	public final void move(final int distanceX, final int distanceY) {
		this.positionX += distanceX;
		this.positionY += distanceY;
	}

	/**
	 * Updates attributes, mainly used for animation purposes.
	 */
	public final void update() {
		if (this.animationCooldown.checkFinished()) {
			this.animationCooldown.reset();

			switch (this.spriteType) {
			case EnemyShipA1:
				this.spriteType = SpriteType.EnemyShipA2;
				break;
			case EnemyShipA2:
				this.spriteType = SpriteType.EnemyShipA1;
				break;
			case EnemyShipB1:
				this.spriteType = SpriteType.EnemyShipB2;
				break;
			case EnemyShipB2:
				this.spriteType = SpriteType.EnemyShipB1;
				break;
			case EnemyShipC1:
				this.spriteType = SpriteType.EnemyShipC2;
				break;
			case EnemyShipC2:
				this.spriteType = SpriteType.EnemyShipC1;
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Destroys the ship, causing an explosion.
	 */
	public final void destroy() {
		/*this.isDestroyed = true;
		this.spriteType = SpriteType.Explosion;*/
		this.isDestroyed = true;
		this.spriteType = spriteType.Explosion;
		this.animationCooldown = Core.getCooldown(1000);
		this.setColor(color_dead);
	}

	/**
	 * Checks if the ship has been destroyed.
	 * 
	 * @return True if the ship has been destroyed.
	 */
	public final boolean isDestroyed() {
		return this.isDestroyed;
	}

	public void setInitPointValue(){
		if(this.spriteType == spriteType.EnemyShipA1 || this.spriteType == spriteType.EnemyShipA2) setPointValue(A_TYPE_POINTS);
		else if(this.spriteType == spriteType.EnemyShipB1 || this.spriteType == spriteType.EnemyShipB2) setPointValue(B_TYPE_POINTS);
		else if(this.spriteType == spriteType.EnemyShipC1 || this.spriteType == spriteType.EnemyShipC2) setPointValue(C_TYPE_POINTS);
		else if(this.spriteType == spriteType.EnemyShipSpecial) setPointValue(BONUS_TYPE_POINTS);
	}
}

