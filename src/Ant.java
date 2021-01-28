import sndlib.core.network.Link;

import java.util.ArrayList;
import java.util.Random;

public class Ant {
    private String previousNode;
    private String currentNode;
    private String nextNode;
    private double currentTravelCost;

    private String sourceNode;
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
    private int id;
    public Ant(int id, String initNode, String destinationNode, double alpha, double beta){
        this.id = id;
        this.previousNode = null;
        this.currentNode = initNode;
        this.nextNode = null;

        this.trail = new ArrayList<>();
        this.trail.add(initNode);
        this.trailCost = 0;

        this.sourceNode = initNode;
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

    public ArrayList<String> getTrail() {
        return trail;
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
                    this.nextNode = possibleLinks.get(i).getSecondNode().getId();
                    this.currentTravelCost = possibleLinks.get(i).getSetupCost();
                    break;
                }
            }
        }

    }
    private ArrayList<Double> getSelectionProbabilities(ArrayList<Link> possibleLinks){
        double sum = 0.0;
        ArrayList<Double> probabilities = new ArrayList<>();
        for (Link link: possibleLinks) {
            if(!link.getSecondNode().getId().equals(this.previousNode)){
                sum += Math.pow(link.getRoutingCost(), alpha) * Math.pow(link.getSetupCost(), beta);
            }

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
            this.trail.add(nextNode);
            this.trailCost += this.currentTravelCost;
            this.currentTravelCost = 0;

            this.previousNode = this.currentNode;
            this.currentNode = this.nextNode;
            this.nextNode = null;

            //System.out.println("["+id+"] @" + currentNode);

            if(currentNode.equals(destinationNode)){
                this.isTravelling = false;
            }


        }

    }
    public void updateSolutionAndReturn(){
        if(!this.isTravelling()){
            //ant reached destination, return to the source
           String tmp = this.destinationNode;
           this.destinationNode = this.sourceNode;
           this.sourceNode = tmp;
//            this.currentNode = this.sourceNode;

            //clear cost and paht
            this.trail.clear();
            this.trailCost = 0.0;
            this.trail.add(this.currentNode);
            this.previousNode = "";
            this.isTravelling = true;
        }
    }
    public void print(){
        System.out.println("ANT");
        // System.out.println("Trail: "+trail);
        System.out.println("Cost: "+trailCost);
        System.out.println();
    }

}