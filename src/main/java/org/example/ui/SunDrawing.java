package org.example.ui;

import lombok.Getter;
import org.example.model.Sun;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Getter
public class SunDrawing extends JComponent {
    private Sun sun;
    private BufferedImage bi;

    public SunDrawing(Sun sun) {
        this.sun = sun;
        setBounds(sun.getX(), sun.getY(), sun.getWidth(), sun.getHeight());

        try (var input = getClass().getClassLoader().getResourceAsStream("sun.png")) {
            bi = ImageIO.read(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updatePosition() {
        setLocation(sun.getX(), sun.getY());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(bi, 0, 0, sun.getWidth(), sun.getHeight(), this);
    }
}
