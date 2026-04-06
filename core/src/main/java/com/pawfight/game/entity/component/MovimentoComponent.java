package com.pawfight.game.entity.component;

import com.badlogic.gdx.math.Rectangle;

public class MovimentoComponent {

    private float nextX;
    private float nextY;
    private float speed;
    private boolean moving;
    private boolean olhandoEsquerda;

    public void update(Rectangle hitBox, int velocidade, float delta, InputComponent input) {
        moving = false;
        speed = velocidade * delta;

        nextX = hitBox.x;
        nextY = hitBox.y;

        if (input.isMoveRight()) {
            nextX += speed;
            olhandoEsquerda = false;
            moving = true;
        }
        if (input.isMoveLeft()) {
            nextX -= speed;
            olhandoEsquerda = true;
            moving = true;
        }
        if (input.isMoveUp()) {
            nextY += speed;
            moving = true;
        }
        if (input.isMoveDown()) {
            nextY -= speed;
            moving = true;
        }
    }

    public float getNextX() { return nextX; }
    public float getNextY() { return nextY; }
    public float getSpeed() { return speed; }
    public boolean isMoving() { return moving; }
    public boolean isOlhandoEsquerda() { return olhandoEsquerda; }
}
