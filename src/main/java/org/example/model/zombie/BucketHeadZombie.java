package org.example.model.zombie;

public class BucketHeadZombie extends Zombie {

    public BucketHeadZombie(int x, int y, int width, int height) {
        super(x, y, width, height);
        setHealth(200); // más vida
    }
}
