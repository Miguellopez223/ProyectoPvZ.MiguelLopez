package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter
@Setter
public class Hipnoseta extends Plant {
    private long prevTime;

    public Hipnoseta(int x, int y, int width, int height) {
        this.id = UUID.randomUUID().toString();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.prevTime = System.currentTimeMillis();
        this.defenseInitial = 20;
        this.defense = 20;
    }

    @Override
    public String toString() {
        return "Hipnoseta{" +
                "id='" + id + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", defenseInitial=" + defenseInitial +
                ", defense=" + defense +
                '}';
    }
}
