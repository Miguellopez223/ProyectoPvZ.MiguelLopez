package org.example.ui;

import lombok.Getter;
import org.example.model.plant.SnowPeaShooter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class SnowPeaShooterDrawing extends JComponent {
    private BufferedImage bi;
    private SnowPeaShooter snowPeaShooter;

    public SnowPeaShooterDrawing(SnowPeaShooter snowPeaShooter) {
        this.snowPeaShooter = snowPeaShooter;
        setBounds(snowPeaShooter.getX(), snowPeaShooter.getY(), snowPeaShooter.getWidth(), snowPeaShooter.getHeight());

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("SnowPea.png")) {
            bi = ImageIO.read(input);
        } catch (IOException e) {
            System.err.println("Error cargando imagen estática de Snow Pea.");
            e.printStackTrace();
        }
    }

    public String getId() {
        return snowPeaShooter.getId();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (bi != null) {
            g.drawImage(bi, 0, 0, snowPeaShooter.getWidth(), snowPeaShooter.getHeight(), this);
        }
    }
}
