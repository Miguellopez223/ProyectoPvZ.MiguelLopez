package org.example.ui;

import lombok.Getter;
import org.example.model.plant.WallNut;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class WallNutDrawing extends JComponent {
    private WallNut wallNut;
    private BufferedImage bi;

    public WallNutDrawing(WallNut wallNut) {
        this.wallNut = wallNut;
        setBounds(wallNut.getX(), wallNut.getY(), wallNut.getWidth(), wallNut.getHeight());

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("wallnut.png")) {
            bi = ImageIO.read(input);
        } catch (IOException e) {
            System.err.println("Error cargando imagen de WallNut.");
            e.printStackTrace();
        }
    }

    public String getId() {
        return wallNut.getId();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (bi != null) {
            g.drawImage(bi, 0, 0, wallNut.getWidth(), wallNut.getHeight(), this);
        }
    }
}
