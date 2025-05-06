package org.example.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

    public Background() {
        InputStream inputStream = null;
        try {
            inputStream = this.getClass().getClassLoader().getResourceAsStream("pvz-jardin-full.png"); // no funciona bien con webp
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        //super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        //g2d.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), this);
        g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight()
                , 165, 0
                , 990, 570, this);
    }
}
