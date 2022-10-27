package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;

/**
 * Implements a bullet that moves vertically up or down.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class Laser extends Entity {

    /**
     * Speed of the bullet, positive or negative depending on direction -
     * positive is down.
     */
    private int speed;

    /**
     * Constructor, establishes the bullet's properties.
     *
     * @param positionX
     *            Initial position of the bullet in the X axis.
     * @param positionY
     *            Initial position of the bullet in the Y axis.
     * @param speed
     *            Speed of the bullet, positive or negative depending on
     *            direction - positive is down.
     */
    public Laser(final int positionX, final int positionY, final int speed) {
        super(positionX, positionY, 3 * 2, 5 * 2, Color.WHITE);

        this.speed = speed;
        setSprite();
    }

    /**
     * Sets correct sprite for the bullet, based on speed.
     */
    public final void setSprite() {
            this.spriteType = SpriteType.Laser;
            this.setColor(Color.RED);
    }

    /**
     * Updates the bullet's position.
     */
    public final void update() {
        this.positionY += this.speed;
    }

    /**
     * Setter of the speed of the bullet.
     *
     * @param speed
     *            New speed of the bullet.
     */
    public final void setSpeed(final int speed) {
        this.speed = speed;
    }
}
