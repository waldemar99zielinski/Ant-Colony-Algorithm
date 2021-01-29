import com.atesio.utils.CollectionUtils;
import sndlib.core.io.*;
import sndlib.core.model.Model;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;

import javax.xml.parsers.SAXParser;
import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class NetworkAPI {
    public Network network;
    public Model model;


    public NetworkAPI(String networkPath, String modelPath) {
        Reader modelReader = null;
        Reader networkReader= null;
        try{
            modelReader = new FileReader(modelPath);
            networkReader = new FileReader(networkPath);
        }
        catch (IOException spx){
            System.err.println("[ERR] could not read network or model file: " + spx);
        }


        SNDlibParser parser = SNDlibIOFactory.newParser(SNDlibIOFormat.NATIVE);
        network = null;
        model = null;
        try {
            network = parser.parseNetwork(networkReader);
            model = parser.parseModel(modelReader);
        }
        catch(SNDlibParseException | IOException spx) {
            System.err.println("[ERR] could not parse network or model file: " + spx);
        }

        System.out.println("[INFO] NetworkAPI created");
    }
    public void setupNetwork(){
        for(Link link:network.links()){
            link.setSetupCost( link.getModule(1008.0).getCost() );
//            link.setSetupCost(1.0);
            link.setRoutingCost(1.0);

        }
        System.out.println("[INFO] NetworkAPI setup");
    }
    public void saveNetwork(){
        SNDlibWriter writer = SNDlibIOFactory.newWriter(SNDlibIOFormat.NATIVE);

        try {
            System.out.println("[INFO] Algorithm stopped. Saving network...");
            Writer networkWriter =  new FileWriter("network.txt");
            Writer modelWriter = new FileWriter("model.txt");
            writer.writeNetwork(network, networkWriter);
            writer.writeModel(model, modelWriter);
            networkWriter.close();
            modelWriter.close();
            System.out.println("[INFO] Network saved");
        }
        catch(SNDlibWriteException | IOException swx) {
            System.err.println("[ERR] could not write network or model file: " + swx);
        }
    }
    public Node getNode(String nodeName){
        return network.getNode(nodeName);
    }
    public ArrayList<Link> getNodeLinks(String nodeName){

        ArrayList<Link>  links = (ArrayList<Link>) network.links().stream()
                .filter(link -> link.getFirstNode().getId().equals(nodeName))
                .collect(Collectors.toList());


        return links;
    }
    public void setLinkPheromone(String sourceName, String directionName, double value){

        ArrayList<Link>  links = (ArrayList<Link>) network.links().stream()
                .filter(link -> (link.getFirstNode().getId().equals(sourceName) && link.getSecondNode().getId().equals(directionName))
                || (link.getFirstNode().getId().equals(directionName) && link.getSecondNode().getId().equals(sourceName)))
                .collect(Collectors.toList());

        if(links.isEmpty()){
            System.err.println("[ERR] NetworkAPI: setLinkPheromone: link doesn't exist. src="+sourceName+" dst="+directionName);
        }

        links.forEach(link -> {
            double currentPheromone = link.getRoutingCost();
            link.setRoutingCost(currentPheromone + value);
        });
        //System.out.println("[INFO] NetworkAPI: setLinkPheromone: between "+links.get(0).getFirstNode().getId()+" "+links.get(0).getSecondNode().getId() +" newval="+value);
    }


    public void evaporatePheromone(double evaporationRate){
        if(evaporationRate>=1.0 || evaporationRate<0.0){
            System.err.println("[ERR] Invalid evaporation rate: "+evaporationRate);
        }
        for(Link link:network.links()){
            double currentPheromone = link.getRoutingCost();
            //System.out.println(currentPheromone);

            if (currentPheromone * evaporationRate < 0.001)
                link.setRoutingCost(0.001);
            else
                link.setRoutingCost(currentPheromone * evaporationRate);

        }
    }

    public Network getNetwork() { return network; }

}
