package org.example.ui;

import org.example.logic.Game;
import org.example.logic.IGameEvents;
import org.example.model.Sun;
import org.example.model.plant.PeaShooter;
import org.example.model.attack.GreenPea;
import org.example.model.plant.Plant;
import org.example.model.plant.SunFlower;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.random.RandomGenerator;
import java.util.List;


/**
 * Frame
 *
 * @author Marcos Quispe
 * @since 1.0
 */
public class Frame extends JFrame implements IGameEvents {

    private Game game;

    private List<SunDrawing> sunDrawings = new ArrayList<>();


    public Frame() throws HeadlessException {
        setTitle("My first JFrame");
        setSize(1010, 735);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //getContentPane().setLayout(null);
        setVisible(true);

        Background background = new Background();
        background.setSize(getWidth() - 10, getHeight() - 35);
        repaint();
        getContentPane().add(background);

        game = new Game(this);
        new Thread(() -> {
            int nroPlanta = 0;
            while (nroPlanta < 5) {
                game.createDefaultPeaShooter(nroPlanta);
                nroPlanta++;
                try {
                    Thread.sleep(RandomGenerator.getDefault().nextInt(2000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            game.createDefaultSunFlower();
        }).start();

        System.out.println("images creada");

        getContentPane().repaint();

        // REFRESH PLANTS
        new Thread(() -> {
            while (true) {
                for (Component c : getContentPane().getComponents()) {
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

        // AVANZAR PROJECTILES
        new Thread(() -> {
            game.reviewAttacks();
            while (true) {
                for (Component c : getContentPane().getComponents()) {
                    if (c instanceof GreenPeaDrawing) {
                        c.repaint();
                    }
                }
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //repaint();
            }
        }).start();

        // GENERAR SOLES
        new Thread(() -> {
            while (true) {
                game.generateFallingSun();
                try {
                    Thread.sleep(10000); // cada 10 segundos exactos
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        //MOVER SOLES
        new Thread(() -> {
            while (true) {
                game.reviewFallingSuns();
                try {
                    Thread.sleep(20); // velocidad de caída
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // VALIDAR REGLAS JUEGO
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
        System.out.println("frame creado");
    }

    @Override
    public void addPlantUI(Plant p) {
        JComponent plantDrawing = null;
        if (p instanceof PeaShooter ps) {
            plantDrawing = new PeaShooterDrawing(ps);
        } else if (p instanceof SunFlower sf) {
            plantDrawing = new SunFlowerDrawing(sf);
        }
        getContentPane().add(plantDrawing, 0);
        plantDrawing.repaint();
        System.out.println("nueva planta");
    }

    @Override
    public void throwAttackUI(GreenPea p) {
        GreenPeaDrawing pDrawing = new GreenPeaDrawing(p);
        getContentPane().add(pDrawing, 0);
        pDrawing.repaint();
    }

    @Override
    public void updatePositionUI(String id) {
        Component c = getComponentById(id);
        if (c != null) {
            ((GreenPeaDrawing) c).updatePosition();
        }
    }

    @Override
    public void deleteComponentUI(String id) {
        Component c = getComponentById(id);
        if (c != null) {
            getContentPane().remove(c);
        }
    }

    public Component getComponentById(String id) {
        for (int i = 0; i < getContentPane().getComponents().length; i++) {
            if (getContentPane().getComponents()[i] instanceof GreenPeaDrawing pd) {
                if (pd.getId().equals(id)) {
                    return getContentPane().getComponents()[i];
                }
            }
        }
        return null;
    }

    @Override
    public void addSunUI(Sun sun) {
        SunDrawing sd = new SunDrawing(sun);
        sunDrawings.add(sd);
        getContentPane().add(sd, 0);
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
                getContentPane().remove(sd);
                return true;
            }
            return false;
        });
    }


    public static void main(String[] args) {
        Frame frame = new Frame();
    }
}
