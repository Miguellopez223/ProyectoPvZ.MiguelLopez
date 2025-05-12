package org.example.logic;

import org.example.model.Sun;
import org.example.model.attack.GreenPea;
import org.example.model.plant.Plant;
import org.example.model.zombie.Zombie;

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
    void addZombieUI(Zombie z);
    void updateZombieUI(String id);
    void removeZombieUI(String id);

}
