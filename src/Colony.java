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
    private double evaporationRate = 0.9;
    private double Q = 10;

    private ArrayList<String> bestTrail = new ArrayList<>();
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
            ants.add(new Ant(i, source, destination, alpha, beta));
        }

        System.out.println("[INFO] Colony created");

    }
    public void makeAntsSelectNextNode(){
        for(Ant ant: ants){
            if(ant.isTravelling()){
                String currentNode = ant.getCurrentNode();
                ant.selectLink(network.getNodeLinks(currentNode));
            }else{
                this.numberOfSolution++;
            }

        }
    }
    public void pheromoneUpdate(){
        network.evaporatePheromone(this.evaporationRate);

        for(Ant ant: ants){
            if(!ant.isTravelling()){
                double generatedPheromone = Q/(ant.getTrailCost());
                ArrayList<String> sol = ant.getTrail();
                for(int i = 0 ;i < sol.size() - 1; i++){
                    network.setLinkPheromone(sol.get(i),sol.get(i + 1), generatedPheromone);
                }

            }
        }
    }
    public void makeAntsTravel(){
        for(Ant ant: ants){
            ant.travel();
        }
    }
    public void updateSolution(){

        for(Ant ant: ants){
            if(!ant.isTravelling() && bestSolution > ant.getTrailCost()){

                //if (bestSolution > ant.getTrailCost())
                    //printSolution();


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

    public int getNumberOfSolution() { return numberOfSolution; }

    public void printSolution() {
        System.out.println("------------------");
        System.out.println("[INFO] Cost of best solution ever found: " + bestSolution);
        for (String s : bestTrail)
            System.out.println(s);

    }
}