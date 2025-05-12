package org.example.model.zombie;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Zombie {
    private String id;
    private int x;
    private int y;
    private int width;
    private int height;
    private int speed = 1; // velocidad
    private long prevMoveTime;
    private int health = 100;
    private long prevAttackTime = System.currentTimeMillis();

    public Zombie(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.prevMoveTime = System.currentTimeMillis();
        this.health = 100; // Asegurar que empiece con 100 de vida
    }

    public void moveLeft() {
        x -= speed;
    }

    public boolean isOutOfScreen() {
        return x + width < 0;
    }

    public void takeDamage(int damage) {
        health -= damage;
        System.out.println("🧟 Zombie golpeado. Vida restante: " + health);
    }
}
