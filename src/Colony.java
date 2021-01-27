import sndlib.core.network.Link;
import sndlib.core.network.Network;

import java.util.ArrayList;

public class Colony {

    private NetworkAPI network;
    private ArrayList<Ant> ants;
    private int numberOfAnts;
    private String source;
    private String destination;

    //algorithm parameters
    private double alpha;
    private double beta;
    private double evaporationRate;
    private double Q;

    private ArrayList<Link> bestTrail;
    private double bestSolution;

    public Colony(NetworkAPI network, String source, String destination, double alpha, double beta) {
        this.network = network;
        this.source = source;
        this.destination = destination;
        this.alpha = alpha;
        this.beta = beta;

        for(int i = 0;i<numberOfAnts; i++){
            ants.add(new Ant(source, destination, alpha, beta));
        }

    }
    private void makeAntsSelectNextNode(){
        for(Ant ant: ants){
            String currentNode = ant.getCurrentNode();
            ant.selectLink(network.getNodeLinks(currentNode));
        }
    }
    private void pheromoneUpdate(){
        network.evaporatePheromone(this.evaporationRate);

        for(Ant ant: ants){
            if(ant.isTravelling()){
                double generatedPheromone = Q/(ant.getCurrentTravelCost() + ant.getTrailCost());
                network.setLinkPheromone(ant.getCurrentNode(), ant.getNextNode(), generatedPheromone);
            }
        }
    }
    private void makeThoseAntsTravel(){
        for(Ant ant: ants){
          ant.travel();
        }
    }

}
