import javafx.geometry.Point2D;

public class City {

    private double longitude;
    private double latitude;

    private double x;
    private double y;
    private String name;

    //wspolrzedne normalizujące
    private static final double minX = -1;
    private static final double minY = -1;
    private static final double maxX = 1;
    private static final double maxY = 1;

    public City(double longitude, double latitude, String name) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
    }


    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getName() { return name; }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void convertCoords(int MAP_WIDTH, int MAP_HEIGHT) {

        //konwersja do radianów
        double lon = longitude * Math.PI / 180;
        double lat = latitude * Math.PI / 180;

        Point2D point = new Point2D(lon, Math.log( Math.tan( Math.PI/4 + 0.5*lat )));
       // point.
    }
}
