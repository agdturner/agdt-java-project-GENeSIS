/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.io;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author geoagdt
 */
public class OpenStreetMapData {

    public OpenStreetMapData() {
    }

    public static void main(String[] args) {
        OpenStreetMapData aOpenStreetMapData = new OpenStreetMapData();
        aOpenStreetMapData.run();
    }

    public void run() {
        File directory = new File("C:/Work/data/Open/OSM");
        File file = new File(directory, "map.osm");
        XMLDecoder aXMLDecoder = null;
        try {
            aXMLDecoder = new XMLDecoder(
                    new BufferedInputStream(
                    new FileInputStream(file)));
        } catch (IOException aIOException) {
            System.exit(1);
        }
        Object result = aXMLDecoder.readObject();
        aXMLDecoder.close();
    }
}
