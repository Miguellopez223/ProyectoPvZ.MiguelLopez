package org.example.logic;

import lombok.Getter;
import org.example.model.Sun;
import org.example.model.attack.Attack;
import org.example.model.plant.PeaShooter;
import org.example.model.attack.GreenPea;
import org.example.model.plant.Plant;
import org.example.model.plant.SunFlower;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Game
 *
 * @author Marcos Quispe
 * @since 1.0
 */
public class Game {
    public static final int PEA_SHOOTER_WIDTH = 50;
    public static final int PEA_SHOOTER_HEIGHT = 70;
    public static final int PEA_WIDTH = 20;
    public static final int PEA_HEIGHT = 20;

    private int posStartX = 0;
    private int posStartY = 0;
    private int accumulatedSuns = 0;
    private boolean[][] plantsInBoard; // para saber si una planta esta en el tablero

    @Getter
    private List<Plant> availablePlants;
    @Getter
    private List<Plant> plants;
    @Getter
    private List<Attack> attacks;
    private List<Sun> fallingSuns = new java.util.concurrent.CopyOnWriteArrayList<>();

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
        plants.add(peaShooter);
        iGameEvents.addPlantUI(peaShooter);
    }

    public void createDefaultSunFlower() {
        int widthCuadrante = 100;
        int heightCuadrante = 120;
        int x = posStartX + widthCuadrante + (widthCuadrante / 2) - (PEA_SHOOTER_WIDTH / 2);
        int y = posStartY + (heightCuadrante / 2) - (PEA_SHOOTER_HEIGHT / 2);
        SunFlower sunFlower = new SunFlower(x, y, PEA_SHOOTER_WIDTH, PEA_SHOOTER_HEIGHT);
        plants.add(sunFlower);
        iGameEvents.addPlantUI(sunFlower);
    }

    public void reviewPlants() {
        long currentTime = System.currentTimeMillis();

        // REVIEW PLANTS
        for (int i = 0; i < plants.size(); i++) {
            if (plants.get(i) instanceof PeaShooter ps) {
                if (currentTime - ps.getPrevTime() > ps.getAttackTime()) {
                    ps.setPrevTime(currentTime);
                    GreenPea p = newGreenPea(ps);
                    attacks.add(p);
                    iGameEvents.throwAttackUI(p);
                    //System.out.println("projectile: " + p);
                }
            } else if (plants.get(i) instanceof SunFlower sf) {
                if (currentTime - sf.getPrevTime() > sf.getSunTime()) {
                    sf.setPrevTime(currentTime);
                    int centerX = sf.getX() + (sf.getWidth() / 2) - 30; // centrado
                    int startY = sf.getY() - 60; // encima de la planta
                    int finalY = sf.getY() + sf.getHeight(); // pie de la planta

                    Sun sun = new Sun(centerX, startY, 60, 60);
                    sun.setStopY(finalY); // indica que debe detenerse en la base del girasol
                    fallingSuns.add(sun);
                    iGameEvents.addSunUI(sun);


                }
            }
        }
    }

    public void reviewAttacks() {
        long currentTime = System.currentTimeMillis();

        // REVIEW ATTACKS
        List<Integer> indexDeletes = new ArrayList<>();
        int i = 0;
        boolean avanzoAlgunProjectile = false;
        for (Attack attack : attacks) {
            if (attack instanceof GreenPea gp) {
                if (currentTime - gp.getPrevTime() > gp.getAdvanceTime()) {
                    gp.avanzar();
                    avanzoAlgunProjectile = true;
                    if (gp.getX() > gp.getMaxXToDied()) {
                        indexDeletes.add(i);
                        iGameEvents.deleteComponentUI(attack.getId());
                    } else {
                        iGameEvents.updatePositionUI(gp.getId());
                    }
                    gp.setPrevTime(currentTime);
                    i++;
                }
            }
        }
        indexDeletes.forEach(index -> attacks.remove(index));
        //if (avanzoAlgunProjectile)
        //System.out.println("avance time: " + (System.currentTimeMillis() - currentTime) + "ms. projectiles: " + projectiles.size());
    }

    private GreenPea newGreenPea(PeaShooter plant) {
        int x = plant.getX() + plant.getWidth();
        int y = plant.getY() + (PEA_SHOOTER_HEIGHT / 4) - (PEA_WIDTH / 2) + 4;
        GreenPea gp = new GreenPea(x, y, PEA_WIDTH, PEA_HEIGHT);
        gp.setMaxXToDied(800);
        return gp;
    }


    public List<Sun> getFallingSuns() {
        return fallingSuns;
    }

    public void generateFallingSun() {
        int x = 100 + (int) (Math.random() * 800);
        Sun sun = new Sun(x, 0, 60, 60);
        sun.setStopY(600); // <= FUNDAMENTAL PARA QUE TOQUE EL SUELO
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

        // 🔥 eliminar fuera del bucle
        fallingSuns.removeAll(sunsToRemove);
    }



}
