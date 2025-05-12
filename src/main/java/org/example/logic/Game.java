package org.example.logic;

import lombok.Getter;
import org.example.model.Sun;
import org.example.model.attack.Attack;
import org.example.model.attack.GreenPea;
import org.example.model.plant.PeaShooter;
import org.example.model.plant.Plant;
import org.example.model.plant.SunFlower;
import org.example.model.zombie.Zombie;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game {
    public static final int PEA_SHOOTER_WIDTH = 50;
    public static final int PEA_SHOOTER_HEIGHT = 70;
    public static final int PEA_WIDTH = 20;
    public static final int PEA_HEIGHT = 20;

    private int posStartX = 0;
    private int posStartY = 0;
    private int accumulatedSuns = 0;
    private boolean[][] plantsInBoard;

    @Getter
    private List<Plant> availablePlants;
    @Getter
    private List<Plant> plants;
    @Getter
    private List<Attack> attacks;
    private List<Sun> fallingSuns = new CopyOnWriteArrayList<>();
    private List<Zombie> zombies = new ArrayList<>();
    public List<Zombie> getZombies() { return zombies; }
    private List<Zombie> attackingZombies = new ArrayList<>();


    private IGameEvents iGameEvents;

    public Game(IGameEvents iGameEvents) {
        availablePlants = new ArrayList<>();
        plants = new ArrayList<>();
        attacks = new ArrayList<>();
        posStartX = 100;
        posStartY = 100;
        this.iGameEvents = iGameEvents;
    }

    public void createDefaultPeaShooter(int nroPlanta) {
        int widthCuadrante = 100;
        int heightCuadrante = 120;
        int x = posStartX + (widthCuadrante / 2) - (PEA_SHOOTER_WIDTH / 2);
        int y = posStartY + (nroPlanta * heightCuadrante) + (heightCuadrante / 2) - (PEA_SHOOTER_HEIGHT / 2);
        PeaShooter peaShooter = new PeaShooter(x, y, PEA_SHOOTER_WIDTH, PEA_SHOOTER_HEIGHT);
        peaShooter.setDefenseInitial(100);
        peaShooter.setDefense(100);
        plants.add(peaShooter);
        iGameEvents.addPlantUI(peaShooter);
    }

    public void createDefaultSunFlower() {
        int widthCuadrante = 100;
        int heightCuadrante = 120;
        int x = posStartX + widthCuadrante + (widthCuadrante / 2) - (PEA_SHOOTER_WIDTH / 2);
        int y = posStartY + (heightCuadrante / 2) - (PEA_SHOOTER_HEIGHT / 2);
        SunFlower sunFlower = new SunFlower(x, y, PEA_SHOOTER_WIDTH, PEA_SHOOTER_HEIGHT);
        sunFlower.setDefenseInitial(100);
        sunFlower.setDefense(100);
        plants.add(sunFlower);
        iGameEvents.addPlantUI(sunFlower);
    }

    public void reviewPlants() {
        long currentTime = System.currentTimeMillis();

        for (int i = 0; i < plants.size(); i++) {
            if (plants.get(i) instanceof PeaShooter ps) {
                if (currentTime - ps.getPrevTime() > ps.getAttackTime()) {
                    ps.setPrevTime(currentTime);
                    GreenPea p = newGreenPea(ps);
                    attacks.add(p);
                    iGameEvents.throwAttackUI(p);
                }
            } else if (plants.get(i) instanceof SunFlower sf) {
                if (currentTime - sf.getPrevTime() > sf.getSunTime()) {
                    sf.setPrevTime(currentTime);
                    int centerX = sf.getX() + (sf.getWidth() / 2) - 30;
                    int startY = sf.getY() - 60;
                    int finalY = sf.getY() + sf.getHeight();

                    Sun sun = new Sun(centerX, startY, 60, 60);
                    sun.setStopY(finalY);
                    fallingSuns.add(sun);
                    iGameEvents.addSunUI(sun);
                }
            }
        }
    }

    public void reviewAttacks() {
        long currentTime = System.currentTimeMillis();
        List<Attack> toRemove = new ArrayList<>();
        List<Zombie> zombiesToRemove = new ArrayList<>();

        for (Attack attack : new ArrayList<>(attacks)) {
            if (attack instanceof GreenPea gp) {
                if (currentTime - gp.getPrevTime() > gp.getAdvanceTime()) {
                    gp.avanzar();
                    gp.setPrevTime(currentTime);

                    Rectangle peaRect = new Rectangle(gp.getX(), gp.getY(), gp.getWidth(), gp.getHeight());
                    Zombie closestZombie = null;

                    for (Zombie z : zombies) {
                        Rectangle zombieRect = new Rectangle(z.getX(), z.getY(), z.getWidth(), z.getHeight());
                        if (peaRect.intersects(zombieRect)) {
                            if (closestZombie == null || z.getX() < closestZombie.getX()) {
                                closestZombie = z;
                            }
                        }
                    }

                    if (closestZombie != null) {
                        closestZombie.takeDamage(10);
                        if (closestZombie.getHealth() <= 0) {
                            zombiesToRemove.add(closestZombie);
                        }
                        iGameEvents.deleteComponentUI(gp.getId());
                        toRemove.add(gp);
                    } else {
                        if (gp.getX() > gp.getMaxXToDied()) {
                            iGameEvents.deleteComponentUI(gp.getId());
                            toRemove.add(gp);
                        } else {
                            iGameEvents.updatePositionUI(gp.getId());
                        }
                    }
                }
            }
        }

        attacks.removeAll(toRemove);
        for (Zombie z : zombiesToRemove) {
            iGameEvents.removeZombieUI(z.getId());
            zombies.remove(z);
        }
    }

    private GreenPea newGreenPea(PeaShooter plant) {
        int x = plant.getX() + plant.getWidth();
        int y = plant.getY() + (PEA_SHOOTER_HEIGHT / 4) - (PEA_WIDTH / 2) + 4;
        GreenPea gp = new GreenPea(x, y, PEA_WIDTH, PEA_HEIGHT);
        gp.setMaxXToDied(1050);
        return gp;
    }

    public List<Sun> getFallingSuns() {
        return fallingSuns;
    }

    public void generateFallingSun() {
        int x = 100 + (int) (Math.random() * 800);
        Sun sun = new Sun(x, 0, 60, 60);
        sun.setStopY(600);
        fallingSuns.add(sun);
        iGameEvents.addSunUI(sun);
    }

    public void reviewFallingSuns() {
        List<Sun> sunsToRemove = new ArrayList<>();

        for (Sun sun : fallingSuns) {
            if (!sun.hasLanded()) {
                sun.fall();
                iGameEvents.updateSunPosition(sun.getId());
            } else {
                if (sun.getLandedTime() == -1) {
                    sun.setLandedTime(System.currentTimeMillis());
                } else {
                    long tiempoActual = System.currentTimeMillis();
                    long tiempoDesdeQueCayo = tiempoActual - sun.getLandedTime();
                    if (tiempoDesdeQueCayo > 6000) {
                        iGameEvents.removeSunUI(sun.getId());
                        sunsToRemove.add(sun);
                        System.out.println("☀️ Sol eliminado tras " + tiempoDesdeQueCayo + "ms");
                    }
                }
            }
        }

        fallingSuns.removeAll(sunsToRemove);
    }

    public void spawnZombie() {
        int fila = (int) (Math.random() * 5);
        int startY = posStartY + fila * 120;
        Zombie z = new Zombie(1010 + (int)(Math.random() * 50), startY + 10, 60, 80);
        zombies.add(z);
        iGameEvents.addZombieUI(z);
    }

    public void reviewZombies() {
        List<Zombie> toRemove = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (Zombie z : zombies) {
            boolean collided = false;

            for (Plant p : new ArrayList<>(plants)) {
                Rectangle zombieRect = new Rectangle(z.getX(), z.getY(), z.getWidth(), z.getHeight());
                Rectangle plantRect = new Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());

                if (zombieRect.intersects(plantRect)) {
                    collided = true;

                    if (now - z.getPrevAttackTime() >= 500) {  // ⏱️ solo cada 0.5 seg
                        z.setPrevAttackTime(now);  // ⏲️ actualizar tiempo

                        p.setDefense(p.getDefense() - 10);
                        System.out.println("🧟 Zombie atacó a planta, defensa restante: " + p.getDefense());

                        if (p.getDefense() <= 0) {
                            iGameEvents.deleteComponentUI(p.getId());
                            plants.remove(p);
                            System.out.println("🌱 Planta destruida por zombie.");
                        }
                    }

                    break;
                }
            }

            if (!collided) {
                // solo avanza si no está chocando con planta
                if (now - z.getPrevMoveTime() > 220) {
                    z.moveLeft();
                    z.setPrevMoveTime(now);
                    iGameEvents.updateZombieUI(z.getId());

                    if (z.isOutOfScreen()) {
                        toRemove.add(z);
                        iGameEvents.removeZombieUI(z.getId());
                    }
                }
            }
        }

        zombies.removeAll(toRemove);
    }
}
