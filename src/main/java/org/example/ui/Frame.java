package org.example.ui;

import org.example.logic.Game;
import org.example.logic.IGameEvents;
import org.example.model.Sun;
import org.example.model.attack.GreenPea;
import org.example.model.plant.PeaShooter;
import org.example.model.plant.Plant;
import org.example.model.plant.SunFlower;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
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


    // Tamaño de celdas del jardín
    private final int startX = 100;
    private final int startY = 100;
    private final int cellWidth = 102;
    private final int cellHeight = 125;
    private final int cols = 9;
    private final int rows = 5;

    private Point getCellCenterIfValid(int x, int y) {
        int col = (x - startX) / cellWidth;
        int row = (y - startY) / cellHeight;

        if (col >= 0 && col < cols && row >= 0 && row < rows) {
            int centerX = startX + col * cellWidth + cellWidth / 2;
            int centerY = startY + row * cellHeight + cellHeight / 2;
            return new Point(centerX, centerY);
        }
        return null;
    }

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
        sunCounterLabel.setForeground(Color.BLACK); // ahora en negro
        sunCounterLabel.setBounds(35, 60, 100, 40);  // más arriba (antes estaba en 90)
        layeredPane.add(sunCounterLabel, Integer.valueOf(4));

        background.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (background.peaShooterSlot.contains(x, y)) {
                    selectedPlant = "Peashooter";
                    System.out.println("Seleccionaste Peashooter");
                    return;
                } else if (background.sunflowerSlot.contains(x, y)) {
                    selectedPlant = "Sunflower";
                    System.out.println("Seleccionaste Sunflower");
                    return;
                }

                for (SunDrawing sd : new ArrayList<>(sunDrawings)) {
                    if (sd.getBounds().contains(e.getPoint())) {
                        sunCounter += 25;
                        sunCounterLabel.setText(String.valueOf(sunCounter));
                        removeSunUI(sd.getSun().getId());
                        game.getFallingSuns().remove(sd.getSun());
                        break;
                    }
                }

                if (selectedPlant != null) {
                    Point cellCenter = getCellCenterIfValid(x, y);
                    if (cellCenter == null) {
                        System.out.println("Haz clic en una celda válida.");
                        return;
                    }

                    int px = cellCenter.x - Game.PEA_SHOOTER_WIDTH / 2;
                    int py = cellCenter.y - Game.PEA_SHOOTER_HEIGHT / 2;

                    for (Plant p : game.getPlants()) {
                        if (Math.abs(p.getX() - px) < cellWidth / 2 && Math.abs(p.getY() - py) < cellHeight / 2) {
                            System.out.println("Ya hay una planta en esta celda.");
                            return;
                        }
                    }

                    if (selectedPlant.equals("Peashooter")) {
                        if (sunCounter >= COSTO_PEASHOOTER) {
                            sunCounter -= COSTO_PEASHOOTER;
                            sunCounterLabel.setText(String.valueOf(sunCounter));
                            PeaShooter ps = new PeaShooter(px, py, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT);
                            game.getPlants().add(ps);
                            addPlantUI(ps);
                        } else {
                            System.out.println("No tienes suficientes soles para plantar un Peashooter.");
                        }
                    } else if (selectedPlant.equals("Sunflower")) {
                        if (sunCounter >= COSTO_SUNFLOWER) {
                            sunCounter -= COSTO_SUNFLOWER;
                            sunCounterLabel.setText(String.valueOf(sunCounter));
                            SunFlower sf = new SunFlower(px, py, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT);
                            game.getPlants().add(sf);
                            addPlantUI(sf);
                        } else {
                            System.out.println("No tienes suficientes soles para plantar un Sunflower.");
                        }
                    }


                    selectedPlant = null;
                }
            }
        });

        pack();
        setVisible(true);
        game = new Game(this);
        startGameThreads();
        background.repaint();
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
                    Thread.sleep(10000);
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
    }

    @Override
    public void addPlantUI(Plant p) {
        JComponent plantDrawing = null;
        if (p instanceof PeaShooter ps) {
            plantDrawing = new PeaShooterDrawing(ps);
        } else if (p instanceof SunFlower sf) {
            plantDrawing = new SunFlowerDrawing(sf);
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
        }
    }

    public Component getComponentById(String id) {
        for (Component comp : layeredPane.getComponents()) {
            if (comp instanceof GreenPeaDrawing pd && pd.getId().equals(id)) {
                return comp;
            }
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

    public static void main(String[] args) {
        new Frame();
    }
}
