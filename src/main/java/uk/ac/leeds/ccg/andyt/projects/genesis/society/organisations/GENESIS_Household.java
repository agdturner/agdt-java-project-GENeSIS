/**
 * A component of a library for <a
 * href="http://www.geog.leeds.ac.uk/people/a.turner/src/andyt/java/projects/GENESIS/">GENESIS</a>
 * Copyright (C) 2008 <a
 * href="http://www.geog.leeds.ac.uk/people/a.turner/">Andy Turner</a>, <a
 * href="http://www.leeds.ac.uk/">University of Leeds</a>.
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
package uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations;

import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Person;
import java.util.HashSet;
import uk.ac.leeds.ccg.andyt.vector.geometry.Vector_Point2D;

public class GENESIS_Household extends GENESIS_Organisation {

    /**
     * Stores the current location of the GENESIS_Household
     */
    public Vector_Point2D _Point2D;
    /**
     * Stores the Resources of the household
     */
    double _Resource;

    public GENESIS_Household() {
        this._People = new HashSet();
    }

    public GENESIS_Household(HashSet _People) {
        this._People = _People;
    }

    public GENESIS_Household(Vector_Point2D _Point2D) {
        this._People = new HashSet();
        this._Places = new HashSet();
        this._Point2D = _Point2D;
        //this._Places.add(_Place_Location);
        //_Init_Resource();
    }

    public GENESIS_Household(
            HashSet _People,
            Vector_Point2D _Point2D) {
        this._People = _People;
        this._Places = new HashSet();
        this._Point2D = _Point2D;
    }

    public void _Init_Resource() {
        _Resource = 100.0d;
    }

    public void _Add_Person(GENESIS_Person _Person) {
        _People.add(_Person);
    }

    public void _Remove_Person(GENESIS_Person _Person) {
        _People.remove(_Person);
    }

    /**
     * @return description of this.
     */
    @Override
    public String toString() {
        String _String = "Household: ";
        _String += "_People.size() " + _People.size();
        return _String;
    }
}
