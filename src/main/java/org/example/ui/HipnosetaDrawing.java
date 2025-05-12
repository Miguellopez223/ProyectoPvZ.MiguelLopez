package org.example.ui;

import lombok.Getter;
import org.example.model.plant.Hipnoseta;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Getter
public class HipnosetaDrawing extends JComponent {
    private Hipnoseta hipnoseta;
    private BufferedImage bi;

    public HipnosetaDrawing(Hipnoseta hipnoseta) {
        this.hipnoseta = hipnoseta;
        setBounds(hipnoseta.getX(), hipnoseta.getY(), hipnoseta.getWidth(), hipnoseta.getHeight());

        try {
            bi = ImageIO.read(getClass().getClassLoader().getResource("Hipnoseta.png"));
        } catch (IOException e) {
            System.err.println("Error cargando imagen de Hipnoseta.");
            e.printStackTrace();
        }
    }

    public String getId() {
        return hipnoseta.getId();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawImage(bi, 0, 0, hipnoseta.getWidth(), hipnoseta.getHeight(), this);
    }
}
