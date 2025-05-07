package org.example.ui;

import lombok.Getter;
import org.example.model.zombie.NormalZombie;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.io.IOException;

@Getter
public class NormalZombieDrawing extends JComponent {
    private NormalZombie normalZombie;
    private BufferedImage bi;

    public NormalZombieDrawing(NormalZombie zombie) {
        this.normalZombie = zombie;
        setBounds(zombie.getX(), zombie.getY(), zombie.getWidth(), zombie.getHeight());
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("NormalZombie.png")) {
            bi = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getId() {
        return normalZombie.getId();
    }

    public void updatePosition() {
        setLocation(normalZombie.getX(), normalZombie.getY());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(bi, 0, 0, normalZombie.getWidth(), normalZombie.getHeight(), this);
    }
}
