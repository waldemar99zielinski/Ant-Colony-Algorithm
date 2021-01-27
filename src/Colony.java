import sndlib.core.network.Link;
import sndlib.core.network.Network;

import java.util.ArrayList;

public class Colony {

    private final NetworkAPI network;
    private ArrayList<Ant> ants;
    private int numberOfAnts;
    private final String source;
    private final String destination;

    private int numberOfSolution = 0;

    //algorithm parameters
    private double alpha;
    private double beta;
    private double evaporationRate = 0.5;
    private double Q = 1;

    private ArrayList<String> bestTrail;
    private double bestSolution;

    public Colony(NetworkAPI network, int numberOfAnts,String source, String destination, double alpha, double beta) {
        this.network = network;
        this.source = source;
        this.destination = destination;
        this.alpha = alpha;
        this.beta = beta;
        this.numberOfAnts = numberOfAnts;
        this.ants = new ArrayList<>();
        this.bestSolution = 1000000000;

        for(int i = 0;i<numberOfAnts; i++){
            ants.add(new Ant(source, destination, alpha, beta));
        }


    }
    private void makeAntsSelectNextNode(){
        for(Ant ant: ants){
            if(ant.isTravelling()){
                String currentNode = ant.getCurrentNode();
                ant.selectLink(network.getNodeLinks(currentNode));
            }else{
                this.numberOfSolution++;
            }

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
    private void makeAntsTravel(){
        for(Ant ant: ants){
          ant.travel();
        }
    }
    private void updateSolution(){

        for(Ant ant: ants){
            if(!ant.isTravelling() && bestSolution>ant.getTrailCost()){
                bestSolution = ant.getTrailCost();
                bestTrail = ant.getTrail();
                //ants.remove(ant);
            }

        }
    }

    public void run(){
        while(this.numberOfSolution<1){
            makeAntsSelectNextNode();
            pheromoneUpdate();
            makeAntsTravel();
            updateSolution();
        }
        System.out.println(this.bestSolution);
        System.out.println(this.bestTrail);
    }


}
