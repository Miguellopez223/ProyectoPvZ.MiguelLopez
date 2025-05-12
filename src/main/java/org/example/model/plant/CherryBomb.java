package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CherryBomb extends Plant {
    private long prevTime;
    public CherryBomb(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.defenseInitial = 100;
        this.defense = 100;
        this.prevTime = System.currentTimeMillis(); // <- importante
    }

    @Override
    public String toString() {
        return "CherryBomb{" +
                "id='" + id + '\'' +
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
