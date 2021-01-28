import javafx.geometry.Point2D;

public class City {

    private double longitude;
    private double latitude;
    //private static final int BORDER_OFFSET = 5;

    private double x;
    private double y;
    private String name;


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

    public City getCity() { return this; }

//    private void mapCityToCoords(int MAP_WIDTH, int MAP_HEIGHT, int minX, int minY, int maxX, int maxY) {
//
//        double percentageX = ((longitude - minX) / (maxX - minX));
//        double percentageY = ((latitude - minY) / (maxY - minY));
//
//        this.x = MAP_WIDTH * percentageX;
//        this.y = MAP_HEIGHT * (1 - percentageY);
//
//
//    }
}
