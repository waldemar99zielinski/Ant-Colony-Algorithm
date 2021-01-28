import sndlib.core.network.Link;
import sndlib.core.network.Network;

import java.util.ArrayList;
import java.util.Arrays;

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
    private double Q = 10;

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
            if(!ant.isTravelling()){
                double generatedPheromone = Q/(ant.getTrailCost());
                ArrayList<String> sol = ant.getTrail();
                for(int i=0;i<sol.size() - 2;i++){
                    network.setLinkPheromone(sol.get(i),sol.get(i+1), generatedPheromone);
                }

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
                this.bestSolution = ant.getTrailCost();
                this.bestTrail = (ArrayList<String>) ant.getTrail().clone();

            }
            if(!ant.isTravelling()){
                ant.updateSolutionAndReturn();
            }

        }
    }

    public void run(){
        int i = 0;
        while(i<100){
            makeAntsSelectNextNode();
            makeAntsTravel();
            pheromoneUpdate();
            updateSolution();
            i++;
            //System.out.println(i);
        }
        System.out.println(this.bestSolution);
        System.out.println(this.bestTrail);
        for (Ant ant:ants) {
            ant.print();
        }
    }


}
