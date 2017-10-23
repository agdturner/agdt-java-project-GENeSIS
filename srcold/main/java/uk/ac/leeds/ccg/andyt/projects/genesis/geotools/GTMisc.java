/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.geotools;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
//import com.vividsolutions.jts.geom.PrecisionModel;
import org.geotools.factory.GeoTools;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/**
 *
 * @author geoagdt
 */
public class GTMisc {

    /**
     * @param x Easting
     * @param y Northing
     * @return double[] lat, lon
     */
    public static double[] transform_OSGB_To_LatLon(
            double x,
            double y) {
        double[] result = new double[2];
        GeometryFactory geometryFactory = new GeometryFactory();
        //PrecisionModel precisionModel = geometryFactory.getPrecisionModel();
        Point a_Point = geometryFactory.createPoint(
                new Coordinate(x, y));
        //System.out.println(a_Point.toString());
        CoordinateReferenceSystem sourceCRS = null;
        CoordinateReferenceSystem targetCRS = null;
        MathTransform transform = null;
        Geometry targetGeometry = null;
        try {
            sourceCRS = CRS.decode("EPSG:27700");
            targetCRS = CRS.decode("EPSG:4326");
            transform = CRS.findMathTransform(sourceCRS, targetCRS);
            //TransformException
            targetGeometry = JTS.transform(a_Point, transform);
        } catch (Exception e) {
            e.printStackTrace();

        }
        //System.out.println(targetGeometry.toString());
        Point latlon_Point = (Point) targetGeometry;
        result[0] = latlon_Point.getX();
        result[1] = latlon_Point.getY();
        return result;
    }

    /**
     * @param lat Latitude
     * @param lon Longitude
     * @return double[] x (Easting), y (Northing)
     */
    public static double[] transform_LatLon_To_OSGB(
            double lat,
            double lon) {
        double[] result = new double[2];
        GeometryFactory geometryFactory = new GeometryFactory();
        //PrecisionModel precisionModel = geometryFactory.getPrecisionModel();
        Point a_Point = geometryFactory.createPoint(
                new Coordinate(lat, lon));
        System.out.println(a_Point.toString());
        CoordinateReferenceSystem sourceCRS = null;
        CoordinateReferenceSystem targetCRS = null;
        MathTransform transform = null;
        Geometry targetGeometry = null;
        try {
            sourceCRS = CRS.decode("EPSG:4326");
            targetCRS = CRS.decode("EPSG:27700");
            transform = CRS.findMathTransform(sourceCRS, targetCRS);
            //TransformException
            targetGeometry = JTS.transform(a_Point, transform);
        } catch (Exception e) {
            e.printStackTrace();

        }
        System.out.println(targetGeometry.toString());
        Point xy_Point = (Point) targetGeometry;
        result[0] = xy_Point.getX();
        result[1] = xy_Point.getY();
        return result;
    }
}
