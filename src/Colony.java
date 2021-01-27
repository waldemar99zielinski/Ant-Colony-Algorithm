import sndlib.core.network.Network;

import java.util.ArrayList;

public class Colony {

    private NetworkAPI network;
    private ArrayList<Ant> ants;
    private int numberOfAnts;
    private String source;
    private String destination;

    public Colony(NetworkAPI network, String source, String destination) {
        this.network = network;
        this.source = source;
        this.destination = destination;

        for(int i = 0;i<numberOfAnts; i++){
            ants.add(new Ant(source));
        }

    }

}
