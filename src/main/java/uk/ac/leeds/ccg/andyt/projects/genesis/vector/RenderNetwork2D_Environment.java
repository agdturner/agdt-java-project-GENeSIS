package uk.ac.leeds.ccg.andyt.projects.genesis.vector;

/**
 * Library for handling spatial vector data. Copyright (C) 2009 Andy Turner,
 * CCG, University of Leeds, UK.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 */
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
import uk.ac.leeds.ccg.andyt.vector.visualisation.VectorRenderNetwork2D;
//import uk.ac.leeds.ccg.andyt.;

/**
 * Class for rendering Network2D instances.
 */
public class RenderNetwork2D_Environment
        extends VectorRenderNetwork2D {

    public GENESIS_Environment ge;

    public RenderNetwork2D_Environment(
            GENESIS_Environment _GENESIS_Environment,
            JFrame a_JFrame,
            int width,
            int height,
            Vector_Envelope2D a_VectorEnvelope2D) {
        this.ge = _GENESIS_Environment;
        this._width_int = width;
        this._height_int = height;
        this._JFrame = a_JFrame;
        this._JFrame.getContentPane().add("Center", this);
        this.init();
        _JFrame.pack();
        _JFrame.setSize(new Dimension(this._width_int, this._height_int));
        //_JFrame.pack();
        _JFrame.setVisible(true);
        this._VectorEnvelope2D = a_VectorEnvelope2D;
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
                    ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(ge.HandleOutOfMemoryErrorTrue).getMale(a_AgentID,
                    GENESIS_Person.getTypeLivingMale_String(),
                    ge.HandleOutOfMemoryErrorTrue);
            a_Network2D = a_Person._reporting_VectorNetwork2D;
            Set<Vector_Point2D> keys = a_Network2D._Connection_HashMap.keySet();
            Iterator bIterator;
            bIterator = keys.iterator();
            Vector_Point2D a_Point2D;
            HashSet<Connection> a_Connection_HashSet;
            while (bIterator.hasNext()) {
                a_Point2D = (Vector_Point2D) bIterator.next();
                a_Connection_HashSet = (HashSet<Connection>) a_Network2D._Connection_HashMap.get(a_Point2D);
                draw(
                        a_Point2D,
                        a_Connection_HashSet);
            }
        }
        a_Iterator = ge.getTrafficModel()._MalePopulation_HashSet.iterator();
        while (a_Iterator.hasNext()) {
            a_AgentID = (Long) a_Iterator.next();
            a_Person =
                    ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(ge.HandleOutOfMemoryErrorTrue).getMale(a_AgentID,
                    GENESIS_Person.getTypeLivingFemale_String(),
                    ge.HandleOutOfMemoryErrorTrue);
            a_Network2D = a_Person._reporting_VectorNetwork2D;
            Set<Vector_Point2D> keys = a_Network2D._Connection_HashMap.keySet();
            Iterator bIterator;
            bIterator = keys.iterator();
            Vector_Point2D a_Point2D;
            HashSet<Connection> a_Connection_HashSet;
            while (bIterator.hasNext()) {
                a_Point2D = (Vector_Point2D) bIterator.next();
                a_Connection_HashSet = (HashSet<Connection>) a_Network2D._Connection_HashMap.get(a_Point2D);
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
//        boolean handleOutOfMemoryError = _Environment.HandleOutOfMemoryError;
//        BigDecimal[] reportingDimensions = _Environment._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.get_Dimensions(handleOutOfMemoryError);
//        BigDecimal[] networkDimensions = _Environment._network_Grid2DSquareCellDouble.get_Dimensions(handleOutOfMemoryError);
//        int scale = reportingDimensions[0].divide(networkDimensions[0]).intValue();
//        this._Graphics2D.setPaint(Color.RED);
//        int ax = (int) (a_Point2D._x.doubleValue() / scale);
//        int ay = (int) (a_Point2D._y.doubleValue() / scale);
//        int bx;
//        int by;
//        Iterator aIterator;
//        aIterator = a_Connection_HashSet.iterator();
//        while (aIterator.hasNext()) {
//            Connection a_Connection = (Connection) aIterator.next();
//            bx = (int) (a_Connection._Point2D._x.doubleValue() / scale);
//            by = (int) (a_Connection._Point2D._y.doubleValue() / scale);
//            if (! (ax == bx && ay == by)) {
//                this._Graphics2D.drawLine(
//                        ax,
//                        _height_int - ay,
//                        bx,
//                        _height_int - by);
//            }
//        }
//    }
    @Override
    public void draw(
            Vector_Point2D a_Point2D,
            HashSet<Connection> a_Connection_HashSet) {
        boolean handleOutOfMemoryError = ge.HandleOutOfMemoryError;
        Grids_Dimensions reportingDimensions;
        reportingDimensions = ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.getDimensions(handleOutOfMemoryError);
        Grids_Dimensions networkDimensions = ge._network_Grid2DSquareCellDouble.getDimensions(handleOutOfMemoryError);
//        BigDecimal[] reportingDimensions = ge._reportingPopulationDensityAggregate_Grid2DSquareCellDouble.get_Dimensions(handleOutOfMemoryError);
//        BigDecimal[] networkDimensions = ge._network_Grid2DSquareCellDouble.get_Dimensions(handleOutOfMemoryError);
        BigDecimal reportingDimensionXrange_BigDecimal = reportingDimensions.getWidth();
        BigDecimal reportingDimensionYrange_BigDecimal = reportingDimensions.getHeight();
        BigDecimal scale_BigDecimal = reportingDimensions.getCellsize().divide(networkDimensions.getCellsize());
        int scale = scale_BigDecimal.intValue() + 1;
        BigDecimal width_BigDecimal = new BigDecimal(_width_int);
        BigDecimal height_BigDecimal = new BigDecimal(_height_int);
        this._Graphics2D.setPaint(Color.RED);
        int ax = (((a_Point2D._x.subtract(reportingDimensions.getXMin())).divide(reportingDimensionXrange_BigDecimal, scale, RoundingMode.DOWN)).multiply(width_BigDecimal)).intValue();
        int ay = (((a_Point2D._y.subtract(reportingDimensions.getYMin())).divide(reportingDimensionYrange_BigDecimal, scale, RoundingMode.DOWN)).multiply(height_BigDecimal)).intValue();
        int bx;
        int by;
        Iterator aIterator;
        aIterator = a_Connection_HashSet.iterator();
        while (aIterator.hasNext()) {
            Connection a_Connection = (Connection) aIterator.next();
            bx = (((a_Connection._Point2D._x.subtract(reportingDimensions.getXMin())).divide(reportingDimensionXrange_BigDecimal, scale, RoundingMode.DOWN)).multiply(width_BigDecimal)).intValue();
            by = (((a_Connection._Point2D._y.subtract(reportingDimensions.getYMin())).divide(reportingDimensionYrange_BigDecimal, scale, RoundingMode.DOWN)).multiply(height_BigDecimal)).intValue();
//            System.out.println(
//                    " ax " + ax +
//                    " ay " + ay +
//                    " bx " + bx +
//                    " by " + by);
            if (!(ax == bx && ay == by)) {
                this._Graphics2D.drawLine(
                        ax,
                        _height_int - ay,
                        bx,
                        _height_int - by);
            }
        }
    }
}
