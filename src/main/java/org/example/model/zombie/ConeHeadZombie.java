package org.example.model.zombie;

public class ConeHeadZombie extends Zombie {

    public ConeHeadZombie(int x, int y, int width, int height) {
        super(x, y, width, height);
        setHealth(150); // más vida
    }
}
