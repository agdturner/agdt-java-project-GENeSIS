package uk.ac.leeds.ccg.andyt.projects.genesis.visualisation;

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
//import java.io.BufferedInputStream;
import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
//import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.StreamTokenizer;
import java.net.MalformedURLException;
//import java.net.URI;
import java.net.URL;
//import java.net.URLConnection;
import java.util.Vector;

public class AnimationApplet_1 extends AnimationApplet {

    @Override
    public void init() {
        _Run = 1;
        //_Directory_String = "_Population_Movement_Composite_Map_0/";
        //_Directory_String = "_Population_Locationt_Composite_Map";
        index = 0;
        URL _Index_URL = null;
        try {
            //_Index_URL = new File("C:/Work/Projects/GENESIS/workspace/Test/index").toURL();
            _Index_URL = new File("C:/Work/Projects/GENESIS/workspace/Test2/index").toURL();
            //_Index_URL = new File("C:/Work/Projects/GENESIS/workspace/GenerateSociety_5/index").toURL();
            //_Index_URL = new URL(getDocumentBase(), "genesis/" + _Run + "/" + _Directory_String + "index");
        } catch (MalformedURLException aMalformedURLException) {
            aMalformedURLException.printStackTrace();
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
                try {
                    URL _Image_URL = (URL) _URL_Vector.elementAt(i);
                    _Images[i] = getImage(_Image_URL);
                    _MediaTracker.addImage(_Images[i], i);
                } catch (OutOfMemoryError _OutOfMemoryError) {
                    _OutOfMemoryError.printStackTrace();
                    continue;
                }
            }
            _MediaTracker.waitForAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _BufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        AnimationThread _AnimationThread = new AnimationThread();
        //_AnimationThread.delayedAnimation(this, 100);
        _AnimationThread.delayedAnimation(this, 1000);
        _AnimationThread.start();
    }

    public Object[] read_Index_URL(URL _URL) {
        Object[] result = new Object[3];
        Vector _URL_Vector = new Vector();
        try {
            BufferedReader aBufferedReader =
                    new BufferedReader(
                    new InputStreamReader(
                    _URL.openStream()));
            String line = aBufferedReader.readLine();
            result[0] = 1024;
            result[1] = 512;
            while ((line = aBufferedReader.readLine()) != null) {
                System.out.println(line);
                //_URL = new URL(getDocumentBase(), "genesis/" + _Run + "/" + _Directory_String + _Line);
                //_URL = new URL(getDocumentBase(), "genesis/" + _Run + "/" + _Directory_String + _Line);
                try {
                    //_URL = new File("C:/Work/Projects/GENESIS/workspace/GenerateSociety_5/" + line).toURL();
                    //_URL = new File("C:/Work/Projects/GENESIS/workspace/Test/" + line).toURL();
                    _URL = new File("C:/Work/Projects/GENESIS/workspace/Test2/" + line).toURL();
                } catch (MalformedURLException aMalformedURLException) {
                    aMalformedURLException.printStackTrace();
                }
                _URL_Vector.add(_URL);
            }
            aBufferedReader.close();
            result[2] = _URL_Vector;
        } catch (IOException _IOException) {
            _IOException.printStackTrace();
        }
        return result;
    }
}
