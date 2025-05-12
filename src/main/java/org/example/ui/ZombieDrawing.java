package org.example.ui;

import lombok.Getter;
import org.example.model.zombie.Zombie;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class ZombieDrawing extends JComponent {
    private Zombie zombie;
    private BufferedImage bi;

    public ZombieDrawing(Zombie zombie) {
        this.zombie = zombie;
        setBounds(zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight());

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("zombie.png")) {
            bi = ImageIO.read(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return zombie.getId();
    }

    public void updatePosition() {
        setLocation(zombie.getX(), zombie.getY());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(bi, 0, 0, zombie.getWidth(), zombie.getHeight(), this);
    }
}