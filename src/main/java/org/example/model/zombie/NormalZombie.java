package org.example.model.zombie;

import java.util.UUID;

public class NormalZombie extends Zombie {

    private int advanceTime = 500; // cada 500 ms
    private long prevTime;

    public NormalZombie(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.prevTime = System.currentTimeMillis();
        this.defenseInitial = 100;
        this.defense = 100;
    }

    public boolean canAdvance() {
        return System.currentTimeMillis() - prevTime > advanceTime;
    }

    public void avanzar() {
        this.x -= 1; // se mueve a la izquierda
        this.prevTime = System.currentTimeMillis();
    }
}
