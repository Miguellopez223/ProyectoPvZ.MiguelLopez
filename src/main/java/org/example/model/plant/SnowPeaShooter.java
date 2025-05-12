package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SnowPeaShooter extends Plant {
    private int attackTime = 1000;
    private long prevTime;

    public SnowPeaShooter(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.prevTime = System.currentTimeMillis();
        this.defenseInitial = 50;
        this.defense = 50;
    }

    @Override
    public String toString() {
        return "SnowPeaShooter{" +
                "attackTime=" + attackTime +
                ", prevTime=" + prevTime +
                ", id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", reloadTime=" + reloadTime +
                ", defenseInitial=" + defenseInitial +
                ", defense=" + defense +
                '}';
    }
}
