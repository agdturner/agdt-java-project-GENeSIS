package uk.ac.leeds.ccg.andyt.projects.genesis.resource;

//import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.Location;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

public class Food extends AbstractResource {

    public Food(
            Vector_Point2D _Point2D,
            double _Value,
            double _Weight) {
        this._Point2D = _Point2D;
        this._Value = _Value;
        this._Weight = _Weight;
        this._ID = new Object();
    }
}
