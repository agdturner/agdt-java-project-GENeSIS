package uk.ac.leeds.ccg.andyt.projects.genesis.visualisation;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;

public class GENESIS_AnimationApplet extends Applet {

    protected BufferedImage _BufferedImage;
    protected Image[] _Images;
    protected MediaTracker _MediaTracker;
    protected int width;
    protected int height;
    protected int index;
    /**
     * This needs incrementing so as not to overwrite any results
     */
    protected int _Run;
    protected String _Directory_String;

    @Override
    public void update(Graphics _Graphics) {
        paint(_Graphics);
    }

//    @Override
//    public void init() {
//    }
    @Override
    public void paint(Graphics _Graphics) {
        Graphics2D _Graphics2D = _BufferedImage.createGraphics();
//        for (int i = 0; i < _Images.length; i++) {
//            gi.drawImage(_Images[i], 0, 0, this);
//        }
        if (_Images[0] != null) {
            _Graphics2D.drawImage(_Images[index], 0, 0, this);
            index++;
            if (index == _Images.length) {
                index = 0;
            }
            //index = (index < maxImg) ? index + 1 : 0;
        }
        _Graphics2D.dispose();
        //g.drawImage(_BufferedImage, 0, 0, width, height, null);
        _Graphics.drawImage(_BufferedImage, 0, 0, width, height, this);
    }

    public void animate() {
        repaint();
    }

    protected class AnimationThread extends Thread {

        GENESIS_AnimationApplet _AnimationApplet;
        int delay;

        public void delayedAnimation(
                GENESIS_AnimationApplet _AnimationApplet,
                int delay) {
            this._AnimationApplet = _AnimationApplet;
            this.delay = delay;
        }

        @Override
        public void run() {
            _AnimationApplet.resize(width, height);
            while (true) {
                try {
                    sleep(delay);
                    _AnimationApplet.animate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
