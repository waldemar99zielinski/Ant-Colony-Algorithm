import com.atesio.utils.CollectionUtils;
import sndlib.core.io.*;
import sndlib.core.model.Model;
import sndlib.core.network.Link;
import sndlib.core.network.Network;
import sndlib.core.network.Node;

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


    }
    public void saveNetwork(){
        SNDlibWriter writer = SNDlibIOFactory.newWriter(SNDlibIOFormat.NATIVE);

        try {
            Writer networkWriter =  new FileWriter("network.txt");
            Writer modelWriter = new FileWriter("model.txt");
            writer.writeNetwork(network, networkWriter);
            writer.writeModel(model, modelWriter);
            networkWriter.close();
            modelWriter.close();
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

        links.forEach(link -> link.setRoutingCost(value));
        System.out.println("[INFO] NetworkAPI: setLinkPheromone: between "+links.get(0).getFirstNode().getId()+" "+links.get(0).getSecondNode().getId() +" val="+value);
    }

}
