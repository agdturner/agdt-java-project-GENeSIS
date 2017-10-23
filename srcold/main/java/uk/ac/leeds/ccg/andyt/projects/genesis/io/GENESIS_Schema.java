package uk.ac.leeds.ccg.andyt.projects.genesis.io.schema;

public class GENESIS_Schema {
	
	public static void initAll() {
		FertilityFactory.init();
		MortalityFactory.init();
		PopulationFactory.init();
		MetadataFactory.init();
		ParametersFactory.init();
	}

}
