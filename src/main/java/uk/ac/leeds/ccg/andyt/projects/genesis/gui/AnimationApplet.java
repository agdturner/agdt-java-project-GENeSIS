package uk.ac.leeds.ccg.andyt.projects.genesis.gui;

import java.applet.Applet;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Vector;

public class AnimationApplet extends Applet {

    private BufferedImage _BufferedImage;
    private Image[] _Images;
    private MediaTracker _MediaTracker;
    private int width;
    private int height;
    private int index;

    public void update(Graphics _Graphics) {
        paint(_Graphics);
    }

    @Override
    public void init() {
        index = 0;
//        width = 201;
//        height = 201;
//        URL _URL = null;
//        try {
//            _URL = new URL(getDocumentBase(), "genesis/");
//        } catch (MalformedURLException _MalformedURLException) {
//            _MalformedURLException.printStackTrace();
//        }
//        String[] _Filenames = new File(_URL.getFile()).list();
//        for (int i = 0; i < _Filenames.length; i++) {
//            System.out.println(_Filenames[i]);
//        }
//        _Images = new Image[_Filenames.length];
//        _MediaTracker = new MediaTracker(this);
//        try {
//            // Load Images
//            for (int i = 0; i < _Filenames.length; i++) {
//                _Images[i] = getImage(new URL(getDocumentBase(), "genesis/" + _Filenames[i]));
//                _MediaTracker.addImage(_Images[i], i);
//            }
//            _MediaTracker.waitForAll();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // Need an index file as there is a security issue with being able to
        // list contents of Web directories
        // Get width and height for the images from the index file
        URL _Index_URL = null;
        try {
            _Index_URL = new URL(getDocumentBase(), "genesis/index");
        } catch (MalformedURLException _MalformedURLException) {
            _MalformedURLException.printStackTrace();
        }
        Object[] _Index_Object = read_Index_URL(_Index_URL);
        width = (Integer) _Index_Object[0];
        height = (Integer) _Index_Object[1];
        Vector _URL_Vector = (Vector) _Index_Object[2];
        for (int i = 0; i < _URL_Vector.size(); i++) {
            System.out.println(_URL_Vector.elementAt(i));
        }
        _Images = new Image[_URL_Vector.size()];
        _MediaTracker = new MediaTracker(this);
        try {
            // Load Images
            for (int i = 0; i < _URL_Vector.size(); i++) {
                URL _Image_URL = (URL) _URL_Vector.elementAt(i);
                _Images[i] = getImage(_Image_URL);
                _MediaTracker.addImage(_Images[i], i);
            }
            _MediaTracker.waitForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        _AnimationApplet.resize(
//                            img[0].getWidth(_AnimationApplet),
//                            img[0].getHeight(_AnimationApplet));
//        width = img[0].getWidth(this);
//        height = img[0].getHeight(this);
        //this.resize(
        //                    img[0].getWidth(this),
        //                    img[0].getHeight(this));
        _BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        AnimationThread _AnimationThread = new AnimationThread();
        _AnimationThread.delayedAnimation(this, 100);
        _AnimationThread.start();
    }

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

    public Object[] read_Index_URL(URL _URL) {
        Object[] result = new Object[3];
        Vector _URL_Vector = new Vector();
        try {
            BufferedReader _BufferedReader = new BufferedReader(
                    new InputStreamReader(
                    _URL.openStream()));
            String _Line;

            _Line = _BufferedReader.readLine();
            result[0] = new Integer(_Line);
            result[1] = new Integer(_Line);
            while ((_Line = _BufferedReader.readLine()) != null) {
                System.out.println(_Line);
                _URL = new URL(getDocumentBase(), "genesis/" + _Line);
                _URL_Vector.add(_URL);
            }
            _BufferedReader.close();
            result[2] = _URL_Vector;
        } catch (IOException _IOException) {
            _IOException.printStackTrace();
        }
        return result;
    }

    public void animate() {
        repaint();
    }

    class AnimationThread extends Thread {

        AnimationApplet _AnimationApplet;
        int delay;

        public void delayedAnimation(AnimationApplet _AnimationApplet, int delay) {
            this._AnimationApplet = _AnimationApplet;
            this.delay = delay;
        }

        @Override
        public void run() {
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
