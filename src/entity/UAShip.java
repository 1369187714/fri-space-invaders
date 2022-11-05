package entity;

import java.awt.Color;

import engine.DrawManager.SpriteType;

public class UAShip extends Entity {
    private int speed;
    private int HP;

    public UAShip(final int positionX, final int positionY) {
        super(positionX, positionY, 9 * 2, 9 * 2, Color.PINK);
        this.HP = 2;
        this.speed = -6;
        setSprite();
    }

    public final void setSprite() {
            this.spriteType = SpriteType.UAShip;
    }

    public final void update() {
        this.positionX += this.speed;
    }

    public final void collision(){
        this.HP--;
    }

    public final boolean isDestroy(){
        return this.HP <= 0;
    }
    public void reHp(){
        this.HP = 2;
    }
}
