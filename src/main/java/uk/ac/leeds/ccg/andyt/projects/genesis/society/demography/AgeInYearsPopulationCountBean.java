//package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;
package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ageInYearsPopulationCount", propOrder = {"ageInYears", "populationCount"})
@Deprecated
public class AgeInYearsPopulationCountBean {

    static final long serialVersionUID = 1L;
    int ageInYears;
    BigInteger populationCount;

    public AgeInYearsPopulationCountBean(
            int ageInYears,
            BigInteger populationCount) {
        this.ageInYears = ageInYears;
        this.populationCount = populationCount;
    }

    public AgeInYearsPopulationCountBean() {
        super();
    }

    @XmlElement
    public int getAgeInYears() {
        return ageInYears;
    }

    public void setAgeInYears(int ageInYears) {
        this.ageInYears = ageInYears;
    }

    @XmlElement
    public BigInteger getPopulationCount() {
        return populationCount;
    }

    public void setPopulationCount(BigInteger populationCount) {
        this.populationCount = populationCount;
    }
}
