import sndlib.core.network.Link;

import java.util.ArrayList;
import java.util.Random;

public class Ant {
    private String previousNode;
    private String currentNode;
    private String nextNode;
    private double currentTravelCost;
    private String destinationNode;


    private boolean isTravelling;

    private ArrayList<String> trail;
    private double trailCost;

    public String getNextNode() {
        return nextNode;
    }

    public void setNextNode(String nextNode) {
        this.nextNode = nextNode;
    }

    public double getCurrentTravelCost() {
        return currentTravelCost;
    }

    public void setCurrentTravelCost(double currentTravelCost) {
        this.currentTravelCost = currentTravelCost;
    }

    public boolean isTravelling() {
        return isTravelling;
    }

    public void setTravelling(boolean travelling) {
        isTravelling = travelling;
    }

    public double getTrailCost() {
        return trailCost;
    }

    public void setTrailCost(double trailCost) {
        this.trailCost = trailCost;
    }

    private final double alpha;
    private final double beta;

    private Random random;

    public Ant(String initNode, String destinationNode, double alpha, double beta){
        this.previousNode = null;
        this.currentNode = initNode;
        this.nextNode = null;

        this.trail = new ArrayList<>();
        this.trailCost = 0;
        this.destinationNode = destinationNode;
        this.isTravelling = true;

        this.alpha = alpha;
        this.beta = beta;

        this.random = new Random();
    }

    public String getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(String currentNode) {
        this.currentNode = currentNode;
    }

    public void selectLink(ArrayList<Link> possibleLinks){
        if(this.isTravelling){
            ArrayList<Double> probabilities = getSelectionProbabilities(possibleLinks);
            //get prob
            double chosenProb = random.nextDouble();
            double totalProb = 0.0;

            for(int i = 0;i < possibleLinks.size();i++){
                totalProb += probabilities.get(i);

                if(totalProb>= chosenProb){
                    nextNode = possibleLinks.get(i).getSecondNode().getId();
                    currentTravelCost = possibleLinks.get(i).getSetupCost();
                }
            }
        }

    }
    private ArrayList<Double> getSelectionProbabilities(ArrayList<Link> possibleLinks){
        double sum = 0.0;
        ArrayList<Double> probabilities = new ArrayList<>();
        for (Link link: possibleLinks) {
            sum += Math.pow(link.getRoutingCost(), alpha) * Math.pow(link.getSetupCost(), beta);
        }

        for (Link link: possibleLinks) {
            //prevent going backwards
            if(link.getSecondNode().getId().equals(this.previousNode)){
                probabilities.add(0.0);
            }else{
                double linkProbability = Math.pow(link.getRoutingCost(), alpha) * Math.pow(link.getSetupCost(), beta);
                probabilities.add(linkProbability/sum);
            }
        }
        return probabilities;
    }
    public void travel(){
        if(this.isTravelling){
            trail.add(nextNode);
            trailCost += currentTravelCost;
            currentTravelCost = 0;

            this.previousNode = this.currentNode;
            this.currentNode = this.nextNode;
            this.nextNode = null;
            if(currentNode.equals(destinationNode)){
                this.isTravelling = false;
            }
        }

    }

}
