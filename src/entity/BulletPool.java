package entity;

import java.util.HashSet;
import java.util.Set;

/**
 * Implements a pool of recyclable bullets.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class BulletPool {

	/** Set of already created bullets. */
	private static Set<Bullet> pool = new HashSet<Bullet>();
	private static Set<Laser> Lpool = new HashSet<Laser>();
	private static Set<UAShip> Upool = new HashSet<UAShip>();
	/**
	 * Constructor, not called.
	 */
	private BulletPool() {

	}

	/**
	 * Returns a bullet from the pool if one is available, a new one if there
	 * isn't.
	 * 
	 * @param positionX
	 *            Requested position of the bullet in the X axis.
	 * @param positionY
	 *            Requested position of the bullet in the Y axis.
	 * @param speed
	 *            Requested speed of the bullet, positive or negative depending
	 *            on direction - positive is down.
	 * @return Requested bullet.
	 */
	public static Bullet getBullet(final int positionX,
			final int positionY, final int speed) {
		Bullet bullet;
		if (!pool.isEmpty()) {
			bullet = pool.iterator().next();
			pool.remove(bullet);
			bullet.setPositionX(positionX - bullet.getWidth() / 2);
			bullet.setPositionY(positionY);
			bullet.setSpeed(speed);
			bullet.setSprite();
		} else {
			bullet = new Bullet(positionX, positionY, speed);
			bullet.setPositionX(positionX - bullet.getWidth() / 2);
		}
		return bullet;
	}
	public static Laser getLaser(final int positionX,
									  final int positionY, final int speed) {
		Laser laser;
		if (!Lpool.isEmpty()) {
			laser = Lpool.iterator().next();
			Lpool.remove(laser);
			laser.setPositionX(positionX - laser.getWidth() / 2);
			laser.setPositionY(positionY);
			laser.setSpeed(speed);
			laser.setSprite();
		} else {
			laser = new Laser(positionX, positionY, speed);
			laser.setPositionX(positionX - laser.getWidth() / 2);
		}
		return laser;
	}

	public static UAShip getUAShip(final int positionX,
								 final int positionY) {
		UAShip u;
		if (!Upool.isEmpty()) {
			u = Upool.iterator().next();
			Upool.remove(u);
			u.setPositionX(positionX - u.getWidth() / 2);
			u.setPositionY(positionY);
			u.setSprite();
		} else {
			u = new UAShip(positionX, positionY);
			u.setPositionX(positionX - u.getWidth() / 2);
		}
		return u;
	}
	/**
	 * Adds one or more bullets to the list of available ones.
	 * 
	 * @param bullet
	 *            Bullets to recycle.
	 */
	public static void recycle(final Set<Bullet> bullet) {
		pool.addAll(bullet);
	}
}
