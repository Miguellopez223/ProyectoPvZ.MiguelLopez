package org.example.model.zombie;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Zombie {
    protected String id;
    protected int x, y, width, height;
    protected int defenseInitial;
    protected int defense;
}
