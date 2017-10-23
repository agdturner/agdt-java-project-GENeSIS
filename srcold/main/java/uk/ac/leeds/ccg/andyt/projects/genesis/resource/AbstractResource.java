package uk.ac.leeds.ccg.andyt.projects.genesis.resource;

//import uk.ac.leeds.ccg.andyt.projects.genesis.utilities.Location;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

public class AbstractResource {

    public Vector_Point2D _Point2D;
    public double _Value;
    public double _Weight;
    public Object _ID;

    @Override
    public boolean equals(Object o) {
        if (o instanceof AbstractResource) {
            return ((AbstractResource) o)._ID.equals(_ID);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this._ID != null ? this._ID.hashCode() : 0);
        return hash;
    }
}
