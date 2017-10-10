/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.travelingsalesman;

import java.io.File;
import java.io.Serializable;
import org.openstreetmap.osm.data.MemoryDataSet;
import org.openstreetmap.osm.data.coordinates.LatLon;
import org.openstreetmap.osm.data.searching.NearestStreetSelector;
import org.openstreetmap.osmosis.core.domain.v0_6.Node;
import org.openstreetmap.travelingsalesman.routing.Route;
import org.openstreetmap.travelingsalesman.routing.routers.TurnRestrictedMultiTargetDijkstraRouter;
import org.openstreetmap.travelingsalesman.routing.selectors.Motorcar;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;

/**
 *
 * @author geoagdt
 */
public class TSMisc implements Serializable {

    public MemoryDataSet _MemoryDataSet;
    public File _Map_File;
    public GENESIS_Environment _Environment;
    // @TODO Pass this in to method
    public NearestStreetSelector _NearestStreetSelector;
    public TurnRestrictedMultiTargetDijkstraRouter _TurnRestrictedMultiTargetDijkstraRouter;

    public MemoryDataSet get_MemoryDataSet() {
        if (_MemoryDataSet == null) {
            init_MemoryDataSet();
        }
        return _MemoryDataSet;
    }

    public NearestStreetSelector get_NearestStreetSelector() {
        if (_NearestStreetSelector == null) {
            _NearestStreetSelector = new NearestStreetSelector();
        }
        return _NearestStreetSelector;
    }

    public TurnRestrictedMultiTargetDijkstraRouter get_TurnRestrictedMultiTargetDijkstraRouter() {
        if (_TurnRestrictedMultiTargetDijkstraRouter == null) {
            _TurnRestrictedMultiTargetDijkstraRouter = new TurnRestrictedMultiTargetDijkstraRouter();
        }
        return _TurnRestrictedMultiTargetDijkstraRouter;
    }

    public TSMisc(
            GENESIS_Environment _Environment,
            File _Map_File) {
        this._Environment = _Environment;
        this._Map_File = _Map_File;
    }

    public TSMisc() {
        //this.map_File = new File("C:/Work/data/Open/OpenStreetMap/map.osm");
        //this.map_File = new File("C:/Work/data/Open/OSM/OtleyAndWharfeDale/map.osm");
        //this.map_File = new File("C:/Work/data/Open/OSM/Leeds/map.osm");
        this._Map_File = new File("C:/Work/data/OpenStreetMap/Leeds/map.osm");
        //this.map_File = new File("C:/Work/data/OpenStreetMap/Taipei/map.osm");
        //this.map_File = new File("C:/Work/data/Open/OSM/UK/uk-100211.osm");
        //http://api.openstreetmap.org/api/0.6/map?bbox=-1.858,53.661,-1.149,53.947
    }

    public void init_MemoryDataSet() {
        System.out.print("init_MemoryDataSet...");
        //URI a_URI = a_File.toURI();
//        try {
        //map = (new org.openstreetmap.osm.io.FileLoader(new URL("file:///testdata.osm"))).parseOsm();
        //map = (new org.openstreetmap.osm.io.FileLoader(a_URI.toURL())).parseOsm();
        _MemoryDataSet = (new org.openstreetmap.osm.io.FileLoader(_Map_File)).parseOsm();
//        } catch (MalformedURLException a_MalformedURLException) {
//            a_MalformedURLException.printStackTrace();
//        }
        //map = (new org.openstreetmap.osm.io.BoundingBoxDownloader(lat0, lon0, lat1, lon1)).parseOsm();
        System.out.println("done.");
    }

    /**
     *
     * @param origin (x, y) (lon, lat) axis order
     * @param destination (x, y) (lon, lat) axis order
     * @return
     */
    public Route getRoute(
            double[] origin,
            double[] destination) {
        MemoryDataSet a_MemoryDataSet = get_MemoryDataSet();
        LatLon originCoord = new LatLon(
                origin[1],
                origin[0]);
        Node originNode = a_MemoryDataSet.getNearestNode(
                originCoord,
                get_NearestStreetSelector());
        double originNearestNodeLatitude = originNode.getLatitude();
        double originNearestNodeLongitude = originNode.getLongitude();
        System.out.println("In " + this.getClass().getName() + ".getRoute(double[],double[]):");

        // Report any major differences in origin 
        double originLatitiudeDiff = origin[1] - originNearestNodeLatitude;
        double originLongitudeDiff = origin[0] - originNearestNodeLongitude;
        double originNearestNode_Distance_Origin = Math.sqrt(
                (originLatitiudeDiff * originLatitiudeDiff)
                - (originLongitudeDiff * originLongitudeDiff));
        double cellsize = _Environment._network_Grid2DSquareCellDouble.getCellsizeDouble(true);
        if (originNearestNode_Distance_Origin > 3.0d * cellsize) {
            System.out.println(
                    "NearestStreetSelector to origin is "
                    + (originNearestNode_Distance_Origin / cellsize)
                    + " cellsizes away!");
        }

        if (!_Environment._network_Grid2DSquareCellDouble.isInGrid(originNearestNodeLongitude, originNearestNodeLatitude, true)) {
            return null;
        }
        //Node startNode = NodeHelper.findNearestNode(osmData, startCoord);
        //Node startNode = findNearestNode(osmData, startCoord);
        //Node startNode = new Node(1L, 1, new Date(), null, 0L, TESTSTARTLAT, TESTSTARTLON);

        LatLon destinationCoord = new LatLon(
                destination[1],
                destination[0]);
        //Node targetNode = NodeHelper.findNearestNode(osmData, targetCoord);
        //Node targetNode = new Node(1L, 1, new Date(), null, 0L, TESTENDLAT, TESTENDLON);
        Node destinationNode = a_MemoryDataSet.getNearestNode(
                destinationCoord,
                get_NearestStreetSelector());
        double destinationNearestNodeLatitude = destinationNode.getLatitude();
        double destinationNearestNodeLongitude = destinationNode.getLongitude();

        // Report any major differences in destination
        double destinationLatitiudeDiff = destination[1] - destinationNearestNodeLatitude;
        double destinationLongitudeDiff = destination[0] - destinationNearestNodeLongitude;
        double destinationNearestNode_Distance_Origin = Math.sqrt(
                (destinationLatitiudeDiff * destinationLatitiudeDiff)
                - (destinationLongitudeDiff * destinationLongitudeDiff));
        if (destinationNearestNode_Distance_Origin > 3.0d * cellsize) {
            System.out.println(
                    "NearestStreetSelector to destination is "
                    + (destinationNearestNode_Distance_Origin / cellsize)
                    + " cellsizes away");
        }

        if (!_Environment._network_Grid2DSquareCellDouble.isInGrid(destinationNearestNodeLongitude, destinationNearestNodeLatitude, true)) {
            return null;
        }
        //Router router = new TurnRestrictedMultiTargetDijkstraRouter();
        if (originNode == destinationNode) {
            //No need to do anything
            return null;
        }

        //Route theRoute = router.route(map, targetNode, startNode, a_NearestStreetSelector);
        Route theRoute = null;
        try {
            theRoute = get_TurnRestrictedMultiTargetDijkstraRouter().route(
                    get_MemoryDataSet(),
                    originNode,
                    destinationNode,
                    new Motorcar());
            //System.out.println("theRoute.distanceInMeters() " + theRoute.distanceInMeters());
        } catch (java.util.NoSuchElementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return theRoute;
        //theRoute.getStartNode().
        //assertTrue(theRoute.getSegmentList().size() > 0);
    }
}
