import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class CityLink {

    private City src;
    private City dest;
    private Line edge = new Line();

    public CityLink(City src, City dest) {
        //System.out.println("[CL] Got link from " + src.getX() + "x" + src.getY() + " to " + dest.getX() + "x" + dest.getY());
        this.src = src;
        this.dest = dest;
        setupEdge();
    }

    private void setupEdge() {
        edge.setStartX(src.getX());
        edge.setStartY(src.getY());
        edge.setEndX(dest.getX());
        edge.setEndY(dest.getY());
        edge.setStroke(Color.BLACK);
        edge.setStrokeWidth(1);
    }


    public City getSrc() { return src; }
    public City getDest() { return dest; }
    public Line getEdge() { return edge; }

    //to-do wygaszanie koloru
    public void EdgeDeath() {}

    //to-do wzmacnianie koloru
    public void useEdge() {}
}
