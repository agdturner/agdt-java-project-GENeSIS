/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.process;

import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import uk.ac.leeds.ccg.andyt.grids.core.grid.Grids_AbstractGrid2DSquareCell;
import uk.ac.leeds.ccg.andyt.grids.exchange.Grids_ImageExporter;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Agent;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_AgentCollectionManager;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_FemaleCollection;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_MaleCollection;
import uk.ac.leeds.ccg.andyt.projects.genesis.geotools.GTMisc;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_AgentCollection;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Female;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Male;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_PersonFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Person;
import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.GENESIS_Time;
import uk.ac.leeds.ccg.andyt.agdtcensus.cas.CASAreaEastingNorthingDataRecord;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Envelope2D;
import uk.ac.leeds.ccg.andyt.vector.visualisation.VectorImageManipulation;
import uk.ac.leeds.ccg.andyt.vector.visualisation.VectorOverlayComponent_Network2D;
import uk.ac.leeds.ccg.andyt.vector.visualisation.VectorRenderNetwork2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_LineSegment2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Network2D;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;
import uk.ac.leeds.ccg.andyt.projects.genesis.vector.RenderNetwork2D_Environment;
import uk.ac.leeds.ccg.andyt.vector.core.Vector_Environment;

/**
 *
 * @author geoagdt
 */
public abstract class AbstractTrafficModel extends Abstract_GENESIS_Model {

    static final long serialVersionUID = 1L;
    //public MathContext _MathContextForCalculations;
    //public MathContext _MathContextForNetworkCalculations;
    //public MathContext _MathContextForNetworkDestinations;
    // Fields for _Display to screen
    //public long _Display_NRows;
    //public long _Display_NCols;
    //public long _NRows;
    //public long _NCols;
    // A grid of populationDensity
//    public Grid2DSquareCellDouble _PopulationDensity_Grid2DSquareCellDouble;
    // A grid of aggregatePopulationDensity
//    public Grid2DSquareCellDouble _AggregatePopulationDensity_Grid2DSquareCellDouble;
    // A grid of accessibility
//    public Grid2DSquareCellDouble _Accessibility_Grid2DSquareCellDouble;
    // A grid for storing resources
//    public Grid2DSquareCellDouble _Resources_Grid2DSquareCellDouble;
    public HashSet<Long> _MalePopulation_HashSet;
    public HashSet<Long> _FemalePopulation_HashSet;
    public GENESIS_PersonFactory _PersonFactory;
    public BigDecimal _SpeedDefault_BigDecimal;
//    public HashSet _Resource_HashSet;
//    public double resourceGridCellInitial_double;
//    public double resourceGridCellRecovery_double;
//    public double resourceGridCellMax_double;
//    public double resourcePersonInitial_double;
//    public double resourcePersonMax_double;

