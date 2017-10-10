package uk.ac.leeds.ccg.andyt.projects.genesis.io.schema;

import java.math.BigDecimal;

import javax.xml.datatype.XMLGregorianCalendar;

import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.Age;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBound;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBoundPopulation;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBoundProbability;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.AgeBoundRate;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.GenderedAgeBoundPopulation;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.GenderedAgeBoundProbabilities;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.GenderedAgeBoundRates;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.ObjectFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.common.Time;

public class CommonFactory {

	private static ObjectFactory objectFactory;

	public static void init() {
		objectFactory = new ObjectFactory();
	}

	public static Age newAge() {
		if (objectFactory == null) {
			init();
		}
		return objectFactory.createAge();
	}

	public static Age newAge(Age a) {
		if (objectFactory == null) {
			init();
		}
		Age newAge = objectFactory.createAge();
		Long ageInYears = a.getAgeInYears();
		if (ageInYears != null) {
			newAge.setAgeInYears(ageInYears);
		}
		Time ageInYearsCalculationTime = a.getAgeInYearsCalculationTime();
		if (ageInYearsCalculationTime != null) {
			newAge.setAgeInYearsCalculationTime(ageInYearsCalculationTime);
		}
		XMLGregorianCalendar dateOfBirth = a.getDateOfBirth();
		if (dateOfBirth != null) {
			newAge.setDateOfBirth((XMLGregorianCalendar) dateOfBirth.clone());
		}
		Boolean dateOfBirthEstimated = a.isDateOfBirthEstimated();
		if (dateOfBirthEstimated != null) {
			newAge.setDateOfBirthEstimated(dateOfBirthEstimated);
		}
		Integer aDayOfMonthOfBirth = a.getDayOfMonthOfBirth();
		if (aDayOfMonthOfBirth != null) {
			newAge.setMonthOfYearOfBirth(new Integer(aDayOfMonthOfBirth));
		}
		Integer aMonthOfYearOfBirth = a.getMonthOfYearOfBirth();
		if (aMonthOfYearOfBirth != null) {
			newAge.setMonthOfYearOfBirth(new Integer(aMonthOfYearOfBirth));
		}
		Time timeOfBirth = a.getTimeOfBirth();
		if (timeOfBirth != null) {
			newAge.setTimeOfBirth(newTime(timeOfBirth));
		}
		AgeBound ageBound = a.getTimeOfBirthAgeBound();
		if (ageBound != null) {
			newAge.setTimeOfBirthAgeBound(newAgeBound(ageBound));
		}
		return newAge;
	}

	public static AgeBound newAgeBound() {
		if (objectFactory == null) {
			init();
		}
		return objectFactory.createAgeBound();
	}

	public static AgeBound newAgeBound(AgeBound a) {
		if (objectFactory == null) {
			init();
		}
		AgeBound ageBound = objectFactory.createAgeBound();
		if (a.getAgeMin() != null) {
			ageBound.setAgeMin(newTime(a.getAgeMin()));
		}
		if (a.getAgeMax() != null) {
			ageBound.setAgeMax(newTime(a.getAgeMax()));
		}
		return ageBound;
	}

	public static AgeBound newAgeBound(Time ageMin, Time ageMax) {
		if (objectFactory == null) {
			init();
		}
		AgeBound ageBound = objectFactory.createAgeBound();
		if (ageMin != null) {
			ageBound.setAgeMin(newTime(ageMin));
		}
		if (ageMax != null) {
			ageBound.setAgeMax(newTime(ageMax));
		}
		return ageBound;
	}
	public static Time newTime() {
		if (objectFactory == null) {
			init();
		}
		return objectFactory.createTime();
	}

	public static Time newTime(Time t) {
		if (objectFactory == null) {
			init();
		}
		Time time = objectFactory.createTime();
		time.setDayOfYear(new Integer(t.getDayOfYear()));
		time.setSecondOfDay(new Integer(t.getSecondOfDay()));
		time.setYear(t.getYear());
		return time;
	}

	public static Time newTime(
			long year, 
			Integer dayOfYear, 
			Integer secondOfDay) {
		if (objectFactory == null) {
			init();
		}
		Time time = objectFactory.createTime();
		time.setYear(year);
		time.setDayOfYear(new Integer(dayOfYear.toString()));
		time.setSecondOfDay(new Integer(secondOfDay.toString()));
		return time;
	}
	
	public static Time newTime(
			long year) {
		if (objectFactory == null) {
			init();
		}
		Time time = objectFactory.createTime();
		time.setYear(year);
		time.setDayOfYear(0);
		time.setSecondOfDay(0);
		return time;
	}
	public static AgeBoundProbability newAgeBoundProbability() {
		if (objectFactory == null) {
			init();
		}
		return objectFactory.createAgeBoundProbability();
	}

	public static AgeBoundProbability newAgeBoundProbability(
			AgeBoundProbability a) {
		if (objectFactory == null) {
			init();
		}
		AgeBoundProbability ageBoundProbability = objectFactory
				.createAgeBoundProbability();
		ageBoundProbability.setAgeBound(newAgeBound(a.getAgeBound()));
		ageBoundProbability.setProbability(new BigDecimal(a.getProbability()
				.toString()));
		return ageBoundProbability;
	}

	public static GenderedAgeBoundProbabilities newGenderedAgeBoundProbabilities() {
		if (objectFactory == null) {
			init();
		}
		return objectFactory.createGenderedAgeBoundProbabilities();
	}

	public static AgeBoundPopulation newAgeBoundPopulation() {
		if (objectFactory == null) {
			init();
		}
		return objectFactory.createAgeBoundPopulation();
	}

	public static AgeBoundPopulation newAgeBoundPopulation(AgeBoundPopulation a) {
		if (objectFactory == null) {
			init();
		}
		AgeBoundPopulation ageBoundPopulation = objectFactory
				.createAgeBoundPopulation();
		ageBoundPopulation.setAgeBound(newAgeBound(a.getAgeBound()));
		ageBoundPopulation.setPopulation(new BigDecimal(a.getPopulation()
				.toString()));
		return ageBoundPopulation;
	}

	public static GenderedAgeBoundPopulation newGenderedAgeBoundPopulation() {
		if (objectFactory == null) {
			init();
		}
		GenderedAgeBoundPopulation result = objectFactory
				.createGenderedAgeBoundPopulation();
		// ArrayList<AgePopulation> females = new ArrayList<AgePopulation>();
		return result;
	}

	public static AgeBoundRate newAgeBoundRate() {
		if (objectFactory == null) {
			init();
		}
		return objectFactory.createAgeBoundRate();
	}

	public static AgeBoundRate newAgeBoundRate(AgeBoundRate a) {
		if (objectFactory == null) {
			init();
		}
		AgeBoundRate ageBoundRate = objectFactory.createAgeBoundRate();
		ageBoundRate.setAgeBound(newAgeBound(a.getAgeBound()));
		ageBoundRate.setRate(new BigDecimal(a.getRate().toString()));
		return ageBoundRate;
	}

	public static GenderedAgeBoundRates newGenderedAgeBoundRates() {
		if (objectFactory == null) {
			init();
		}
		return objectFactory.createGenderedAgeBoundRates();
	}
}
