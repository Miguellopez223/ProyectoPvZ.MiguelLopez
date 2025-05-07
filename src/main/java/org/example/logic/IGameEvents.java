package org.example.logic;

import org.example.model.Sun;
import org.example.model.attack.GreenPea;
import org.example.model.plant.Plant;

/**
 * IEventosShapes
 *
 * @author Marcos Quispe
 * @since 1.0
 */
public interface IGameEvents {

    void addPlantUI(Plant plant);

    void throwAttackUI(GreenPea greenPea);

    void updatePositionUI(String id);

    void deleteComponentUI(String id);
    void addSunUI(Sun sun);
    void updateSunPosition(String id);
    void removeSunUI(String id);
    void addZombieUI(org.example.model.zombie.Zombie zombie);
    void clicInYard(int x, int y);


}
