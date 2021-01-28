import com.sun.javafx.geom.Edge;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class CityLink {

    private static final double EXTINCTION_VALUE = 5;
    private static final double USAGE_VALUE = 20;

    private String identifier;
    private City src;
    private City dest;
    private Line edge = new Line();
    private double pheromoneValue = 0;
    private Text text = new Text();

    public CityLink(String identifier, City src, City dest) {
        //System.out.println("[CL] Got link from " + src.getX() + "x" + src.getY() + " to " + dest.getX() + "x" + dest.getY());
        this.identifier = identifier;
        this.src = src;
        this.dest = dest;
        setupEdge();
    }

    private void setupEdge() {
        edge.setStartX(src.getX());
        edge.setStartY(src.getY());
        edge.setEndX(dest.getX());
        edge.setEndY(dest.getY());
        edge.setStroke(Color.rgb(0,0,0));
        edge.setStrokeWidth(3);

        text.setX((edge.getStartX() + 10 + edge.getEndX())/2);
        text.setY((edge.getStartY() + edge.getEndY())/2);
        text.setText(String.valueOf(pheromoneValue));
        text.setFill(Color.BLACK);
        System.out.println("[EDGE] Pheromone value: " + pheromoneValue);
//        text.set
    }


    public City getSrc() { return src; }
    public City getDest() { return dest; }
    public Line getEdge() { return edge; }
    public String getIdentifier() { return identifier; }

    public void setStroke(double factor) {
        String currentColor = edge.getStroke().toString();

        int r = Integer.valueOf( currentColor.substring(2,4), 16);
        int g = Integer.valueOf( currentColor.substring(4,6), 16);
        int b = Integer.valueOf( currentColor.substring(6,8), 16);

        double temp_r  = r * factor * 100;
    }


    public void edgeUpdate(boolean isUsed) {
        String currentColor = edge.getStroke().toString();

        int r = Integer.valueOf( currentColor.substring(2,4), 16);
        int g = Integer.valueOf( currentColor.substring(4,6), 16);
        int b = Integer.valueOf( currentColor.substring(6,8), 16);

        if (isUsed)
            r += USAGE_VALUE;
        else
            r -= EXTINCTION_VALUE;

        if (r > 255)
            r = 255;
        else if (r < 0)
            r = 0;

        edge.setStroke(Color.rgb(r,g,b));

    }

    public void edgeUpdate(double value) {
        String currentColor = edge.getStroke().toString();
        pheromoneValue = value;
        String temp = String.valueOf(pheromoneValue);
        if (temp.length() > 4)
            this.text.setText(temp.substring(0,4));
        else
            this.text.setText(temp);

        //System.out.println("[EDGEUPDATE] Pheromone value: " + text.getText());
        int r = Integer.valueOf( currentColor.substring(2,4), 16);
        int g = Integer.valueOf( currentColor.substring(4,6), 16);
        int b = Integer.valueOf( currentColor.substring(6,8), 16);

        r = (int) Math.round(value * 255);
//        Integer.

        if (r > 255)
            r = 254;
        else if (r < 0)
            r = 0;

        //if (dest.getName().equals("Miami") || dest.getName().equals("Tampa"))
            //System.out.println("Edge from " + src.getName() + "to " + dest.getName() + " with r value: " + r + "factor: " + value);
        //System.out.println("New r value: " + r);
        edge.setStroke(Color.rgb(r,g,b));
        edge.setFill(Color.rgb(r,g,b));
    }

    public Text getPheromoneValue() {
        //++T+ext text = new Text((edge.getStartX() + edge.getEndX())/2 , (edge.getStartY() + edge.getEndY())/2, String.valueOf(pheromoneValue));
        return text;
    }
}
