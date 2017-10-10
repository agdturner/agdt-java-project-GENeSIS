package uk.ac.leeds.ccg.andyt.projects.genesis.society.organisations;

import java.io.Serializable;
import java.util.HashSet;

public abstract class Organisation
        implements Serializable {

    public HashSet _People;
    public HashSet _Places;
}
