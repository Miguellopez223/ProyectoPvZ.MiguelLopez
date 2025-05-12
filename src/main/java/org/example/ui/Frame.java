package org.example.ui;

import org.example.logic.Game;
import org.example.logic.IGameEvents;
import org.example.model.Sun;
import org.example.model.attack.GreenPea;
import org.example.model.plant.*;
import org.example.model.zombie.Zombie;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Frame extends JFrame implements IGameEvents {

    public static String selectedPlant = null;

    private Game game;
    private Background background;
    private JLayeredPane layeredPane;
    private List<SunDrawing> sunDrawings = new ArrayList<>();
    private JLabel sunCounterLabel;
    private int sunCounter = 0;

    private static final int COSTO_SUNFLOWER = 50;
    private static final int COSTO_PEASHOOTER = 100;
    private static final int COSTO_SNOWPEA = 150;
    private static final int COSTO_WALLNUT = 50;


    private List<ZombieDrawing> zombieDrawings = new ArrayList<>();
    private boolean shovelSelected = false;

    private final int startX = 100;
    private final int startY = 100;
    private final int cellWidth = 102;
    private final int cellHeight = 125;

    private Image wallNutIcon;
    public Rectangle wallNutSlot = new Rectangle(310, 10, 60, 80);


    public Frame() {
        setTitle("Plants vs Zombies");
        setSize(1010, 735);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        layeredPane.setPreferredSize(new Dimension(1010, 735));
        setContentPane(layeredPane);

        background = new Background();
        background.setBounds(0, 0, 1010, 735);
        layeredPane.add(background, Integer.valueOf(0));

        sunCounterLabel = new JLabel("0");
        sunCounterLabel.setFont(new Font("Arial", Font.BOLD, 24));
        sunCounterLabel.setForeground(Color.BLACK);
        sunCounterLabel.setBounds(35, 60, 100, 40);
        layeredPane.add(sunCounterLabel, Integer.valueOf(4));

        background.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // Seleccionar planta
                if (background.wallNutSlot.contains(x, y)) {
                    selectedPlant = "Wallnut";
                    System.out.println("Seleccionaste Wallnut");
                    return;
                }
                if (background.snowPeaSlot.contains(x, y)) {
                    selectedPlant = "SnowPea";
                    System.out.println("Seleccionaste SnowPea");
                    return;
                }
                if (background.peaShooterSlot.contains(x, y)) {
                    selectedPlant = "Peashooter";
                    System.out.println("Seleccionaste Peashooter");
                    return;
                } else if (background.sunflowerSlot.contains(x, y)) {
                    selectedPlant = "Sunflower";
                    System.out.println("Seleccionaste Sunflower");
                    return;
                }

                // Recolectar soles
                for (SunDrawing sd : new ArrayList<>(sunDrawings)) {
                    if (sd.getBounds().contains(e.getPoint())) {
                        sunCounter += 25;
                        sunCounterLabel.setText(String.valueOf(sunCounter));
                        removeSunUI(sd.getSun().getId());
                        game.getFallingSuns().remove(sd.getSun());
                        break;
                    }
                }

                // Activar/desactivar pala
                if (background.shovelSlot.contains(x, y)) {
                    shovelSelected = !shovelSelected;
                    selectedPlant = null;
                    System.out.println("🪣 Modo pala: " + (shovelSelected ? "activo" : "desactivado"));
                    return;
                }

                // Eliminar planta con pala
                if (shovelSelected) {
                    Component clicked = layeredPane.getComponentAt(SwingUtilities.convertPoint(background, e.getPoint(), layeredPane));
                    if (clicked instanceof PeaShooterDrawing || clicked instanceof SunFlowerDrawing) {
                        String id = null;
                        if (clicked instanceof PeaShooterDrawing ps) id = ps.getId();
                        else if (clicked instanceof SunFlowerDrawing sf) id = sf.getId();

                        if (id != null) {
                            Iterator<Plant> iterator = game.getPlants().iterator();
                            while (iterator.hasNext()) {
                                Plant p = iterator.next();
                                if (p.getId().equals(id)) {
                                    iterator.remove();
                                    break;
                                }
                            }
                            deleteComponentUI(id);
                            int row = (clicked.getY() - startY + clicked.getHeight() / 2) / cellHeight;
                            int col = (clicked.getX() - startX + clicked.getWidth() / 2) / cellWidth;
                            game.getPlantsInBoard()[row][col] = false;
                            System.out.println("🌱 Planta eliminada con pala");
                            shovelSelected = false;
                            System.out.println("🪣 Modo pala desactivado automáticamente");

                        }
                    }
                    return;
                }

                // Plantar
                if (selectedPlant != null) {
                    Point cellCenter = getCellCenterIfValid(x, y);
                    if (cellCenter == null) return;

                    int px = cellCenter.x - Game.PEA_SHOOTER_WIDTH / 2;
                    int py = cellCenter.y - Game.PEA_SHOOTER_HEIGHT / 2;
                    int row = (py - startY + Game.PEA_SHOOTER_HEIGHT / 2) / cellHeight;
                    int col = (px - startX + Game.PEA_SHOOTER_WIDTH / 2) / cellWidth;

                    if (game.getPlantsInBoard()[row][col]) return;

                    if (selectedPlant.equals("Peashooter") && sunCounter >= COSTO_PEASHOOTER) {
                        sunCounter -= COSTO_PEASHOOTER;
                        PeaShooter ps = new PeaShooter(px, py, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT);
                        game.getPlants().add(ps);
                        addPlantUI(ps);
                        game.getPlantsInBoard()[row][col] = true;
                    }
                    else if (selectedPlant.equals("Sunflower") && sunCounter >= COSTO_SUNFLOWER) {
                        sunCounter -= COSTO_SUNFLOWER;
                        SunFlower sf = new SunFlower(px, py, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT);
                        game.getPlants().add(sf);
                        addPlantUI(sf);
                        game.getPlantsInBoard()[row][col] = true;
                    }
                    else if (selectedPlant.equals("SnowPea") && sunCounter >= COSTO_SNOWPEA) {
                        sunCounter -= COSTO_SNOWPEA;
                        SnowPeaShooter snow = new SnowPeaShooter(px, py, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT);
                        game.getPlants().add(snow);
                        addPlantUI(snow);
                        game.getPlantsInBoard()[row][col] = true;
                    }
                    else if (selectedPlant.equals("Wallnut") && sunCounter >= COSTO_WALLNUT) {
                        sunCounter -= COSTO_WALLNUT;
                        WallNut wall = new WallNut(px, py, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT);
                        game.getPlants().add(wall);
                        addPlantUI(wall);
                        game.getPlantsInBoard()[row][col] = true;
                    }

                    sunCounterLabel.setText(String.valueOf(sunCounter));
                    selectedPlant = null;
                }
            }
        });

        pack(); // Ajustar componentes
        setVisible(true); // Mostrar ventana después de agregar shovelComponent

        game = new Game(this);
        startGameThreads();
        background.repaint();
    }

    private Point getCellCenterIfValid(int x, int y) {
        int col = (x - startX) / cellWidth;
        int row = (y - startY) / cellHeight;

        if (col >= 0 && col < 9 && row >= 0 && row < 5) {
            int centerX = startX + col * cellWidth + cellWidth / 2;
            int centerY = startY + row * cellHeight + cellHeight / 2;
            return new Point(centerX, centerY);
        }
        return null;
    }

    private void startGameThreads() {
        new Thread(() -> {
            while (true) {
                game.reviewPlants();
                game.reviewAttacks();
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                game.reviewFallingSuns();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                game.generateFallingSun();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                for (Component c : layeredPane.getComponents()) {
                    if (c instanceof PeaShooterDrawing || c instanceof SunFlowerDrawing) {
                        c.repaint();
                    }
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                for (Component c : layeredPane.getComponents()) {
                    if (c instanceof GreenPeaDrawing) {
                        c.repaint();
                    }
                }
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                game.spawnZombie();
                try {
                    Thread.sleep(8000); // cada 8 segundos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                game.reviewZombies();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void addPlantUI(Plant p) {
        JComponent plantDrawing = null;
        if (p instanceof PeaShooter ps) {
            plantDrawing = new PeaShooterDrawing(ps);
        } else if (p instanceof SunFlower sf) {
            plantDrawing = new SunFlowerDrawing(sf);
        }
        else if (p instanceof SnowPeaShooter snow) {
            plantDrawing = new SnowPeaShooterDrawing(snow);
        }
        else if (p instanceof WallNut wallNut) {
            plantDrawing = new WallNutDrawing(wallNut);
        }

        if (plantDrawing != null) {
            plantDrawing.setBounds(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            layeredPane.add(plantDrawing, Integer.valueOf(1));
            plantDrawing.repaint();
        }

    }

    @Override
    public void throwAttackUI(GreenPea p) {
        GreenPeaDrawing pDrawing = new GreenPeaDrawing(p);
        pDrawing.setBounds(p.getX(), p.getY(), p.getWidth(), p.getHeight());
        layeredPane.add(pDrawing, Integer.valueOf(2));
        pDrawing.repaint();
    }

    @Override
    public void updatePositionUI(String id) {
        Component c = getComponentById(id);
        if (c instanceof GreenPeaDrawing gp) {
            gp.updatePosition();
        }
    }

    @Override
    public void deleteComponentUI(String id) {
        Component c = getComponentById(id);
        if (c != null) {
            layeredPane.remove(c);
            layeredPane.repaint(); // ✅ fuerza redibujado
        }
    }

    public Component getComponentById(String id) {
        for (Component comp : layeredPane.getComponents()) {
            if (comp instanceof GreenPeaDrawing gp && gp.getId().equals(id)) return comp;
            if (comp instanceof PeaShooterDrawing ps && ps.getId().equals(id)) return comp;
            if (comp instanceof SunFlowerDrawing sf && sf.getId().equals(id)) return comp;
            if (comp instanceof SunDrawing sun && sun.getSun().getId().equals(id)) return comp;
            if (comp instanceof ZombieDrawing zd && zd.getZombie().getId().equals(id)) return comp;
        }
        return null;
    }

    @Override
    public void addSunUI(Sun sun) {
        SunDrawing sd = new SunDrawing(sun);
        sd.setBounds(sun.getX(), sun.getY(), sun.getWidth(), sun.getHeight());
        sunDrawings.add(sd);
        layeredPane.add(sd, Integer.valueOf(3));
        sd.repaint();
    }

    @Override
    public void updateSunPosition(String id) {
        for (SunDrawing sd : sunDrawings) {
            if (sd.getSun().getId().equals(id)) {
                sd.updatePosition();
                break;
            }
        }
    }

    @Override
    public void removeSunUI(String id) {
        sunDrawings.removeIf(sd -> {
            if (sd.getSun().getId().equals(id)) {
                layeredPane.remove(sd);
                return true;
            }
            return false;
        });

        layeredPane.revalidate();
        layeredPane.repaint();
    }

    @Override
    public void addZombieUI(Zombie z) {
        ZombieDrawing zd = new ZombieDrawing(z);
        zd.setBounds(z.getX(), z.getY(), z.getWidth(), z.getHeight());
        zombieDrawings.add(zd);
        layeredPane.add(zd, Integer.valueOf(2));
        zd.repaint();
    }

    @Override
    public void updateZombieUI(String id) {
        for (ZombieDrawing zd : zombieDrawings) {
            if (zd.getZombie().getId().equals(id)) {
                zd.updatePosition();
                break;
            }
        }
    }

    @Override
    public void removeZombieUI(String id) {
        zombieDrawings.removeIf(zd -> {
            if (zd.getZombie().getId().equals(id)) {
                layeredPane.remove(zd);
                return true;
            }
            return false;
        });
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    public static void main(String[] args) {
        new Frame();
    }
}
