/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.tests.travelingsalesman;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.data.NodeHelper;
import org.openstreetmap.osm.data.Selector;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osm.data.searching.NearestStreetSelector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.travelingsalesman.routing.Route;
import org.openstreetmap.travelingsalesman.routing.routers.TurnRestrictedMultiTargetDijkstraRouter;
import org.openstreetmap.travelingsalesman.routing.selectors.Motorcar;

/**
 *
 * @author geoagdt
 */
public class TSTest {

    public static void main(String[] args) {
        new TSTest().run();
    }

    public void run() {
        double lat0 = -1.7241d;
        double lon0 = 53.8951d;
        double lat1 = -1.6624d;
        double lon1 = 53.9231d;
        double TESTSTARTLAT = -1.69654d;
        double TESTSTARTLON = 53.90954d;
        double TESTENDLAT = -1.69d;
        double TESTENDLON = 53.91412d;
        NearestStreetSelector a_NearestStreetSelector = new NearestStreetSelector();
        //a_NearestStreetSelector.g
        Motorcar a_Motorcar = new Motorcar();
        MemoryDataSet map = null;
        //File a_File = new File("C:/Work/data/Open/OpenStreetMap/map.osm");
        File a_File = new File("C:/Work/data/Open/OSM/OtleyAndWharfeDale/map.osm");
        //URI a_URI = a_File.toURI();
//        try {
        //map = (new org.openstreetmap.osm.io.FileLoader(new URL("file:///testdata.osm"))).parseOsm();
        //map = (new org.openstreetmap.osm.io.FileLoader(a_URI.toURL())).parseOsm();
        map = (new org.openstreetmap.osm.io.FileLoader(a_File)).parseOsm();
//        } catch (MalformedURLException a_MalformedURLException) {
//            a_MalformedURLException.printStackTrace();
//        }
        //map = (new org.openstreetmap.osm.io.BoundingBoxDownloader(lat0, lon0, lat1, lon1)).parseOsm();
        LatLon startCoord = new LatLon(TESTSTARTLAT, TESTSTARTLON);
        Node startNode = map.getNearestNode(startCoord, a_NearestStreetSelector);
        //Node startNode = NodeHelper.findNearestNode(osmData, startCoord);
        //Node startNode = findNearestNode(osmData, startCoord);
        //Node startNode = new Node(1L, 1, new Date(), null, 0L, TESTSTARTLAT, TESTSTARTLON);
        LatLon targetCoord = new LatLon(TESTENDLAT, TESTENDLON);
        //Node targetNode = NodeHelper.findNearestNode(osmData, targetCoord);
        //Node targetNode = new Node(1L, 1, new Date(), null, 0L, TESTENDLAT, TESTENDLON);
        Node targetNode = map.getNearestNode(targetCoord, a_NearestStreetSelector);
        //Router router = new TurnRestrictedMultiTargetDijkstraRouter();
        TurnRestrictedMultiTargetDijkstraRouter router = new TurnRestrictedMultiTargetDijkstraRouter();
        //Route theRoute = router.route(map, targetNode, startNode, a_NearestStreetSelector);
        Route theRoute = router.route(map, targetNode, startNode, a_Motorcar);

        System.out.println("theRoute.distanceInMeters() " + theRoute.distanceInMeters());

        //theRoute.getStartNode().
        //assertTrue(theRoute.getSegmentList().size() > 0);
    }
}
