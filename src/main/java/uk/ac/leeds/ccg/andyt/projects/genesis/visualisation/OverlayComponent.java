package uk.ac.leeds.ccg.andyt.projects.genesis.visualisation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

@Deprecated
public class OverlayComponent extends JPanel {

    BufferedImage _BufferedImage = null;

    public OverlayComponent() {
    }

    public void readImage(URL imageURL) {
        try {
            _BufferedImage = ImageIO.read(imageURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (_BufferedImage != null) {
            g.drawImage(_BufferedImage, 0, 0, this);
        }
        // Call out to all things to be overlayed...
//        g.setColor(Color.RED);
//        g.drawLine(0, 0, 100, 100);
    }
}
