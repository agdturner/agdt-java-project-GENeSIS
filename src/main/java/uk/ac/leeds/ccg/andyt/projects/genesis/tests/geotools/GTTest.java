package uk.ac.leeds.ccg.andyt.projects.genesis.tests.geotools;

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
 * Hello world!
 *
 */
public class GTTest {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        System.out.println("Hello GeoTools:" + GeoTools.getVersion());

        GeometryFactory geometryFactory = new GeometryFactory();
        //PrecisionModel precisionModel = geometryFactory.getPrecisionModel();
        double x = 420000d;
        double y = 450000d;
        Point a_Point = geometryFactory.createPoint(new Coordinate(x, y));
        System.out.println(a_Point.toString());

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
        System.out.println(targetGeometry.toString());



    }
}
