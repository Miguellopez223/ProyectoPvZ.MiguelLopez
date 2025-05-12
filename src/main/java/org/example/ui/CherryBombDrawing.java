package org.example.ui;

import lombok.Getter;
import org.example.model.plant.CherryBomb;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Getter
public class CherryBombDrawing extends JComponent {
    private CherryBomb cherryBomb;
    private BufferedImage bi;

    public CherryBombDrawing(CherryBomb cherryBomb) {
        this.cherryBomb = cherryBomb;
        setBounds(cherryBomb.getX(), cherryBomb.getY(), cherryBomb.getWidth(), cherryBomb.getHeight());

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("CherryBomb.png")) {
            bi = ImageIO.read(input);
        } catch (IOException e) {
            System.err.println("Error cargando imagen de Cherry Bomb.");
            e.printStackTrace();
        }
    }

    public String getId() {
        return cherryBomb.getId();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (bi != null) {
            g.drawImage(bi, 0, 0, cherryBomb.getWidth(), cherryBomb.getHeight(), this);
        }
    }
}
