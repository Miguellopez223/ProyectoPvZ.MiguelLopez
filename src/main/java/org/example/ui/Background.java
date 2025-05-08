package org.example.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class Background extends JPanel {

    private BufferedImage bufferedImage;
    private Image peaShooterIcon;
    private Image sunflowerIcon;

    // Zonas clicables de íconos
    public Rectangle peaShooterSlot = new Rectangle(50, 10, 60, 80);
    public Rectangle sunflowerSlot = new Rectangle(120, 10, 60, 80);

    public Background() {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("pvz-jardin-full.png")) {
            bufferedImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            System.err.println("Error cargando fondo.");
            e.printStackTrace();
        }

        try {
            peaShooterIcon = ImageIO.read(getClass().getClassLoader().getResource("PeashooterIcon.png"));
            sunflowerIcon = ImageIO.read(getClass().getClassLoader().getResource("SunflowerIcon.png"));
        } catch (IOException e) {
            System.err.println("Error cargando íconos de plantas.");
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // LIMPIA el panel antes de dibujar

        Graphics2D g2d = (Graphics2D) g;

        // Fondo del jardín
        if (bufferedImage != null) {
            g2d.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(),
                    165, 0, 990, 570, this);
        }

        // Selector de plantas
        if (peaShooterIcon != null) {
            g2d.drawImage(peaShooterIcon, peaShooterSlot.x, peaShooterSlot.y,
                    peaShooterSlot.width, peaShooterSlot.height, this);
        }
        if (sunflowerIcon != null) {
            g2d.drawImage(sunflowerIcon, sunflowerSlot.x, sunflowerSlot.y,
                    sunflowerSlot.width, sunflowerSlot.height, this);
        }
    }
}
