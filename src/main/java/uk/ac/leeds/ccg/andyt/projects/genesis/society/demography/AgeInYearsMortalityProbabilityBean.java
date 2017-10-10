package uk.ac.leeds.ccg.andyt.projects.genesis.society.demography;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ageInYearsMortalityProbability", propOrder = {"ageInYears", "mortalityProbability"})
@Deprecated
public class AgeInYearsMortalityProbabilityBean {

    static final long serialVersionUID = 1L;
    int ageInYears;
    BigDecimal mortalityProbability;

    public AgeInYearsMortalityProbabilityBean(
            int ageInYears,
            BigDecimal mortalityProbability) {
        this.ageInYears = ageInYears;
        this.mortalityProbability = new BigDecimal(mortalityProbability.toString());
        //this.mortalityProbability = mortalityProbability;
    }

    public AgeInYearsMortalityProbabilityBean() {
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
    public BigDecimal getMortalityProbability() {
        return new BigDecimal(mortalityProbability.toString());
        //return mortalityProbability;
    }

    public void setMortalityProbability(BigDecimal mortalityProbability) {
        this.mortalityProbability = new BigDecimal(mortalityProbability.toString());
        //this.mortalityProbability = mortalityProbability;
    }
}
