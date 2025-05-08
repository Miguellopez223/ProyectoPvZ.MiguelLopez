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

    // Tamaño de celdas del jardín
    private final int startX = 100;
    private final int startY = 100;
    private final int cellWidth = 102;
    private final int cellHeight = 125;
    private final int cols = 9;
    private final int rows = 5;



    // Devuelve el centro de la celda si está dentro del área válida
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

        // Usamos layeredPane para manejar el fondo + componentes
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);
        layeredPane.setPreferredSize(new Dimension(1010, 735));
        setContentPane(layeredPane);

        // Fondo
        background = new Background();
        background.setBounds(0, 0, 1010, 735);
        layeredPane.add(background, Integer.valueOf(0)); // fondo en la capa más baja

        // MouseListener para selección y plantado
        background.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                // 1. Primero, revisamos si hiciste clic en un ícono (selector)
                if (background.peaShooterSlot.contains(x, y)) {
                    selectedPlant = "Peashooter";
                    System.out.println("Seleccionaste Peashooter");
                    return;
                } else if (background.sunflowerSlot.contains(x, y)) {
                    selectedPlant = "Sunflower";
                    System.out.println("Seleccionaste Sunflower");
                    return;
                }

                // 2. Si ya tenías una planta seleccionada, revisamos si hiciste clic en una celda válida
                if (selectedPlant != null) {
                    Point cellCenter = getCellCenterIfValid(x, y);
                    if (cellCenter == null) {
                        System.out.println("Haz clic en una celda válida.");
                        return;
                    }

                    int px = cellCenter.x - Game.PEA_SHOOTER_WIDTH / 2;
                    int py = cellCenter.y - Game.PEA_SHOOTER_HEIGHT / 2;

                    // Plantar la planta seleccionada
                    if (selectedPlant.equals("Peashooter")) {
                        PeaShooter ps = new PeaShooter(px, py, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT);
                        game.getPlants().add(ps);
                        addPlantUI(ps);
                    } else if (selectedPlant.equals("Sunflower")) {
                        SunFlower sf = new SunFlower(px, py, Game.PEA_SHOOTER_WIDTH, Game.PEA_SHOOTER_HEIGHT);
                        game.getPlants().add(sf);
                        addPlantUI(sf);
                    }

                    selectedPlant = null; // limpiar selección
                }
            }
        });




        pack();
        setVisible(true);

        // Iniciar lógica del juego
        game = new Game(this);
        startGameThreads();

        background.repaint(); // aseguramos dibujado inicial
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
            layeredPane.add(plantDrawing, Integer.valueOf(1)); // capa sobre el fondo
            plantDrawing.repaint();
        }
    }

    @Override
    public void throwAttackUI(GreenPea p) {
        GreenPeaDrawing pDrawing = new GreenPeaDrawing(p);
        pDrawing.setBounds(p.getX(), p.getY(), p.getWidth(), p.getHeight());
        layeredPane.add(pDrawing, Integer.valueOf(2)); // por encima de plantas
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
        layeredPane.add(sd, Integer.valueOf(3)); // encima de todo
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
    }

    public static void main(String[] args) {
        new Frame();
    }
}
