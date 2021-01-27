import sndlib.core.network.Link;

import java.util.ArrayList;

public class Ant {

    private String currentNode;
    private String nextNode;

    private ArrayList<Link> trail;
    private int trailLength;

    public Ant(String initNode){
        this.currentNode = initNode;
        this.nextNode = null;
        this.trail = new ArrayList<>();
        this.trailLength = 0;
    }

    public String getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(String currentNode) {
        this.currentNode = currentNode;
    }


}
