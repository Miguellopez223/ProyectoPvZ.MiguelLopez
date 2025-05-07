package org.example.ui;

import org.example.logic.IGameEvents;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Fondo
 *
 * @author Marcos Quispe
 * @since 1.0
 */
public class Background extends JPanel {
    private BufferedImage bufferedImage;

    public Background(IGameEvents gameEvents) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("pvz-jardin-full.png")) {
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Detectar clics
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameEvents.clicInYard(e.getX(), e.getY());
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), 165, 0, 990, 570, this);
    }
}

