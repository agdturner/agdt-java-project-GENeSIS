package uk.ac.leeds.ccg.andyt.projects.genesis.io.schema;

import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;

import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.metadata.ObjectFactory;
import uk.ac.leeds.ccg.andyt.projects.genesis.io.schema.metadata.MetadataType;

public class MetadataFactory {

	private static ObjectFactory objectFactory;
	private static JAXBContext context;
	private static Marshaller marshaller;
	private static Unmarshaller unmarshaller;

	public static void init() {
		try {
			context = JAXBContext.newInstance(ObjectFactory.class);
			marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);
			marshaller.setProperty("jaxb.encoding", "UTF-8");
			unmarshaller = context.createUnmarshaller();
			objectFactory = new ObjectFactory();
		} catch (PropertyException e1) {
			e1.printStackTrace();
			System.exit(GENESIS_Schema_ErrorAndExceptionHandler.PropertyExceptionExitCode);
		} catch (JAXBException e) {
			e.printStackTrace();
			System.exit(GENESIS_Schema_ErrorAndExceptionHandler.JAXBExceptionExitCode);
		}
	}

	@SuppressWarnings("unchecked")
	public static MetadataType read(File xmlFile) throws JAXBException {
		if (objectFactory == null) {
			init();
		}
		JAXBElement<MetadataType> topLevel = null;
		try {
			topLevel = (JAXBElement<MetadataType>) unmarshaller
					.unmarshal(xmlFile);
		} catch (UnmarshalException e) {
			// This can happen because the xmlFile does not exist, or the system
			// has run out of file handles to allocate.
			if (!xmlFile.exists()) {
				e.printStackTrace(System.err);
				// null will be returned...
			} else {
				System.err.println("Trying to handle "
						+ e.getLocalizedMessage());
				System.err
						.println("Wait for 2 seconds then trying again to writeToCSV.");
				// Try waiting for 2 seconds and then repeating...
				try {
					synchronized (xmlFile) {
						xmlFile.wait(2000L);
					}
				} catch (InterruptedException ex) {
					Logger.getLogger(PopulationFactory.class.getName()).log(
							Level.SEVERE, null, ex);
				}
				return read(xmlFile);
			}
		} catch (JAXBException e) {
			// This can happen because the xmlFile does not exist, or the system
			// has run out of file handles to allocate.
			if (!xmlFile.exists()) {
				e.printStackTrace(System.err);
				// null will be returned...
			} else {
				System.err.println("Trying to handle "
						+ e.getLocalizedMessage());
				System.err
						.println("Wait for 2 seconds then trying again to writeToCSV.");
				// Try waiting for 2 seconds and then repeating...
				try {
					synchronized (xmlFile) {
						xmlFile.wait(2000L);
					}
				} catch (InterruptedException ex) {
					Logger.getLogger(PopulationFactory.class.getName()).log(
							Level.SEVERE, null, ex);
				}
				return read(xmlFile);
			}
		}
		return topLevel.getValue();
	}

	public static MetadataType newMetadataType() throws JAXBException {
		if (objectFactory == null) {
			init();
		}
		return objectFactory.createMetadataType();
	}

	public static void writeSystemOut(MetadataType metadata)
			throws JAXBException {
		if (objectFactory == null) {
			init();
		}
		JAXBElement<MetadataType> toplevel = objectFactory
				.createMetadata(metadata);
		marshaller.marshal(toplevel, System.out);
	}

	public static void writeFileOut(MetadataType metadata,
			FileOutputStream outStream) throws JAXBException {
		if (objectFactory == null) {
			init();
		}
		JAXBElement<MetadataType> toplevel = objectFactory
				.createMetadata(metadata);
		marshaller.marshal(toplevel, outStream);
	}
}
