package org.example.ui;

import lombok.Getter;
import org.example.model.zombie.ConeHeadZombie;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class ConeHeadZombieDrawing extends JComponent {
    private ConeHeadZombie zombie;
    private BufferedImage bi;

    public ConeHeadZombieDrawing(ConeHeadZombie zombie) {
        this.zombie = zombie;
        setBounds(zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight());

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("ConeHeadZombie.png")) {
            bi = ImageIO.read(input);
        } catch (IOException e) {
            System.err.println("Error cargando imagen de ConeHeadZombie.");
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
