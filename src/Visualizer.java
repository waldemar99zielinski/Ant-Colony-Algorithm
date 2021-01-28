import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class Visualizer {

    private static final int MAP_WIDTH = 800;
    private static final int MAP_HEIGHT = 800;
    private static final int BORDER_OFFSET = 5;

    Collection<City> cities = new ArrayList<>();
    Collection<CityLink> cityLinks = new ArrayList<>();

    Collection<Circle> cityObjects = new ArrayList<>();
    Collection<Text> cityNames = new ArrayList<>();
    Collection<Line> cityEdges = new ArrayList<>();


    private Pane root = new Pane();
    private Stage stage;


    public Visualizer(Stage stage) {

        this.stage = stage;

        Scene scene = new Scene(root, MAP_WIDTH, MAP_HEIGHT, Color.NAVAJOWHITE);
        stage.setTitle("Ant Colony - City Network Visualizer");
        stage.setScene(scene);
        stage.setResizable(false);

        try {
            loadNetwork();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

//        mapCitiesToCoords();
        setupVisuals();

        addObjectsToRoot();
        draw();
    }

    private void addObjectsToRoot() {
        root.getChildren().setAll(cityObjects);
        root.getChildren().addAll(cityNames);
        root.getChildren().addAll(cityEdges);
    }


    private void draw() {
        stage.show();
    }

    private void loadNetwork() throws FileNotFoundException {

        //to-do relative path .-.
        String modelPath = "C:\\Users\\Krzysztof\\Downloads\\Studia\\PW-20Z\\PSZT\\Projekt2\\Ant-Colony-Algorithm\\src\\networkFiles\\janos-us-ca.txt";
        String line = "";
//        String delims = "[ ]";

        Scanner scanner = new Scanner(new FileInputStream(modelPath));
        StringBuilder sb = new StringBuilder();

        //while file has a new line available to read
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            sb.append(line);
            sb.append("\n");
        }

        //limit our content only to nodes
        String citySection = sb.substring(sb.toString().indexOf("NODES") + 10, sb.toString().indexOf("# LINK SECTION") - 4);

        String cityLinksSection = sb.substring(sb.toString().indexOf("LINKS") + 10, sb.toString().indexOf("# DEMAND SECTION") - 4);

        loadCities(citySection);
        loadCityLinks(cityLinksSection);


    }

    private void loadCities(String citySection) {

        String line = "";
        String delims = "[ ]";
        //while a new node is available
        while (citySection.contains("\n")) {

            int newlineIndex = citySection.indexOf("\n");

            //get 'line' from text
            line = citySection.substring(0, newlineIndex);

            //split line into separate strings
            String[] tokens = line.split(delims);

            //shorten string left to parse
            citySection = citySection.substring(newlineIndex+3);

            //create a new city object based on data in the string
            cities.add(new City(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]), tokens[0]));

        }

        //get last node (no "\n" symbol there)
        line = citySection;
        String[] tokens = line.split(delims);
        cities.add(new City(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]), tokens[0]));

        mapCitiesToCoords();
    }

    private void loadCityLinks(String linkSection) {
        String line = "";
        String delims = "[ ]";

        while (linkSection.contains("\n")) {
            int newlineIndex = linkSection.indexOf("\n");

            line = linkSection.substring(0, newlineIndex + 3);

            String[] tokens = line.split(delims);

            for(int i = 0; i < tokens.length; i++)
                System.out.println(tokens[i]);


            boolean foundSrc = false;
            boolean foundDest = false;
            City src = null;
            City dest = null;


            for(City city : cities) {

                //found src
                if (tokens[2].contains(city.getName()) ) {
                    src = city.getCity();
                    foundSrc = true;

                //found dest
                } else if (tokens[3].contains(city.getName()) ) {
                    dest = city.getCity();
                    foundDest = true;
                }

                //add new cityLink
                if (foundSrc && foundDest) {
                    cityLinks.add(new CityLink(src, dest));
                    break;
                }

            }

            linkSection = linkSection.substring(newlineIndex+3);


        }


        for (CityLink cityLink : cityLinks) {
            //System.out.println("EDGE FROM: " + cityLink.getSrc().getName() + " TO: " + cityLink.getDest().getName());
            //System.out.println(cityLink.getSrc().getX() + " " + cityLink.getSrc().getY() + " TO " + cityLink.getDest().getX() + " " + cityLink.getDest().getY());
            cityEdges.add(cityLink.getEdge());
        }
    }

    private void mapCitiesToCoords() {
        double minX = 1000;
        double minY = 1000;
        double maxX = -1000;
        double maxY = -1000;
        double cityLat, cityLong;

        //find min max values for normalisation
        for (City city : cities) {

            cityLong = city.getLongitude();
            cityLat = city.getLatitude();

            minX = Math.min(minX, cityLong);
            minY = Math.min(minY, cityLat);
            maxX = Math.max(maxX, cityLong);
            maxY = Math.max(maxY, cityLat);
        }

        //spread min max values to avoid cities at border
        minX -= BORDER_OFFSET;
        minY -= BORDER_OFFSET;
        maxX += BORDER_OFFSET;
        maxY += BORDER_OFFSET;


        double percentageX, percentageY;

        //calculate relative screen position
        for(City city : cities) {
            percentageX = ((city.getLongitude() - minX) / (maxX - minX));
            percentageY = ((city.getLatitude() - minY) / (maxY - minY));


            city.setX(MAP_WIDTH * percentageX);

            //y axis is inverted ofc
            city.setY(MAP_HEIGHT * (1 - percentageY));
        }
    }

    private void setupVisuals() {

        for (City city : cities) {

            //every city is represented by a circle and text object
            Circle circle = new Circle();
            Text text = new Text();

            //set circle object properly
            circle.setCenterX(city.getX());
            circle.setCenterY(city.getY());
            circle.setRadius(5.0f);
            circle.setFill(Color.PURPLE);

            //set city name properly
            text.setText(city.getName());
            text.setX(city.getX());
            text.setY(city.getY() - 5);
            text.setFill(Color.BLACK);

            //add objects to collections
            cityObjects.add(circle);
            cityNames.add(text);
        }
    }

}
