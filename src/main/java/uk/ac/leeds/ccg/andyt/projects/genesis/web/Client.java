/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.leeds.ccg.andyt.projects.genesis.web;

/**
 *
 * @author geoagdt
 */
public class Client {

    public Client() {
    }

    public static void main(String[] args) {
        new Client().run();
    }

    public void run() {
        try { // Call Web Service Operation
            //uk.ac.leeds.ccg.andyt.projects.neiss.client.GenesisModelService service = new uk.ac.leeds.ccg.andyt.projects.neiss.client.GenesisModelService();
            //uk.ac.leeds.ccg.andyt.projects.neiss.client.GenesisModel port = service.getGenesisModelPort();
            // TODO initialize WS operation arguments here
            java.lang.String baseRunID = "";
            java.lang.String directory = "";
            java.lang.String randomSeed = "";
            java.lang.String years = "";
            java.lang.String maximumNumberOfAgents = "";
            java.lang.String maximumNumberOfAgentsPerAgentCollection = "";
            java.lang.String maximumNumberOfObjectsPerDirectory = "";
            // TODO process result here
            //java.lang.String result = port.run(baseRunID, directory, randomSeed, years, maximumNumberOfAgents, maximumNumberOfAgentsPerAgentCollection, maximumNumberOfObjectsPerDirectory);
            //System.out.println("Result = "+result);
        } catch (Exception ex) {
            // TODO handle custom exceptions here
        }

    }
}
