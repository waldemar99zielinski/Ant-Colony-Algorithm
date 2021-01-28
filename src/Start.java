
import sndlib.core.io.*;
import sndlib.core.model.Model;
import sndlib.core.network.CapacityModule;
import sndlib.core.network.Network;

import java.io.*;


public class Start {

    public static void main(String[] args) throws IOException {





        //Routing cost = pheromone
        //Setup cost  = cost
        NetworkAPI networkAPI = new NetworkAPI("/home/waldemar/IdeaProjects/mrk/src/networkFiles/network.txt","./networkFiles/D-D-M-N-C-A-N-N.txt");

        //System.out.println(networkAPI.getNodeLinks("Dallas").get(0).getId());
        //var a = networkAPI.getNodeLinks("Dallas");
       // a.get(0).setSetupCost(12.0);

        //networkAPI.setLinkPheromone("Dallas", "Denver", 1.25);
        //networkAPI.setupNetwork();
      Colony c = new Colony(networkAPI, 100,"SaltLakeCity", "Chicago", 5, 1);
        c.run();
        networkAPI.saveNetwork();
    }
}
