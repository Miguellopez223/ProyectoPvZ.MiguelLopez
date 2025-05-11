package org.example.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class Sun {
    private String id;
    private int x;
    private int y;
    private int width;
    private int height;
    private long landedTime = -1;
    private int stopY = 600; // por defecto el suelo

    public Sun(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean hasLanded() {
        return y >= stopY;
    }

    public long getLandedTime() {
        return landedTime;
    }

    public boolean shouldDisappear() {
        return landedTime > 0 && System.currentTimeMillis() - landedTime > 6000;
    }

    public void fall() {
        y += 1; // o y += 2 si quieres que caiga más rápido
    }

}
