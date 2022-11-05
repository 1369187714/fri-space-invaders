package entity;

import java.awt.Color;
import java.util.Set;

import engine.Cooldown;
import engine.DrawManager.SpriteType;
import engine.Core;
import engine.FileManager;


public class Pointer extends Entity {
    private double SPEED;
    private int uasnums;
    private Cooldown launchingCooldown;
    public int getLAUNCHING_INTERVAL() {
        return LAUNCHING_INTERVAL;
    }
    private int LAUNCHING_INTERVAL = 600;
    private int defaultX;
    private int defaultY;
    public Pointer(final int positionX, final int positionY, int uasnums) {
        super(positionX, positionY, 3 * 2, 1 * 2, Color.PINK);
        this.defaultX = positionX;
        this.defaultY = positionY;
        this.launchingCooldown = Core.getCooldown(LAUNCHING_INTERVAL);
        this.uasnums = uasnums;
        this.SPEED = 6;
        this.spriteType = SpriteType.Pointer;
    }
    public final void moveUp()
    {
        this.positionY -= SPEED;
    }
    public final void moveDown()
    {
        this.positionY += SPEED;
    }
    public final void launch(final Set<UAShip> uaShips) {
        if (this.launchingCooldown.checkFinished() && this.uasnums > 0) {
            FileManager.setUaShipNum(false);
            this.launchingCooldown.reset();
            this.uasnums--;
            uaShips.add(BulletPool.getUAShip(positionX + this.width / 2, positionY - 6));
//            SoundPlay.getInstance().play(SoundType.shoot);
        }
    }
    public final void update() {
            this.spriteType = SpriteType.Pointer;
    }
    public int getUasnums() {
        return uasnums;
    }
    public void resetXY() {
        this.positionY = defaultY;
        this.positionX = defaultX;
    }
}
