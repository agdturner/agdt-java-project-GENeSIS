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

import java.io.Serializable;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Environment;
import uk.ac.leeds.ccg.andyt.projects.genesis.core.GENESIS_Person;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @TODO Family to extend Agent? A family is individual to a person. It is made
 * up of indivuals and collections of people with special roles or relationships
 * to the individual. A family includes parents. These may also be guardians,
 * but guardians are represented separately and these may change, but parents do
 * not. There may be siblings who can share all, one or no parents or guardians.
 * There may also be children who reciprocate the parent relationship. Often
 * these are dependents, although dependents are represented separately. (Every
 * guardian does not necessarily have the same set of dependents.) Next of kin
 * relationships for the individual is stored here. Spouse relationship is also
 * stored here (this is reciprocal).
 *
 * Family history is stored with dates of changes in the relationships.
 *
 * Every person has a family even if it is not defined.
 */
public class Family
        implements Serializable {

    public transient GENESIS_Environment _GENESIS_Environment;
    protected Long _Mother_Agent_ID;
    //private Long _Father_Agent_ID;
    //private HashSet<Long> _Guardians;
    //private HashSet<Long> _Siblings;
    //private HashSet<Long> _Dependents;
    protected HashSet<Long> _Children_Agent_ID_HashSet;
    //private Long _NextOfKin;
    //private Long _Spouse;

    /**
     * _Order = 0 means this is not completely initialised _Order = 1 means this
     * is initialised such that all relevant members have been added. _Order = 2
     * means all Family members have been initialised to order 1 _Order = 3
     * means there is a full and complete family history
     */
    //private int _Order;
    /**
     * A record of the date and changes in Family for _Person. Inlcuding;
     * Births, deaths, marriages, changes of dependent/guardian status.
     * @param _GENESIS_Environment
     */
    //private Object _FamilyHistory;
    public Family(GENESIS_Environment _GENESIS_Environment) {
        this._GENESIS_Environment = _GENESIS_Environment;
    }

    public Family(GENESIS_Person a_Person) {
        this._GENESIS_Environment = a_Person._GENESIS_Environment;
    }

    /**
     * Children are not necessarily dependents, but are biological/genetic
     * inheritors. A child should be added to a parent. It is possible through
     * genetic engineering that a person has only one parent or has more than
     * two parents. It is usual that a person has two parents, a male and a
     * female. One case of three parents can be a doner egg used in fertility
     * treatment. A child is not to be removed from a family. So when a child
     * becomes adult or non-dependent they are still a child.
     * @param a_Agent_ID
     */
    public void add_Child(Long a_Agent_ID) {
        if (get_Children_Agent_ID_HashSet() == null) {
            _Children_Agent_ID_HashSet = new HashSet<Long>();
        }
        _Children_Agent_ID_HashSet.add(a_Agent_ID);
    }

//    /**
//     * Once a sibling, always a sibling. A sibling is not strictly biological/
//     * genetic in that siblings do not have to share a common parent. To
//     * identify those that do then the parents of siblings should be tested for
//     * equality.
//     *
//     * All siblings are common, however it might be desirable to distinguish
//     * the steps of siblings.
//     *
//     * Should sibling divorce be allowed?
//     *
//     * Siblings that do not share a common parent can legally have children.
//     *
//     * Legality is not well defined yet.
//     *
//     * After a family is initilised, a sibling may be added during simulation
//     * when there is a birth, or a marriage/cohabitation.
//     *
//     * @param _Sibling
//     */
//    public void add_Sibling(
//            GENESIS_Person _Sibling) {
//        if (get_Siblings() == null) {
//            if (_Sibling._Family.get_Siblings() == null){
//                set_Siblings(new HashSet());
//                get_Siblings().add(_Sibling);
//                get_Siblings().add(_Person);
//                get_People().add(_Sibling);
//                _Sibling._Family.set_Siblings(get_Siblings());
//                _Sibling._Family.get_People().add(_Person);
//            } else {
//                set_Siblings(_Sibling._Family.get_Siblings());
//                Iterator _Iterator = get_Siblings().iterator();
//                while(_Iterator.hasNext()){
//                    GENESIS_Person _A_Sibling = (GENESIS_Person) _Iterator.next();
//                    _A_Sibling._Family.get_People().add(_Person);
//                }
//                get_Siblings().add(_Person);
//            }
//        } else {
//            if (_Sibling._Family.get_Siblings() == null){
//                get_Siblings().add(_Sibling);
//                get_People().add(_Sibling);
//                _Sibling._Family.set_Siblings(get_Siblings());
//                _Sibling._Family.get_People().addAll(get_Siblings());
//            } else {
//                Iterator _Iterator = get_Siblings().iterator();
//                while(_Iterator.hasNext()){
//                    GENESIS_Person _A_Sibling = (GENESIS_Person) _Iterator.next();
//                    _A_Sibling._Family.get_People().addAll(_Person._Family.get_Siblings());
//                }
//                get_Siblings().addAll(_Sibling._Family.get_Siblings());
//                get_People().addAll(_Sibling._Family.get_Siblings());
//                _Iterator = _Sibling._Family.get_Siblings().iterator();
//                while(_Iterator.hasNext()){
//                    GENESIS_Person _A_Sibling = (GENESIS_Person) _Iterator.next();
//                    _A_Sibling._Family.set_Siblings(_Person._Family.get_Siblings());
//                }
//            }
//        }
//    }
//
//    /**
//     * After an individuals family is initilised, a guardian may be added during
//     * simulation. This is usually when there is a marriage/cohabitation change.
//     * Cohabitation is not necessary for a guardian relationship, but it is
//     * perhaps usual.
//     * @param _Guardian
//     */
//    public void add_Guardian(
//            GENESIS_Person _Guardian){
//        if (get_Guardians() == null ){
//            set_Guardians(new HashSet());
//        }
//        get_Guardians().add(_Guardian);
//        get_People().add(_Guardian);
//        _Guardian._Family._Add_Dependent(_Person);
//    }
//
//    /**
//     * A guardian may be removed during simulation. Removing a guardian
//     * does not remove the guardian from _People.
//     * @param _Guardian
//     */
//    public void remove_Guardian(
//            GENESIS_Person _Guardian) {
//        get_Guardians().remove(_Guardian);
//        if (get_Guardians().size() == 0){
//            set_Guardians(null);
//        }
//        _Guardian._Family._Remove_Dependent(_Person);
//    }
//
//    /**
//     * Dependents may be old or young related genetically or not. This is a
//     * reciprocal relationship to guardianship. If somone is a persons guardian,
//     * then that person is a dependent of the guardian. The dependency is
//     * established via the _Add_Guardian(GENESIS_Person,boolean) method.
//     * @param _Dependent
//     */
//    private void add_Dependent(GENESIS_Person _Dependent) {
//        if (get_Dependents() == null) {
//            set_Dependents(new HashSet());
//        }
//        get_Dependents().add(_Dependent);
//        get_People().add(_Dependent);
//    }
//
////    public void _Add_Dependents(HashSet _Dependents) {
////        this._Dependents.addAll(_Dependents);
////        _People.addAll(_Dependents);
////    }
//
//    /**
//     * Dependents may be removed during simulation when there is a divorce/
//     * decohabitation. The dependency is removed via the
//     * _Remove_Guardian(GENESIS_Person,boolean) method.
//     * @param _Dependent
//     */
//    private void remove_Dependent(GENESIS_Person _Dependent) {
//        get_Dependents().remove(_Dependent);
//        if (get_Dependents().size() == 0) {
//            set_Dependents(null);
//        }
//    }
//
//    /**
//     * @param _Spouse
//     */
//    public void add_Spouse(
//            GENESIS_Person _Spouse){
//        if (_Spouse != null ){
//            System.out.println("_Family._Spouse exists and is being overwritten for GENESIS_Person.ID " + _Person.get_Agent_ID(_GENESIS_Environment._HandleOutOfMemoryError));
//        }
//        this.set_Spouse(_Spouse);
//        get_People().add(_Spouse);
//        _Spouse._Family.set_Spouse(_Person);
//        _Spouse._Family.get_People().add(_Person);
//    }
//
//    /**
//     * This is like divorce, but that could be more complicated?
//     * @param _Spouse
//     */
//    public void remove_Spouse(
//            GENESIS_Person _Spouse) {
//        _Spouse = null;
//        _Spouse._Family.set_Spouse(null);
//    }
    /**
     * @return description of this.
     */
    @Override
    public String toString() {
        String result = "_Mother_Agent_ID "
                + this.get_Mother_Agent_ID()
                + ", Number of Children ";
        if (_Children_Agent_ID_HashSet == null) {
            result += " 0";
        } else {
            result += "" + _Children_Agent_ID_HashSet.size() + "";
            // Might want to provide detials of children too...
        }
        //_String += "; _People.size() " + get_People().size();
///        _String += "; _Parents.size() " + get_Mother_Agent_ID();
//        if (get_Siblings() == null ){
//            _String += "; Siblings undefined or non existant";
//        } else {
//            _String += "; _Siblings.size() " + get_Siblings().size();
//        }
//        if (get_Guardians() == null ){
//            _String += "; Guardians undefined or non existant";
//        } else {
//            _String += "; _Guardians.size() " + get_Guardians().size();
//        }
//        if (get_Dependents() == null ){
//            _String += "; Dependents undefined or non existant";
//        } else {
//            _String += "; _Dependents.size() " + get_Dependents().size();
//        }
//        if (get_Children() == null ){
//            _String += "; _Children undefined or non existant";
//        } else {
//            _String += "; _Children.size() " + get_Children().size();
//        }
//        if (get_Spouse() == null){
//            _String += "; _Spouse undefined or non existant";
//        } else {
//            _String += "; _Spouse._ID " +  get_Spouse().get_Agent_ID(_GENESIS_Environment._HandleOutOfMemoryError);
//        }
//        if (get_NextOfKin() == null){
//            _String += "; _NextOfKin undefined or non existant";
//        } else {
//            _String += "; _NextOfKin._ID " +  get_NextOfKin().get_Agent_ID(_GENESIS_Environment._HandleOutOfMemoryError);
//        }
        return result;
    }

    /**
     * Mother is the female primary biological/genetic provider. (Complex
     * situation where there is more than one are not considered)
     * @param a_AgentID
     */
    public void set_Mother(Long a_AgentID) {
        this._Mother_Agent_ID = a_AgentID;
    }

//    /**
//     * Father is the male primary biological/genetic provider.
//     * (Complex situation where there is more than one are not considered)
//     */
//    public void set_Father(Long a_AgentID) {
//        this._Father_Agent_ID = a_AgentID;
//    }
    public Long get_Mother_Agent_ID() {
        return this._Mother_Agent_ID;
    }

//    public Long get_Father_Agent_ID() {
//        return this._Father_Agent_ID;
//    }
    public HashSet<Long> get_Children_Agent_ID_HashSet() {
        return _Children_Agent_ID_HashSet;
    }
//    public HashSet get_Guardians() {
//        return _Guardians;
//    }
//    public HashSet getSiblings() {
//        return _Siblings;
//    }
//
//    public void set_Siblings(HashSet _Siblings) {
//        this._Siblings = _Siblings;
//    }
//
//    public HashSet get_Dependents() {
//        return _Dependents;
//    }
//
//    public void set_Dependents(HashSet _Dependents) {
//        this._Dependents = _Dependents;
//    }
//
//    public void set_Children(HashSet _Children) {
//        this._Children = _Children;
//    }
//
//    public GENESIS_Person get_NextOfKin() {
//        return _NextOfKin;
//    }
//
//    public void set_NextOfKin(GENESIS_Person _NextOfKin) {
//        this._NextOfKin = _NextOfKin;
//    }
//
//    public GENESIS_Person get_Spouse() {
//        return _Spouse;
//    }
//
//    public void set_Spouse(GENESIS_Person _Spouse) {
//        this._Spouse = _Spouse;
//    }
//
//    public int get_Order() {
//        return _Order;
//    }
//
//    public void set_Order(int _Order) {
//        this._Order = _Order;
//    }
}