    public void init_Rounding(
            BigDecimal cellsize_BigDecimal,
            BigDecimal minx,
            BigDecimal miny,
            boolean handleOutOfMemoryError) {
        try {
            init_Rounding(
                    cellsize_BigDecimal,
                    minx,
                    miny);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve(
                        handleOutOfMemoryError);
                init_Rounding(
                        cellsize_BigDecimal,
                        minx,
                        miny,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected void init_Rounding(
            BigDecimal cellsize_BigDecimal,
            BigDecimal minx,
            BigDecimal miny) {
        BigDecimal halfCellsize = cellsize_BigDecimal.divide(
                new BigDecimal("2"),
                ge._DecimalPlacePrecisionForCalculations,
                RoundingMode.UNNECESSARY).stripTrailingZeros();
        int halfCellsize_scale = halfCellsize.scale();
        if (minx.scale() < halfCellsize_scale) {
            ge._ToRoundToX_BigDecimal = halfCellsize;
        } else {
            ge._ToRoundToX_BigDecimal = halfCellsize.add(
                    minx.setScale(halfCellsize_scale - 1, RoundingMode.CEILING).subtract(
                            minx.setScale(halfCellsize_scale, RoundingMode.FLOOR)));
        }
        if (miny.scale() < halfCellsize_scale) {
            ge._ToRoundToY_BigDecimal = halfCellsize;
        } else {
            ge._ToRoundToY_BigDecimal = halfCellsize.add(
                    miny.setScale(halfCellsize_scale - 1, RoundingMode.CEILING).subtract(
                            miny.setScale(halfCellsize_scale, RoundingMode.FLOOR)));
        }
    }

    public abstract void simulate();

    public GENESIS_Female getFemale(
            Long a_Agent_ID,
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager
                    = ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
                            handleOutOfMemoryError);
            String type = GENESIS_Person.getTypeLivingFemale_String();
            Long a_AgentCollection_ID
                    = a_GENESIS_AgentCollectionManager.getFemaleCollection_ID(
                            a_Agent_ID,
                            type,
                            handleOutOfMemoryError);
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection
                    = a_GENESIS_AgentCollectionManager.getFemaleCollection(
                            a_AgentCollection_ID,
                            type,
                            handleOutOfMemoryError);
            GENESIS_Female result = getFemale(
                    a_Agent_ID,
                    a_GENESIS_FemaleCollection);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager
                        = ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
                                ge.HandleOutOfMemoryErrorFalse);
                String type = GENESIS_Person.getTypeLivingFemale_String();
                Long a_FemaleCollection_ID
                        = a_GENESIS_AgentCollectionManager.getFemaleCollection_ID(
                                a_Agent_ID,
                                type,
                                ge.HandleOutOfMemoryErrorFalse);
                GENESIS_FemaleCollection a_GENESIS_FemaleCollection
                        = a_GENESIS_AgentCollectionManager.getFemaleCollection(
                                a_FemaleCollection_ID,
                                type,
                                ge.HandleOutOfMemoryErrorFalse);
                if (a_GENESIS_AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
                        a_GENESIS_FemaleCollection,
                        ge.HandleOutOfMemoryErrorFalse) < 1L) {
                    ge.swapToFile_Grid2DSquareCellChunk(
                            ge.HandleOutOfMemoryErrorFalse);
                }
                ge.init_MemoryReserve(
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
                return getFemale(
                        a_Agent_ID,
                        a_GENESIS_FemaleCollection,
                        a_GENESIS_AgentCollectionManager,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public GENESIS_Male getMale(
            Long a_Agent_ID,
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager
                    = ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
                            handleOutOfMemoryError);
            String type = GENESIS_Person.getTypeLivingMale_String();
            Long a_AgentCollection_ID
                    = a_GENESIS_AgentCollectionManager.getMaleCollection_ID(
                            a_Agent_ID,
                            type,
                            handleOutOfMemoryError);
            GENESIS_MaleCollection a_GENESIS_MaleCollection
                    = a_GENESIS_AgentCollectionManager.getMaleCollection(
                            a_AgentCollection_ID,
                            type,
                            handleOutOfMemoryError);
            GENESIS_Male result = getMale(
                    a_Agent_ID,
                    a_GENESIS_MaleCollection);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager
                        = ge._GENESIS_AgentEnvironment.get_AgentCollectionManager(
                                ge.HandleOutOfMemoryErrorFalse);
                String type = GENESIS_Person.getTypeLivingMale_String();
                Long a_MaleCollection_ID
                        = a_GENESIS_AgentCollectionManager.getMaleCollection_ID(
                                a_Agent_ID,
                                type,
                                ge.HandleOutOfMemoryErrorFalse);
                GENESIS_MaleCollection a_GENESIS_MaleCollection
                        = a_GENESIS_AgentCollectionManager.getMaleCollection(
                                a_MaleCollection_ID,
                                type,
                                ge.HandleOutOfMemoryErrorFalse);
                if (a_GENESIS_AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(
                        a_GENESIS_MaleCollection,
                        ge.HandleOutOfMemoryErrorFalse) < 1L) {
                    ge.swapToFile_Grid2DSquareCellChunk(
                            ge.HandleOutOfMemoryErrorFalse);
                }
                ge.init_MemoryReserve(
                        a_GENESIS_MaleCollection,
                        handleOutOfMemoryError);
                return getMale(
                        a_Agent_ID,
                        a_GENESIS_MaleCollection,
                        a_GENESIS_AgentCollectionManager,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    protected GENESIS_Female getFemale(
            Long a_Agent_ID,
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection) {
        return a_GENESIS_FemaleCollection.getFemale(
                a_Agent_ID,
                ge._HandleOutOfMemoryError_boolean);
    }

    protected GENESIS_Male getMale(
            Long a_Agent_ID,
            GENESIS_MaleCollection a_GENESIS_MaleCollection) {
        return a_GENESIS_MaleCollection.getMale(
                a_Agent_ID,
                ge._HandleOutOfMemoryError_boolean);
    }

    public GENESIS_Female getFemale(
            Long a_Agent_ID,
            GENESIS_FemaleCollection a_GENESIS_FemaleCollection,
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager,
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_Female result = getFemale(
                    a_Agent_ID,
                    a_GENESIS_FemaleCollection);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_FemaleCollection,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (a_GENESIS_AgentCollectionManager.swapToFile_FemaleCollectionExcept_Account(
                        a_GENESIS_FemaleCollection,
                        ge.HandleOutOfMemoryErrorFalse) < 1L) {
                    ge.swapToFile_Grid2DSquareCellChunk(
                            ge.HandleOutOfMemoryErrorFalse);
                }
                ge.init_MemoryReserve(
                        a_GENESIS_FemaleCollection,
                        handleOutOfMemoryError);
                return getFemale(
                        a_Agent_ID,
                        a_GENESIS_FemaleCollection,
                        a_GENESIS_AgentCollectionManager,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public GENESIS_Male getMale(
            Long a_Agent_ID,
            GENESIS_MaleCollection a_GENESIS_MaleCollection,
            GENESIS_AgentCollectionManager a_GENESIS_AgentCollectionManager,
            boolean handleOutOfMemoryError) {
        try {
            GENESIS_Male result = getMale(
                    a_Agent_ID,
                    a_GENESIS_MaleCollection);
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    a_GENESIS_MaleCollection,
                    handleOutOfMemoryError);
            return result;
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                if (a_GENESIS_AgentCollectionManager.swapToFile_MaleCollectionExcept_Account(
                        a_GENESIS_MaleCollection,
                        ge.HandleOutOfMemoryErrorFalse) < 1L) {
                    ge.swapToFile_Grid2DSquareCellChunk(
                            ge.HandleOutOfMemoryErrorFalse);
                }
                ge.init_MemoryReserve(
                        a_GENESIS_MaleCollection,
                        handleOutOfMemoryError);
                return getMale(
                        a_Agent_ID,
                        a_GENESIS_MaleCollection,
                        a_GENESIS_AgentCollectionManager,
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    public void visualiseNetworkOnGrid(
            Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell,
            HashSet<Vector_Network2D> a_Network2D_HashSet,
            File a_Directory) {
        boolean handleOutOfMemoryError = ge._HandleOutOfMemoryError_boolean;
        int width = (int) a_Grid2DSquareCell.get_NCols(handleOutOfMemoryError);
        int height = (int) a_Grid2DSquareCell.get_NRows(handleOutOfMemoryError);
        String time_String = ge._Time.toString();
        Grids_ImageExporter aImageExporter = new Grids_ImageExporter(ge.ge);
        //String type = "PNG";
        String type = "JPEG";
        File file = new File(
                a_Directory,
                time_String + "." + type);
        aImageExporter.toGreyScaleImage(
                a_Grid2DSquareCell,
                ge.ge.getGridProcessor(),
                file,
                type,
                ge._HandleOutOfMemoryError_boolean);
        VectorRenderNetwork2D a_RenderNetwork2D = new VectorRenderNetwork2D(
                new JFrame(),
                width,
                height,
                a_Network2D_HashSet);
        VectorOverlayComponent_Network2D a_OverlayComponent_Network2D
                = new VectorOverlayComponent_Network2D(a_RenderNetwork2D);
        try {
            a_OverlayComponent_Network2D.readImage(file.toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        a_RenderNetwork2D.add(a_OverlayComponent_Network2D);
        a_OverlayComponent_Network2D._BufferedImage = VectorImageManipulation.resize(
                a_OverlayComponent_Network2D._BufferedImage,
                width,
                height);
        //a_OverlayComponent_Network2D.setBounds(0, 0, width, height);
        a_OverlayComponent_Network2D.setVisible(true);
        a_RenderNetwork2D.setVisible(true);
        a_RenderNetwork2D.paintAll(a_RenderNetwork2D.getGraphics());
        BufferedImage a_BufferedImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB);
        SwingUtilities.paintComponent(
                a_BufferedImage.getGraphics(),
                //a_OverlayComponent_Network2D._Image.getScaledInstance(width,height,Image.SCALE_DEFAULT).getGraphics(),
                //a_OverlayComponent_Network2D.getGraphics(),
                a_OverlayComponent_Network2D,//aJFrame,
                new JPanel(),
                0,
                0,
                width,
                height);
        try {
            ImageIO.write(
                    a_BufferedImage,
                    //a_OverlayComponent_Network2D._BufferedImage,
                    "jpeg",
                    new File(
                            a_Directory,
                            "out2.JPEG"));
            boolean writtenPNG = ImageIO.write(
                    a_BufferedImage,
                    //a_OverlayComponent_Network2D._BufferedImage,
                    "png",
                    new File(
                            a_Directory,
                            "out2.PNG"));
            System.out.println("writtenPNG " + writtenPNG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void visualiseNetworkOnGrid(
            HashSet<Vector_Network2D> a_Network2D_HashSet,
            Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell,
            File a_Directory) {
        int width = (int) a_Grid2DSquareCell.get_NCols(ge._HandleOutOfMemoryError_boolean);
        int height = (int) a_Grid2DSquareCell.get_NRows(ge._HandleOutOfMemoryError_boolean);
        String time_String = ge._Time.toString();
        Grids_ImageExporter aImageExporter = new Grids_ImageExporter(ge.ge);
        //String type = "PNG";
        String type = "JPEG";
        File file = new File(
                a_Directory,
                time_String + "." + type);
        aImageExporter.toGreyScaleImage(
                a_Grid2DSquareCell,
                ge.ge.getGridProcessor(),
                file,
                type,
                ge._HandleOutOfMemoryError_boolean);
        VectorRenderNetwork2D a_RenderNetwork2D = new VectorRenderNetwork2D(
                new JFrame(),
                width,
                height,
                a_Network2D_HashSet);
        VectorOverlayComponent_Network2D a_OverlayComponent_Network2D
                = new VectorOverlayComponent_Network2D(a_RenderNetwork2D);
        try {
            a_OverlayComponent_Network2D.readImage(file.toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        a_RenderNetwork2D.add(a_OverlayComponent_Network2D);
        a_OverlayComponent_Network2D._BufferedImage = VectorImageManipulation.resize(
                a_OverlayComponent_Network2D._BufferedImage,
                width,
                height);
        //a_OverlayComponent_Network2D.setBounds(0, 0, width, height);
        a_OverlayComponent_Network2D.setVisible(true);
        a_RenderNetwork2D.setVisible(true);
        a_RenderNetwork2D.paintAll(a_RenderNetwork2D.getGraphics());
        BufferedImage a_BufferedImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB);
        SwingUtilities.paintComponent(
                a_BufferedImage.getGraphics(),
                //a_OverlayComponent_Network2D._Image.getScaledInstance(width,height,Image.SCALE_DEFAULT).getGraphics(),
                //a_OverlayComponent_Network2D.getGraphics(),
                a_OverlayComponent_Network2D,//aJFrame,
                new JPanel(),
                0,
                0,
                width,
                height);
        try {
//            ImageIO.write(
//                    a_BufferedImage,
//                    //a_OverlayComponent_Network2D._BufferedImage,
//                    "jpeg",
//                    new File("c:/temp/out2.JPEG"));
            boolean writtenPNG = ImageIO.write(
                    a_BufferedImage,
                    //a_OverlayComponent_Network2D._BufferedImage,
                    "png",
                    new File(
                            a_Directory,
                            time_String + ".PNG"));
            System.out.println("writtenPNG " + writtenPNG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param a_VectorEnvelope
     * @param a_Grid2DSquareCell The grid to visualise on.
     */
    public void visualiseNetworkOnGrid1(
            Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell,
            Vector_Envelope2D a_VectorEnvelope) {
        int width = (int) a_Grid2DSquareCell.get_NCols(true);
        int height = (int) a_Grid2DSquareCell.get_NRows(true);
        String a_Time_toString = ge._Time.toString();
        String type = "PNG";
        File directory = new File(
                ge._Directory,
                "PopulationDensityGrid");
        directory.mkdir();
        File file = new File(
                directory,
                a_Time_toString + "." + type);
        _ImageExporter.toGreyScaleImage(
                a_Grid2DSquareCell,
                ge.ge.getGridProcessor(),
                file,
                type,
                ge._HandleOutOfMemoryError_boolean);
        JFrame a_JFrame = new JFrame();
        RenderNetwork2D_Environment a_RenderNetwork2D = new RenderNetwork2D_Environment(
                ge,
                a_JFrame,
                width,
                height,
                a_VectorEnvelope);
//        VectorRenderNetwork2D a_RenderNetwork2D = new VectorRenderNetwork2D(
//                a_JFrame,
//                (int) _NCols,
//                (int) _NRows,
//                scale,
//                a_Network2D_HashSet);
//        RenderNetwork2D_Environment a_RenderNetwork2D = new RenderNetwork2D_Environment(
//                _GENESIS_Environment,
//                a_JFrame,
//                width,
//                height,
//                a_Network2D_HashSet);
        VectorOverlayComponent_Network2D a_OverlayComponent_Network2D
                = new VectorOverlayComponent_Network2D(a_RenderNetwork2D);
        try {
            a_OverlayComponent_Network2D.readImage(file.toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        a_RenderNetwork2D.add(a_OverlayComponent_Network2D);
//        a_OverlayComponent_Network2D._BufferedImage = VectorImageManipulation.resize(
//                a_OverlayComponent_Network2D._BufferedImage,
//                width,
//                height);
        //a_OverlayComponent_Network2D.setBounds(0, 0, width, height);
        a_OverlayComponent_Network2D.setVisible(true);
        a_RenderNetwork2D.setVisible(true);
        a_RenderNetwork2D.paintAll(a_RenderNetwork2D.getGraphics());
        BufferedImage a_BufferedImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB);
        JPanel a_JPanel = new JPanel();
        SwingUtilities.paintComponent(
                a_BufferedImage.getGraphics(),
                //a_OverlayComponent_Network2D._Image.getScaledInstance(width,height,Image.SCALE_DEFAULT).getGraphics(),
                //a_OverlayComponent_Network2D.getGraphics(),
                a_OverlayComponent_Network2D,//aJFrame,
                a_JPanel,
                0,
                0,
                width,
                height);
        try {
            directory = new File(
                    ge._Directory,
                    "NetworkOnPopulationDensityGrid");
            directory.mkdir();
            file = new File(
                    directory,
                    a_Time_toString + "." + type);
            boolean writtenPNG = ImageIO.write(
                    a_BufferedImage,
                    //a_OverlayComponent_Network2D._BufferedImage,
                    type,
                    file);
            System.out.println("writtenPNG " + writtenPNG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        a_OverlayComponent_Network2D._RenderNetwork2D.destroy();
        a_OverlayComponent_Network2D.removeAll();
        a_OverlayComponent_Network2D = null;
        a_RenderNetwork2D.destroy();
        a_JPanel.removeAll();
        a_JFrame.dispose();
    }

    /**
     *
     * @param a_Network2D_HashSet
     * @param a_Grid2DSquareCell The grid to visualise on.
     * @param a_VectorEnvelope
     */
    public void visualiseNetworkOnGrid1(
            HashSet<Vector_Network2D> a_Network2D_HashSet,
            Grids_AbstractGrid2DSquareCell a_Grid2DSquareCell,
            Vector_Envelope2D a_VectorEnvelope) {
        int width = (int) a_Grid2DSquareCell.get_NCols(true);
        int height = (int) a_Grid2DSquareCell.get_NRows(true);
        String a_Time_toString = ge._Time.toString();
        String type = "PNG";
        File directory = new File(
                ge._Directory,
                "PopulationDensityGrid");
        directory.mkdir();
        File file = new File(
                directory,
                a_Time_toString + "." + type);
        _ImageExporter.toGreyScaleImage(
                a_Grid2DSquareCell,
                ge.ge.getGridProcessor(),
                file,
                type,
                ge._HandleOutOfMemoryError_boolean);
        JFrame a_JFrame = new JFrame();
        VectorRenderNetwork2D a_RenderNetwork2D = new VectorRenderNetwork2D(
                a_JFrame,
                width,
                height,
                a_Network2D_HashSet,
                a_VectorEnvelope);
//        VectorRenderNetwork2D a_RenderNetwork2D = new VectorRenderNetwork2D(
//                a_JFrame,
//                (int) _NCols,
//                (int) _NRows,
//                scale,
//                a_Network2D_HashSet);
//        RenderNetwork2D_Environment a_RenderNetwork2D = new RenderNetwork2D_Environment(
//                _GENESIS_Environment,
//                a_JFrame,
//                width,
//                height,
//                a_Network2D_HashSet);
        VectorOverlayComponent_Network2D a_OverlayComponent_Network2D
                = new VectorOverlayComponent_Network2D(a_RenderNetwork2D);
        try {
            a_OverlayComponent_Network2D.readImage(file.toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
        a_RenderNetwork2D.add(a_OverlayComponent_Network2D);
//        a_OverlayComponent_Network2D._BufferedImage = VectorImageManipulation.resize(
//                a_OverlayComponent_Network2D._BufferedImage,
//                width,
//                height);
        //a_OverlayComponent_Network2D.setBounds(0, 0, width, height);
        a_OverlayComponent_Network2D.setVisible(true);
        a_RenderNetwork2D.setVisible(true);
        a_RenderNetwork2D.paintAll(a_RenderNetwork2D.getGraphics());
        BufferedImage a_BufferedImage = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_INT_RGB);
        JPanel a_JPanel = new JPanel();
        SwingUtilities.paintComponent(
                a_BufferedImage.getGraphics(),
                //a_OverlayComponent_Network2D._Image.getScaledInstance(width,height,Image.SCALE_DEFAULT).getGraphics(),
                //a_OverlayComponent_Network2D.getGraphics(),
                a_OverlayComponent_Network2D,//aJFrame,
                a_JPanel,
                0,
                0,
                width,
                height);
        try {
            directory = new File(
                    ge._Directory,
                    "NetworkOnPopulationDensityGrid");
            directory.mkdir();
            file = new File(
                    directory,
                    a_Time_toString + "." + type);
            boolean writtenPNG = ImageIO.write(
                    a_BufferedImage,
                    //a_OverlayComponent_Network2D._BufferedImage,
                    type,
                    file);
            System.out.println("writtenPNG " + writtenPNG);
        } catch (Exception e) {
            e.printStackTrace();
        }
        a_OverlayComponent_Network2D._RenderNetwork2D.destroy();
        a_OverlayComponent_Network2D.removeAll();
        a_OverlayComponent_Network2D = null;
        a_RenderNetwork2D.destroy();
        a_JPanel.removeAll();
        a_JFrame.dispose();
    }

    /**
     * Work needed here: Split into movement while at work, movement to work and
     * movement home from work and movement around home. At the moment only one
     * mode of movement implemented... Need to simplify this code (split into
     * smaller methods)
     *
     * Move people around in the environment. Each person has a location and a
     * heading and a _Speed. People either get to where they are heading and
     * stop, or set off somewhere else, or they just move to where they are
     * heading at their given _Speed and don't make it. Each movement is
     * accounted for in a persons _Network2D once they reach a heading. The time
     * a person spends in a cell is accounted for in an aggregate form in
     * _AggregatePopulationDensity_Grid2DSquareCellDouble.
     *
     * Movement is restricted to a grid network. Other than this as yet the
     * movement is not contrained in that all people can move down the same part
     * of the network in any tick.
     *
     * A development s to constrain for maximum flow. This might involve
     * implementing some form of queue. To do this a radical refactor may be
     * required... At the same time variable _Speed should be handled...
     */
    public void simulateMovement(BigDecimal tollerance) {
        Iterator<Long> a_Iterator;
        a_Iterator = _FemalePopulation_HashSet.iterator();
        if (_FemalePopulation_HashSet.isEmpty()) {
            System.out.println("_FemalePopulation_HashSet.isEmpty()");
        }
        //MathContext a_MathContext = new MathContext(a_DecimalPlacePrecision);
        int counter = 0;
        Long a_Agent_ID = null;
        Long a_AgentCollection_ID = null;
        GENESIS_FemaleCollection a_FemaleCollection = null;
        GENESIS_Person a_Person = null;
        while (a_Iterator.hasNext()) {
            //if (counter % 10 == 0) {
            System.out.println(
                    "Moving person " + counter + " out of "
                    + _FemalePopulation_HashSet.size() + " persons");
//                if (counter > 1000) {
//                    break;
//                }
            //}
            counter++;
            a_Agent_ID = a_Iterator.next();
            a_Person = getFemale(
                    a_Agent_ID, ge._HandleOutOfMemoryError_boolean);
            a_Person.move(
                    tollerance,
                    ge._HandleOutOfMemoryError_boolean);
//
//            // Set speeds
//            if (_GENESIS_Environment._Time._SecondOfDay < a_Person._Work_Time[0]._SecondOfDay ||
//                    _GENESIS_Environment._Time._SecondOfDay > a_Person._Work_Time[1]._SecondOfDay) {
//                if (a_Person._Point2D.equals(a_Person._Household._Point2D)) {
//                    a_Person._Speed = 0.0d;
//                } else {
//                    a_Person._Speed = 100.0d;
//                }
//            } else {
//                a_Person._Speed = 5.0d;
//            }
//
//            /*
//             * GENESIS_Person is stationary
//             */
//            if (a_Person._Speed == 0.0d) {
//                System.out.println("GENESIS_Person now stationary");
//                // Need to set _Speed if the time is right...
//                break;
//            }
//            boolean movementDone = false;
//            double distance = a_Person._Speed;
//            BigDecimal distance0_BigDecimal = new BigDecimal(distance);
//            long row = _AggregatePopulationDensity_Grid2DSquareCellDouble.getCellRowIndex(
//                    a_Person._Point2D._y,
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            long col = _AggregatePopulationDensity_Grid2DSquareCellDouble.getCellColIndex(
//                    a_Person._Point2D._x,
//                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            while (distance > 0 && !movementDone) {
//                Vector_LineSegment2D a_LineSegment2D = new Vector_LineSegment2D(
//                        a_Person._Point2D,
//                        a_Person._Heading_Point2D);
//                /*
//                 * +---+---+---+
//                 * | 7 | 8 | 1 |
//                 * +---+---+---+
//                 * | 6 | 0 | 2 |
//                 * +---+---+---+
//                 * | 5 | 4 | 3 |
//                 * +---+---+---+
//                 */
//                BigDecimal[] a_CellBounds = _GENESIS_Environment._World_Grid2DSquareCellDouble.getCellBounds_BigDecimalArray(
//                        row,
//                        col,
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                int cellBoundaryIntersect = StaticGrids.getCellBoundaryIntersect(
//                        a_LineSegment2D,
//                        a_CellBounds,
//                        true);
//                if (cellBoundaryIntersect == 0) {
//                    /*
//                     * In this case the movement is within the current cell.
//                     * This can take several forms:
//                     * 1. The movement is towards the cell centroid:
//                     * a) It reaches this destination.
//                     * b) It does not.
//                     * 2. The movement is towards the cell boundary
//                     * a) Begins at cell centroid
//                     * b) Not.
//                     */
//                    double distanceToHeading = a_Person._Point2D.getDistance(
//                            a_Person._Heading_Point2D,
//                            a_MathContext).doubleValue();
//                    if (distanceToHeading <= distance) {
//                        if (a_Person._Point2D.equals(a_Person._Heading_Point2D)) {
//                            /*
//                             * Case 2a)
//                             */
//                            a_Person._Network2D.addToNetwork(
//                                    a_Person._Point2D,
//                                    a_Person._Previous_Point2D);
//                            a_Person._Network2D.addToNetwork(
//                                    a_Person._Previous_Point2D,
//                                    a_Person._Point2D);
//                            a_Person._Previous_Point2D = a_Person._Point2D; // Needed...
//                            if (_GENESIS_Environment._Time._SecondOfDay < a_Person._Work_Time[0]._SecondOfDay ||
//                                    _GENESIS_Environment._Time._SecondOfDay > a_Person._Work_Time[1]._SecondOfDay) {
//                                if (a_Person._Point2D.equals(a_Person._Household._Point2D)) {
//                                    /*
//                                     * a_Person is at a_Person._Household._Point2D and its time to remain there.
//                                     */
//                                    // distanceToHeading set for accounting reasons and it can be for breaking out of loop...
//                                    distanceToHeading = distance;
//                                    movementDone = true;
//                                    a_Person._Movement = null;
//                                    a_Person._Speed = 0.0d;
//                                    // Do accounting here?
//                                } else {
//                                    /*
//                                     * a_Person not at a_Person._Household._Point2D.
//                                     * Check a_Person._Movement._Destination_Point2D.equals(a_Person._Household._Point2D)
//                                     * and if not make it so.
//                                     */
//                                    if (!a_Person._Movement._Destination_Point2D.equals(a_Person._Household._Point2D)) {
//                                        a_Person._Heading_Point2D = a_Person._Household._Point2D;
//                                        a_Person.setMovement();
//                                    }
//                                    a_Person._Speed = 1000;
//                                }
//                            } else {
//                                /*
//                                 * Set a_Person._Heading_Point2D using next connection on route.
//                                 * If no further connection on route set new movement.
//                                 */
//                                Connection nextConnection = a_Person.getNextConnectionOnRoute();
//                                if (nextConnection == null) {
//                                    a_Person._Heading_Point2D = StaticGrids.getRandomCellCentroid_Point2D(
//                                            _GENESIS_Environment._World_Grid2DSquareCellDouble,
//                                            _GENESIS_Environment._Random,
//                                            a_Person._Point2D,
//                                            distance0_BigDecimal,
//                                            Vector_Point2D.DefaultDecimalPlacePrecision,
//                                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                                    a_Person.setMovement();
//                                } else {
//                                    a_Person._Heading_Point2D = nextConnection._Point2D;
//                                }
//                            }
//                        } else {
//                            /*
//                             * Case 1a)
//                             */
//                            a_Person._Point2D = a_Person._Heading_Point2D;
//                            //a_Person._Previous_Point2D = a_Person._Point2D; // a_Person._Previous_Point2D only to be updated once a heading is reached!
//                            distance -= distanceToHeading;
//                            _AggregatePopulationDensity_Grid2DSquareCellDouble.addToCell(
//                                    row,
//                                    col,
//                                    distanceToHeading,
//                                    _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                        }
//                    } else {
//                        /*
//                         * Case 1b) 2b)
//                         * Move a_Person towards heading
//                         */
//                        a_Person._Point2D = Movement.getPoint2D(
//                                a_Person._Point2D,
//                                a_Person._Heading_Point2D,
//                                distance);
//                        _AggregatePopulationDensity_Grid2DSquareCellDouble.addToCell(
//                                row,
//                                col,
//                                distance,
//                                _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                        distance = 0;
//                        movementDone = true;
//                    }
//                } else {
//                    Vector_Point2D a_Point2D;
//                    long row0 = row;
//                    long col0 = col;
//                    switch (cellBoundaryIntersect) {
//                        case 1:
//                            a_Point2D = new Vector_Point2D(
//                                    a_CellBounds[2],
//                                    a_CellBounds[3]);
//                            row++;
//                            col++;
//                            break;
//                        case 2:
//                            a_Point2D = new Vector_Point2D(
//                                    a_CellBounds[2],
//                                    a_Person._Point2D._y);
//                            col++;
//                            break;
//                        case 3:
//                            a_Point2D = new Vector_Point2D(
//                                    a_CellBounds[2],
//                                    a_CellBounds[1]);
//                            col++;
//                            row--;
//                            break;
//                        case 4:
//                            a_Point2D = new Vector_Point2D(
//                                    a_Person._Point2D._x,
//                                    a_CellBounds[1]);
//                            row--;
//                            break;
//                        case 5:
//                            a_Point2D = new Vector_Point2D(
//                                    a_CellBounds[0],
//                                    a_CellBounds[1]);
//                            row--;
//                            col--;
//                            break;
//                        case 6:
//                            a_Point2D = new Vector_Point2D(
//                                    a_CellBounds[0],
//                                    a_Person._Point2D._y);
//                            col--;
//                            break;
//                        case 7:
//                            a_Point2D = new Vector_Point2D(
//                                    a_CellBounds[0],
//                                    a_CellBounds[3]);
//                            row++;
//                            col--;
//                            break;
//                        default:
//                            a_Point2D = new Vector_Point2D(
//                                    a_Person._Point2D._x,
//                                    a_CellBounds[3]);
//                            row++;
//                            break;
//                    }
//                    double distanceTravelledInCell =
//                            a_Person._Point2D.getDistance(a_Point2D, a_MathContext).doubleValue();
//                    a_Person._Point2D = a_Point2D;
//                    distance -= distanceTravelledInCell;
//                    // Account
//                    _AggregatePopulationDensity_Grid2DSquareCellDouble.addToCell(
//                            row0,
//                            col0,
//                            distanceTravelledInCell,
//                            _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                }
//            }
        }
    }

    /**
     * 567 3*4 012
     *
     * @param a_Point2D
     * @return
     */
    public Vector_Point2D getRandomAdjoiningCellPoint2D(
            Vector_Point2D a_Point2D) {
        Vector_Point2D result_Point2D;
        Vector_Point2D b_Point2D;
        long aRowIndex = ge._network_Grid2DSquareCellDouble.getCellRowIndex(
                a_Point2D._y, ge._HandleOutOfMemoryError_boolean);
        long aColIndex = ge._network_Grid2DSquareCellDouble.getCellColIndex(
                a_Point2D._x, ge._HandleOutOfMemoryError_boolean);
        long resultRowIndex = 0L;
        long resultColIndex = 0L;
        do {
            int cell = _RandomArray[0].nextInt(8);
            switch (cell) {
                case 0:
                    resultRowIndex = aRowIndex - 1;
                    resultColIndex
                            = aColIndex - 1;
                    break;

                case 1:
                    resultRowIndex = aRowIndex - 1;
                    resultColIndex
                            = aColIndex;
                    break;

                case 2:
                    resultRowIndex = aRowIndex - 1;
                    resultColIndex
                            = aColIndex + 1;
                    break;

                case 3:
                    resultRowIndex = aRowIndex;
                    resultColIndex
                            = aColIndex - 1;
                    break;

                case 4:
                    resultRowIndex = aRowIndex;
                    resultColIndex
                            = aColIndex + 1;
                    break;

                case 5:
                    resultRowIndex = aRowIndex + 1;
                    resultColIndex
                            = aColIndex - 1;
                    break;

                case 6:
                    resultRowIndex = aRowIndex + 1;
                    resultColIndex
                            = aColIndex;
                    break;

                case 7:
                    resultRowIndex = aRowIndex + 1;
                    resultColIndex
                            = aColIndex + 1;
                    break;

            }
        } while (!ge._network_Grid2DSquareCellDouble.isInGrid(
                resultRowIndex,
                resultColIndex,
                ge._HandleOutOfMemoryError_boolean));
        return new Vector_Point2D(
                a_Point2D._Vector_Environment,
                ge._network_Grid2DSquareCellDouble.getCellXBigDecimal(
                        resultColIndex,
                        ge._HandleOutOfMemoryError_boolean),
                ge._network_Grid2DSquareCellDouble.getCellYBigDecimal(
                        resultRowIndex,
                        ge._HandleOutOfMemoryError_boolean));
    }

    /**
     * Converts Easting and Northing from a_CASAreaEastingNorthingDataRecord
     * into screen coordinates
     *
     * @param a_CASAreaEastingNorthingDataRecord
     * @param a_DecimalPlacePrecision
     * @return
     */
//    public Vector_Point2D getScreen_Point2D(
//            CASAreaEastingNorthingDataRecord a_CASAreaEastingNorthingDataRecord,
//            int a_DecimalPlacePrecision) {
//        Vector_Point2D result;
//        double a_Easting = a_CASAreaEastingNorthingDataRecord.get_Easting();
//        double a_Northing = a_CASAreaEastingNorthingDataRecord.get_Northing();
//        double[] a_LatLon = GTMisc.transform_OSGB_To_LatLon(
//                a_Easting,
//                a_Northing);
//        double a_x = ((a_LatLon[1] - _GENESIS_Environment._XMin_double) * (double) _NCols) / _GENESIS_Environment._XRange_double;
//        double a_y = ((a_LatLon[0] - _GENESIS_Environment._YMin_double) * (double) _NRows) / _GENESIS_Environment._YRange_double;
//        long a_row = this._GENESIS_Environment._World_Grid2DSquareCellDouble.getCellRowIndex(a_y, _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//        long a_col = this._GENESIS_Environment._World_Grid2DSquareCellDouble.getCellRowIndex(a_x, _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//        result = new Vector_Point2D(
//                this._GENESIS_Environment._World_Grid2DSquareCellDouble.getCellXBigDecimal(a_col, _GENESIS_Environment._HandleOutOfMemoryError_boolean),
//                this._GENESIS_Environment._World_Grid2DSquareCellDouble.getCellYBigDecimal(a_row, _GENESIS_Environment._HandleOutOfMemoryError_boolean),
//                a_DecimalPlacePrecision);
//        return result;
//    }
    /**
     * @param a_CASAreaEastingNorthingDataRecord
     * @param a_DecimalPlacePrecision
     * @return
     */
    public Vector_Point2D get_OSGB_To_LatLon_Point2D(
            CASAreaEastingNorthingDataRecord a_CASAreaEastingNorthingDataRecord,
            int a_DecimalPlacePrecision) {
        Vector_Point2D result;
        double a_Easting = a_CASAreaEastingNorthingDataRecord.get_Easting();
        double a_Northing = a_CASAreaEastingNorthingDataRecord.get_Northing();
        double[] a_LatLon = GTMisc.transform_OSGB_To_LatLon(
                a_Easting,
                a_Northing);
        result = new Vector_Point2D(
                null,
                a_LatLon[1],
                a_LatLon[0],
                a_DecimalPlacePrecision);
        return result;
    }

//    public void initialiseAccessibility() {
//        double accessibility;
//        for (long row = 0; row < _NRows; row++) {
//            for (long col = 0; col < _NCols; col++) {
//                accessibility = _GENESIS_Environment._Random.nextInt() + 1;
//                _Accessibility_Grid2DSquareCellDouble.setCell(
//                        row,
//                        col,
//                        accessibility,
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            }
//
//        }
//    }
//
//    public void initialiseResources() {
//        for (long row = 0; row < _NRows; row++) {
//            for (long col = 0; col < _NCols; col++) {
//                _Resources_Grid2DSquareCellDouble.setCell(
//                        row,
//                        col,
//                        resourceGridCellInitial_double,
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            }
//        }
//    }
//
//    public void initialiseResources(int number) {
//        _Resource_HashSet = new HashSet<AbstractResource>();
//        Vector_Point2D a_Point2D;
//        long cellRowIndex;
//        long cellColIndex;
//        for (int i = 0; i
//                < number; i++) {
//            cellRowIndex = _GENESIS_Environment._World_Grid2DSquareCellDouble.getCellRowIndex(
//                    _GENESIS_Environment._Random, _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            cellColIndex =
//                    _GENESIS_Environment._World_Grid2DSquareCellDouble.getCellColIndex(
//                    _GENESIS_Environment._Random, _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//        }
//    }
//
//    public void replenishResources() {
//        double resource;
//        for (long row = 0; row < _NRows; row++) {
//            for (long col = 0; col
//                    < _NCols; col++) {
//                resource = _Resources_Grid2DSquareCellDouble.getCell(
//                        row,
//                        col,
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//                resource += resourceGridCellRecovery_double;
//                resource = Math.min(resource, resourceGridCellMax_double);
//                _Resources_Grid2DSquareCellDouble.setCell(
//                        row,
//                        col,
//                        resource,
//                        _GENESIS_Environment._HandleOutOfMemoryError_boolean);
//            }
//        }
//    }
    public Object[] getCellBoundaryIntersection(
            GENESIS_Person aPerson,
            long aPersonPoint2DRowIndex,
            long aPersonPoint2DColIndex,
            Grids_AbstractGrid2DSquareCell a_Grid2DSquareCellDouble,
            int a_DecimalPlacePrecision) {
        Vector_Environment ve;
        ve = new Vector_Environment();
        Object[] result = new Object[3];
        //Coordinate resultCoordinate = null;
        Vector_Point2D result_Point2D = null;
        long nextCellRowIndex = aPersonPoint2DRowIndex;
        long nextCellColIndex = aPersonPoint2DColIndex;
        BigDecimal[] cellBounds_BigDecimalArray = a_Grid2DSquareCellDouble.getCellBounds_BigDecimalArray(
                aPersonPoint2DRowIndex,
                aPersonPoint2DColIndex,
                ge._HandleOutOfMemoryError_boolean);
        // Create lines
        Vector_LineSegment2D xmin_LineSegment2D = new Vector_LineSegment2D(
                new Vector_Point2D(
                        ve,
                        cellBounds_BigDecimalArray[0],
                        cellBounds_BigDecimalArray[1]),
                new Vector_Point2D(
                        ve,
                        cellBounds_BigDecimalArray[0],
                        cellBounds_BigDecimalArray[3]));
        Vector_LineSegment2D xmax_LineSegment2D = new Vector_LineSegment2D(
                new Vector_Point2D(
                        ve,
                        cellBounds_BigDecimalArray[2],
                        cellBounds_BigDecimalArray[1]),
                new Vector_Point2D(
                        ve,
                        cellBounds_BigDecimalArray[2],
                        cellBounds_BigDecimalArray[3]));
        Vector_LineSegment2D ymin_LineSegment2D = new Vector_LineSegment2D(
                new Vector_Point2D(
                        ve,
                        cellBounds_BigDecimalArray[0],
                        cellBounds_BigDecimalArray[1]),
                new Vector_Point2D(
                        ve,
                        cellBounds_BigDecimalArray[2],
                        cellBounds_BigDecimalArray[1]));
        Vector_LineSegment2D ymax_LineSegment2D = new Vector_LineSegment2D(
                new Vector_Point2D(
                        ve,
                        cellBounds_BigDecimalArray[0],
                        cellBounds_BigDecimalArray[3]),
                new Vector_Point2D(
                        ve,
                        cellBounds_BigDecimalArray[2],
                        cellBounds_BigDecimalArray[3]));
        // heading
        Vector_LineSegment2D heading_LineSegment2D = new Vector_LineSegment2D(
                aPerson._Point2D,
                aPerson._Heading_Point2D);
        if (xmin_LineSegment2D.getIntersects(
                aPerson._Point2D,
                a_DecimalPlacePrecision)) {
            System.out.println("Point2D on xmin");
        }
        if (ymin_LineSegment2D.getIntersects(
                aPerson._Point2D,
                a_DecimalPlacePrecision)) {
            System.out.println("Point2D on ymin");
        }
        boolean xminIntersect = aPerson._Heading_Point2D.getIntersects(
                xmin_LineSegment2D,
                a_DecimalPlacePrecision);
        if (!xmin_LineSegment2D.getIntersects(
                aPerson._Point2D,
                a_DecimalPlacePrecision)) {
            if (xminIntersect) {
                if (aPerson._Heading_Point2D.getIntersects(
                        xmin_LineSegment2D,
                        a_DecimalPlacePrecision)) {
                    // new Vector_Point2D needed?
                    result_Point2D = new Vector_Point2D(aPerson._Heading_Point2D);
                    nextCellColIndex--;
                }
            }
        }
        boolean xmaxIntersect = aPerson._Heading_Point2D.getIntersects(
                xmax_LineSegment2D,
                a_DecimalPlacePrecision);
        if (!xmax_LineSegment2D.getIntersects(
                aPerson._Point2D,
                a_DecimalPlacePrecision)) {
            if (xmaxIntersect) {
                if (aPerson._Heading_Point2D.getIntersects(
                        xmax_LineSegment2D,
                        a_DecimalPlacePrecision)) {
                    // new Vector_Point2D needed?
                    result_Point2D = new Vector_Point2D(aPerson._Heading_Point2D);
                    nextCellColIndex++;
                }
            }
        }
        boolean yminIntersect = aPerson._Heading_Point2D.getIntersects(
                ymin_LineSegment2D,
                a_DecimalPlacePrecision);
        if (!ymin_LineSegment2D.getIntersects(
                aPerson._Point2D,
                a_DecimalPlacePrecision)) {
            if (yminIntersect) {
                if (aPerson._Heading_Point2D.getIntersects(
                        ymin_LineSegment2D,
                        a_DecimalPlacePrecision)) {
                    // new Vector_Point2D needed?
                    result_Point2D = new Vector_Point2D(aPerson._Heading_Point2D);
                    nextCellRowIndex--;
                }
            }
        }
        boolean ymaxIntersect = aPerson._Heading_Point2D.getIntersects(
                ymax_LineSegment2D,
                a_DecimalPlacePrecision);
        if (!ymax_LineSegment2D.getIntersects(
                aPerson._Point2D,
                a_DecimalPlacePrecision)) {
            if (ymaxIntersect) {
                if (aPerson._Heading_Point2D.getIntersects(
                        ymax_LineSegment2D,
                        a_DecimalPlacePrecision)) {
                    // new Vector_Point2D needed?
                    result_Point2D = new Vector_Point2D(aPerson._Heading_Point2D);
                    nextCellRowIndex++;
                }
            }
        }
        result[0] = result_Point2D;
        result[1] = nextCellRowIndex;
        result[2] = nextCellColIndex;
        return result;
    }

    /**
     * Keys are integers between 0 and 11 inclusive Values are start and end
     * times for shift work
     */
    public HashMap _Shifts;

    public void init_Shifts(
            boolean handleOutOfMemoryError) {
        try {
            init_Shifts();
            ge.tryToEnsureThereIsEnoughMemoryToContinue(
                    handleOutOfMemoryError);
        } catch (OutOfMemoryError a_OutOfMemoryError) {
            if (handleOutOfMemoryError) {
                ge.clearMemoryReserve();
                ge.swapDataAny();
                ge.initMemoryReserve(
                        handleOutOfMemoryError);
                init_Shifts(
                        handleOutOfMemoryError);
            } else {
                throw a_OutOfMemoryError;
            }
        }
    }

    /**
     * Shiftwork: Shift 0: 0-12 Shift 1: 6-18 Shift 2: 12-24 Shift 3: 18-6
     */
    public void init_Shifts() {
        GENESIS_Time _Time_Start0 = new GENESIS_Time();
        GENESIS_Time _Time_Start = new GENESIS_Time();
        _Time_Start0.setSecondOfDay(0);
        _Time_Start = new GENESIS_Time(_Time_Start0);
        GENESIS_Time _Time_End0 = new GENESIS_Time();
        GENESIS_Time _Time_End = new GENESIS_Time();
        _Time_End0.setSecondOfDay(60 * 60 * 12);
        _Time_End = new GENESIS_Time(_Time_End0);
        GENESIS_Time[] _Shift = new GENESIS_Time[2];
        _Shift[0] = _Time_Start;
        _Shift[1] = _Time_End;
        int _ShiftKey = 0;
        _Shifts = new HashMap();
        _Shifts.put(_ShiftKey, _Shift);
        _ShiftKey++;
        for (int i = 6; i < 24; i += 6) {
            //System.out.println("_Time_Start._SecondOfDay " + _Time_Start._SecondOfDay + ", _Time_End._SecondOfDay " + _Time_End._SecondOfDay );
            // May need further work if we go over 24 hours in seconds...?
            _Time_Start = new GENESIS_Time(_Time_Start0);
            _Time_Start.setSecondOfDay(_Time_Start.getSecondOfDay() + 60 * 60 * i);
            _Time_End = new GENESIS_Time(_Time_End0);
            _Time_End.setSecondOfDay(_Time_End.getSecondOfDay() + 60 * 60 * i);
            if (_Time_End.getSecondOfDay() > GENESIS_Time.NormalSecondsInDay_int) {
                _Time_End.addDay();
                _Time_End.setSecondOfDay(_Time_End.getSecondOfDay() - GENESIS_Time.NormalSecondsInDay_int);
            }
            _Shift = new GENESIS_Time[2];
            _Shift[0] = _Time_Start;
            _Shift[1] = _Time_End;
            _Shifts.put(_ShiftKey, _Shift);
            _ShiftKey++;
        }
    }
}
