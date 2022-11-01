package entity;

import java.awt.Color;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;
import engine.FileManager;
import sound.SoundPlay;
import sound.SoundType;
//import entity.Shield;

/**
 * Implements a ship, to be controlled by the player.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Ship extends Entity {

	private static engine.Core Core;
	/** Time between shots. */
	private int SHOOTING_INTERVAL, INIT_SHOOTING_INTERVAL;
	/** Speed of the bullets shot by the ship. */
	private int BULLET_SPEED, INIT_BULLET_SPEED;
	/** Movement of the ship for each unit of time. */
	private double SPEED, INIT_SPEED;

	/** Minimum time between shots. */
	private Cooldown shootingCooldown;
	/** Time spent inactive between hits. */
	private Cooldown destructionCooldown;

//	private Shield shield;

	/**
	 * Constructor, establishes the ship's properties.
	 * 
	 * @param positionX
	 *            Initial position of the ship in the X axis.
	 * @param positionY
	 *            Initial position of the ship in the Y axis.
	 */
	public Ship(final int positionX, final int positionY, Color shipColor) {
		super(positionX, positionY, 13 * 2, 8 * 2, shipColor);

		this.SHOOTING_INTERVAL = INIT_SHOOTING_INTERVAL = 750;
		this.BULLET_SPEED = INIT_BULLET_SPEED = -6;
		this.SPEED = INIT_SPEED = 2;
		this.spriteType = SpriteType.Ship;
		this.shootingCooldown = Core.getCooldown(SHOOTING_INTERVAL);
		this.destructionCooldown = Core.getCooldown(1000);
	}
	public Ship(final int positionX, final int positionY, int shipShape, Color shipColor) {
        super(positionX, positionY, 13 * 2, 8 * 2, shipColor);

		this.SHOOTING_INTERVAL = INIT_SHOOTING_INTERVAL = 700;
		this.BULLET_SPEED = INIT_BULLET_SPEED = -6;
		this.SPEED = INIT_SPEED = 3;
		this.spriteType = SpriteType.Ship;
		this.shootingCooldown = Core.getCooldown(SHOOTING_INTERVAL);
		this.destructionCooldown = Core.getCooldown(1000);
    }
	public Ship(final int positionX, final int positionY, char shipShape, Color shipColor) {
		super(positionX, positionY, 13 * 2, 8 * 2, shipColor);

		this.SHOOTING_INTERVAL = INIT_SHOOTING_INTERVAL = 650;
		this.BULLET_SPEED = INIT_BULLET_SPEED = -8;
		this.SPEED = INIT_SPEED = 3;
		this.spriteType = SpriteType.Ship;
		this.shootingCooldown = Core.getCooldown(SHOOTING_INTERVAL);
		this.destructionCooldown = Core.getCooldown(1000);
	}





	/**
	 * Moves the ship speed uni ts right, or until the right screen border is
	 * reached.
	 */
	public final void moveRight()
	{
		this.positionX += SPEED;
//		shield.moveRight();
	}

	/**
	 * Moves the ship speed units left, or until the left screen border is
	 * reached.
	 */
	public final void moveLeft()
	{
		this.positionX -= SPEED;
//		shield.moveRight();
	}

	/**
	 * Shoots a bullet upwards.
	 * 
	 * @param bullets
	 *            List of bullets on screen, to add the new bullet.
	 * @return Checks if the bullet was shot correctly.
	 */
	public final boolean shoot(final Set<Bullet> bullets) {
		if (this.shootingCooldown.checkFinished()) {
			this.shootingCooldown.reset();
			bullets.add(BulletPool.getBullet(positionX + this.width / 2,
					positionY, BULLET_SPEED));
			SoundPlay.getInstance().play(SoundType.shoot);
			return true;
		}
		return false;
	}
	// n~0
	public final int getShotCD(){
		int CD = this.shootingCooldown.showCD();
		return CD < 0 ? 0 : CD;
	}

	/**
	 * Updates status of the ship.
	 */
	public final void update() {
		if (!this.destructionCooldown.checkFinished())
			this.spriteType = SpriteType.ShipDestroyed;
		else
			this.spriteType = SpriteType.Ship;
	}

	/**
	 * Switches the ship to its destroyed state.
	 */
	public final void destroy() {
		this.destructionCooldown.reset();
	}

	/**
	 * Checks if the ship is destroyed.
	 * 
	 * @return True if the ship is currently destroyed.
	 */
	public final boolean isDestroyed() {
		return !this.destructionCooldown.checkFinished();
	}

	/**
	 * Getter for the ship's speed.
	 * 
	 * @return Speed of the ship.
	 */
	public final double getSpeed() {
		return SPEED;
	}
	public final int getShootingInterval() {return SHOOTING_INTERVAL;}
	public final int getBulletSpeed() {return BULLET_SPEED;}
	public void setShootingInterval(double setshootinterval){
		SHOOTING_INTERVAL = (int)setshootinterval;
		this.shootingCooldown = Core.getCooldown((int)setshootinterval);
	}
	public void setBulletSpeed(int setbulletspeed){BULLET_SPEED = setbulletspeed;}
	public void setShipSpeed(double setshipspeed) {SPEED = setshipspeed;}

	public void setInitState(){
		SPEED = INIT_SPEED;
		this.shootingCooldown = engine.Core.getCooldown(INIT_SHOOTING_INTERVAL);
		BULLET_SPEED = INIT_BULLET_SPEED;
	}

}
