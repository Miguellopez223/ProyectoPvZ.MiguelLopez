package org.example.logic;

import lombok.Getter;
import org.example.model.Sun;
import org.example.model.attack.Attack;
import org.example.model.attack.GreenPea;
import org.example.model.plant.CherryBomb;
import org.example.model.plant.PeaShooter;
import org.example.model.plant.Plant;
import org.example.model.plant.SunFlower;
import org.example.model.zombie.BucketHeadZombie;
import org.example.model.zombie.ConeHeadZombie;
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
    private final int cellWidth = 102;
    private final int cellHeight = 125;


    public boolean[][] getPlantsInBoard() {
        return plantsInBoard;
    }


    public Game(IGameEvents iGameEvents) {
        availablePlants = new ArrayList<>();
        plants = new ArrayList<>();
        attacks = new ArrayList<>();
        fallingSuns = new CopyOnWriteArrayList<>();
        zombies = new ArrayList<>();
        attackingZombies = new ArrayList<>();

        posStartX = 100;
        posStartY = 100;
        plantsInBoard = new boolean[5][9]; // 5 filas, 9 columnas
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
        List<Plant> plantsToRemove = new ArrayList<>();

        for (Plant plant : new ArrayList<>(plants)) {
            if (plant instanceof PeaShooter ps) {
                int filaPlanta = (ps.getY() - posStartY + ps.getHeight() / 2) / 120;

                boolean zombieEnFilaVisible = zombies.stream().anyMatch(z ->
                        (z.getY() - posStartY + z.getHeight() / 2) / 120 == filaPlanta && z.getX() < 1000
                );

                if (zombieEnFilaVisible && currentTime - ps.getPrevTime() > ps.getAttackTime()) {
                    ps.setPrevTime(currentTime);
                    GreenPea p = newGreenPea(ps);
                    attacks.add(p);
                    iGameEvents.throwAttackUI(p);
                }

            } else if (plant instanceof SunFlower sf) {
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

            } else if (plant instanceof CherryBomb cherry) {
                if (currentTime - cherry.getPrevTime() > 1000) {
                    int centerX = cherry.getX() + cherry.getWidth() / 2;
                    int centerY = cherry.getY() + cherry.getHeight() / 2;
                    int explosionRadius = 130;

                    List<Zombie> zombiesToDamage = new ArrayList<>();
                    for (Zombie z : zombies) {
                        int zx = z.getX() + z.getWidth() / 2;
                        int zy = z.getY() + z.getHeight() / 2;
                        double distancia = Math.hypot(centerX - zx, centerY - zy);
                        if (distancia <= explosionRadius) {
                            zombiesToDamage.add(z);
                        }
                    }

                    for (Zombie z : zombiesToDamage) {
                        z.takeDamage(300);
                        if (z.getHealth() <= 0) {
                            iGameEvents.removeZombieUI(z.getId());
                            zombies.remove(z);
                            System.out.println("💥 Zombie eliminado por Cherry Bomb");
                        }
                    }

                    iGameEvents.deleteComponentUI(plant.getId());
                    int row = (plant.getY() - posStartY + plant.getHeight() / 2) / cellHeight;
                    int col = (plant.getX() - posStartX + plant.getWidth() / 2) / cellWidth;
                    plantsInBoard[row][col] = false;

                    plantsToRemove.add(plant); // marcar para eliminar luego
                    System.out.println("🍒 Cherry Bomb explotó y fue eliminada");
                }
            }
        }

        // 🔥 Eliminar plantas marcadas, fuera del bucle principal
        plants.removeAll(plantsToRemove);
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

        Zombie z;
        double r = Math.random();
        if (r < 0.33) {
            z = new Zombie(1010 + (int)(Math.random() * 50), startY + 10, 60, 80); // 100 vida
        } else if (r < 0.66) {
            z = new ConeHeadZombie(1010 + (int)(Math.random() * 50), startY + 10, 60, 80); // 150 vida
        } else {
            z = new BucketHeadZombie(1010 + (int)(Math.random() * 50), startY + 10, 60, 80); // 200 vida
        }

        zombies.add(z);
        iGameEvents.addZombieUI(z);
    }


    public void reviewZombies() {
        long now = System.currentTimeMillis();
        Iterator<Zombie> zombieIterator = zombies.iterator();

        while (zombieIterator.hasNext()) {
            Zombie z = zombieIterator.next();
            boolean collided = false;

            Iterator<Plant> iterator = plants.iterator();
            while (iterator.hasNext()) {
                Plant p = iterator.next();
                Rectangle zombieRect = new Rectangle(z.getX(), z.getY(), z.getWidth(), z.getHeight());
                Rectangle plantRect = new Rectangle(p.getX(), p.getY(), p.getWidth(), p.getHeight());

                if (zombieRect.intersects(plantRect)) {
                    collided = true;

                    if (now - z.getPrevAttackTime() >= 1000) {
                        z.setPrevAttackTime(now);
                        p.setDefense(p.getDefense() - 10);
                        System.out.println("Zombie atacó a planta, defensa restante: " + p.getDefense());

                        if (p.getDefense() <= 0) {
                            iGameEvents.deleteComponentUI(p.getId());
                            iterator.remove();
                            System.out.println("Planta destruida por zombie.");

                            int row = (p.getY() - posStartY + p.getHeight() / 2) / 120;
                            int col = (p.getX() - posStartX + p.getWidth() / 2) / 100;
                            plantsInBoard[row][col] = false;
                        }
                    }

                    break; // solo una planta a la vez
                }
            }

            if (!collided && now - z.getPrevMoveTime() > 120) {
                z.moveLeft();
                z.setPrevMoveTime(now);
                iGameEvents.updateZombieUI(z.getId());

                if (z.isOutOfScreen()) {
                    iGameEvents.removeZombieUI(z.getId());
                    zombieIterator.remove();
                    continue;
                }
            }

            //  Verificar si el zombie está muerto (por disparo, cherry bomb, etc.)
            if (z.getHealth() <= 0) {
                iGameEvents.removeZombieUI(z.getId());
                zombieIterator.remove();
            }
        }
    }
}
