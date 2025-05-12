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
    private Image plantSelectorImage;
    private Image shovelIcon;
    private Image snowPeaIcon;
    private Image wallNutIcon;
    private Image cherryIcon;

    // Zonas clicables de íconos
    public Rectangle peaShooterSlot = new Rectangle(170, 10, 60, 80);     // más arriba y a la izquierda
    public Rectangle sunflowerSlot = new Rectangle(100, 10, 60, 80);     // a la derecha del primero
    public Rectangle shovelSlot = new Rectangle(530, 0, 100, 100); // ajusta según tu diseño
    public Rectangle snowPeaSlot = new Rectangle(240, 10, 60, 80); // a la derecha del sunflower
    public Rectangle wallNutSlot = new Rectangle(310, 10, 60, 80);
    public Rectangle cherrySlot = new Rectangle(380, 10, 60, 80);



    // Parámetros de la grilla (misma lógica que en Frame)
    private final int startX = 100;
    private final int startY = 100;
    private final int cellWidth = 102;
    private final int cellHeight = 125;
    private final int cols = 9;
    private final int rows = 5;

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

        try (InputStream input = getClass().getClassLoader().getResourceAsStream("selector_plant.png")) {
            plantSelectorImage = ImageIO.read(input);
        } catch (IOException e) {
            System.err.println("Error cargando imagen del selector de plantas.");
            e.printStackTrace();
        }

        try {
            shovelIcon = ImageIO.read(getClass().getClassLoader().getResource("shovel.png"));
        } catch (IOException e) {
            System.err.println("Error cargando imagen de la pala.");
            e.printStackTrace();
        }

        try {
            snowPeaIcon = ImageIO.read(getClass().getClassLoader().getResource("SnowPeaIcon.png"));
        } catch (IOException e) {
            System.err.println("Error cargando ícono de Snow Pea.");
            e.printStackTrace();
        }

        try {
            wallNutIcon = ImageIO.read(getClass().getClassLoader().getResource("WallnutIcon.png"));
        } catch (IOException e) {
            System.err.println("Error cargando ícono de WallNut.");
            e.printStackTrace();
        }

        try {
            cherryIcon = ImageIO.read(getClass().getClassLoader().getResource("CherryBombIcon.png"));
        } catch (IOException e) {
            System.err.println("Error cargando ícono de Cherry Bomb.");
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

        if (plantSelectorImage != null) {
            g.drawImage(plantSelectorImage, 5, 0, 520, 100, this);// Aumenta el tamaño aquí
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
        if (shovelIcon != null) {
            g2d.drawImage(shovelIcon, shovelSlot.x, shovelSlot.y, shovelSlot.width, shovelSlot.height, this);
        }
        if (snowPeaIcon != null) {
            g2d.drawImage(snowPeaIcon, snowPeaSlot.x, snowPeaSlot.y,
                    snowPeaSlot.width, snowPeaSlot.height, this);
        }
        if (wallNutIcon != null) {
            g2d.drawImage(wallNutIcon, wallNutSlot.x, wallNutSlot.y,
                    wallNutSlot.width, wallNutSlot.height, this);
        }
        if (cherryIcon != null) {
            g2d.drawImage(cherryIcon, cherrySlot.x, cherrySlot.y,
                    cherrySlot.width, cherrySlot.height, this);
        }


        // Dibuja la grilla roja de plantado
        g.setColor(Color.RED);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = startX + j * cellWidth;
                int y = startY + i * cellHeight;
                g.drawRect(x, y, cellWidth, cellHeight);
            }
        }

    }


}
