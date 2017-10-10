package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ageInYearsFertilityProbability", propOrder = {"ageInYears", "fertilityProbability"})
@Deprecated
public class AgeInYearsFertilityProbabilityBean {

    static final long serialVersionUID = 1L;
    int ageInYears;
    BigDecimal fertilityProbability;

    public AgeInYearsFertilityProbabilityBean(
            int ageInYears,
            BigDecimal fertilityProbability) {
        this.ageInYears = ageInYears;
        this.fertilityProbability = new BigDecimal(fertilityProbability.toString());
        //this.fertilityProbability = fertilityProbability;
    }

    public AgeInYearsFertilityProbabilityBean() {
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
    public BigDecimal getFertilityProbability() {
        return new BigDecimal(fertilityProbability.toString());
        //return fertilityProbability;
    }

    public void setFertilityProbability(BigDecimal fertilityProbability) {
        this.fertilityProbability = new BigDecimal(fertilityProbability.toString());
        //this.fertilityProbability = fertilityProbability;
    }
}
