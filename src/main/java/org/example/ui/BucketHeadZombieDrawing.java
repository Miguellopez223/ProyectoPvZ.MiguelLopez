package org.example.ui;

import lombok.Getter;
import org.example.model.zombie.BucketHeadZombie;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class BucketHeadZombieDrawing extends JComponent {
    private BucketHeadZombie zombie;
    private BufferedImage bi;

    public BucketHeadZombieDrawing(BucketHeadZombie zombie) {
        this.zombie = zombie;
        setBounds(zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight());

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("BucketHeadZombie.png")) {
            bi = ImageIO.read(input);
        } catch (IOException e) {
            System.err.println("Error cargando imagen de BucketHeadZombie.");
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
