package uk.ac.leeds.ccg.andyt.projects.genesis.vector;

import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JFrame;
import uk.ac.leeds.ccg.andyt.grids.core.Grids_Dimensions;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Person;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Network2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Network2D.Connection;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;
import uk.ac.leeds.ccg.andyt.vector.visualisation.Vector_RenderNetwork2D;
//import uk.ac.leeds.ccg.andyt.;
/**
 * Class for rendering Network2D instances.
 */
public class GENESIS_RenderNetwork2D
        extends Vector_RenderNetwork2D {

    public GENESIS_Environment ge;

    public GENESIS_RenderNetwork2D(
            GENESIS_Environment ge,
            JFrame a_JFrame,
            int width,
            int height,
            Vector_Envelope2D a_VectorEnvelope2D) {
        this.ge = ge;
        this.Width = width;
        this.Height = height;
        this._JFrame = a_JFrame;
        this._JFrame.getContentPane().add("Center", this);
        this.init();
        _JFrame.pack();
        _JFrame.setSize(new Dimension(this.Width, this.Height));
        //_JFrame.pack();
        _JFrame.setVisible(true);
        this.Envelope = a_VectorEnvelope2D;
    }

    @Override
    public void draw() {
        Iterator a_Iterator;
        Long a_AgentID;
        GENESIS_Person a_Person;
        Vector_Network2D a_Network2D;
        a_Iterator = ge.getTrafficModel()._FemalePopulation_HashSet.iterator();
        while (a_Iterator.hasNext()) {
            a_AgentID = (Long) a_Iterator.next();
            a_Person =
                    ge.AgentEnvironment.get_AgentCollectionManager(ge.HOOMET).getMale(a_AgentID,
                    GENESIS_Person.getTypeLivingMale_String(),
                    ge.HOOMET);
            a_Network2D = a_Person._reporting_VectorNetwork2D;
            Set<Vector_Point2D> keys = a_Network2D.Connections.keySet();
            Iterator bIterator;
            bIterator = keys.iterator();
            Vector_Point2D a_Point2D;
            HashSet<Connection> a_Connection_HashSet;
            while (bIterator.hasNext()) {
                a_Point2D = (Vector_Point2D) bIterator.next();
                a_Connection_HashSet = (HashSet<Connection>) a_Network2D.Connections.get(a_Point2D);
                draw(
                        a_Point2D,
                        a_Connection_HashSet);
            }
        }
        a_Iterator = ge.getTrafficModel()._MalePopulation_HashSet.iterator();
        while (a_Iterator.hasNext()) {
            a_AgentID = (Long) a_Iterator.next();
            a_Person =
                    ge.AgentEnvironment.get_AgentCollectionManager(ge.HOOMET).getMale(a_AgentID,
                    GENESIS_Person.getTypeLivingFemale_String(),
                    ge.HOOMET);
            a_Network2D = a_Person._reporting_VectorNetwork2D;
            Set<Vector_Point2D> keys = a_Network2D.Connections.keySet();
            Iterator bIterator;
            bIterator = keys.iterator();
            Vector_Point2D a_Point2D;
            HashSet<Connection> a_Connection_HashSet;
            while (bIterator.hasNext()) {
                a_Point2D = (Vector_Point2D) bIterator.next();
                a_Connection_HashSet = (HashSet<Connection>) a_Network2D.Connections.get(a_Point2D);
                draw(
                        a_Point2D,
                        a_Connection_HashSet);
            }
        }
    }

//    @Override
//    public void draw(
//            Vector_Point2D a_Point2D,
//            HashSet<Connection> a_Connection_HashSet) {
//        boolean handleOutOfMemoryError = _Environment.HOOME;
//        BigDecimal[] reportingDimensions = _Environment.ReportingPopulationDensityAggregateGridDouble.get_Dimensions(handleOutOfMemoryError);
//        BigDecimal[] networkDimensions = _Environment.NetworkGridDouble.get_Dimensions(handleOutOfMemoryError);
//        int scale = reportingDimensions[0].divide(networkDimensions[0]).intValue();
//        this._Graphics2D.setPaint(Color.RED);
//        int ax = (int) (a_Point2D.X.doubleValue() / scale);
//        int ay = (int) (a_Point2D.Y.doubleValue() / scale);
//        int bx;
//        int by;
//        Iterator aIterator;
//        aIterator = a_Connection_HashSet.iterator();
//        while (aIterator.hasNext()) {
//            Connection a_Connection = (Connection) aIterator.next();
//            bx = (int) (a_Connection.Location.X.doubleValue() / scale);
//            by = (int) (a_Connection.Location.Y.doubleValue() / scale);
//            if (! (ax == bx && ay == by)) {
//                this._Graphics2D.drawLine(
//                        ax,
//                        Height - ay,
//                        bx,
//                        Height - by);
//            }
//        }
//    }
    @Override
    public void draw(
            Vector_Point2D a_Point2D,
            HashSet<Connection> a_Connection_HashSet) {
        boolean handleOutOfMemoryError = ge.HOOME;
        Grids_Dimensions reportingDimensions;
        reportingDimensions = ge.ReportingPopulationDensityAggregateGridDouble.getDimensions();
        Grids_Dimensions networkDimensions = ge.NetworkGridDouble.getDimensions();
//        BigDecimal[] reportingDimensions = ge.ReportingPopulationDensityAggregateGridDouble.get_Dimensions(handleOutOfMemoryError);
//        BigDecimal[] networkDimensions = ge.NetworkGridDouble.get_Dimensions(handleOutOfMemoryError);
        BigDecimal reportingDimensionXrange_BigDecimal = reportingDimensions.getWidth();
        BigDecimal reportingDimensionYrange_BigDecimal = reportingDimensions.getHeight();
        BigDecimal scale_BigDecimal = reportingDimensions.getCellsize().divide(networkDimensions.getCellsize());
        int scale = scale_BigDecimal.intValue() + 1;
        BigDecimal width_BigDecimal = new BigDecimal(Width);
        BigDecimal height_BigDecimal = new BigDecimal(Height);
        this._Graphics2D.setPaint(Color.RED);
        int ax = (((a_Point2D.X.subtract(reportingDimensions.getXMin())).divide(reportingDimensionXrange_BigDecimal, scale, RoundingMode.DOWN)).multiply(width_BigDecimal)).intValue();
        int ay = (((a_Point2D.Y.subtract(reportingDimensions.getYMin())).divide(reportingDimensionYrange_BigDecimal, scale, RoundingMode.DOWN)).multiply(height_BigDecimal)).intValue();
        int bx;
        int by;
        Iterator aIterator;
        aIterator = a_Connection_HashSet.iterator();
        while (aIterator.hasNext()) {
            Connection a_Connection = (Connection) aIterator.next();
            bx = (((a_Connection.Location.X.subtract(reportingDimensions.getXMin())).divide(reportingDimensionXrange_BigDecimal, scale, RoundingMode.DOWN)).multiply(width_BigDecimal)).intValue();
            by = (((a_Connection.Location.Y.subtract(reportingDimensions.getYMin())).divide(reportingDimensionYrange_BigDecimal, scale, RoundingMode.DOWN)).multiply(height_BigDecimal)).intValue();
//            System.out.println(
//                    " ax " + ax +
//                    " ay " + ay +
//                    " bx " + bx +
//                    " by " + by);
            if (!(ax == bx && ay == by)) {
                this._Graphics2D.drawLine(
                        ax,
                        Height - ay,
                        bx,
                        Height - by);
            }
        }
    }
}
