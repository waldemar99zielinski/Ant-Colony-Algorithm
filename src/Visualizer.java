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

    Collection<Circle> cityObjects = new ArrayList<>();
    Collection<Text> cityNames = new ArrayList<>();
    Collection<Line> cityLinks = new ArrayList<>();


    private Pane root = new Pane();
    private Stage stage;


    public Visualizer(Stage stage) {

        this.stage = stage;

        Scene scene = new Scene(root, MAP_WIDTH, MAP_HEIGHT, Color.NAVAJOWHITE);
        stage.setTitle("Ant Colony - City Network Visualizer");
        stage.setScene(scene);
        stage.setResizable(false);

        try {
            loadCities();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        mapCitiesToCoords();
        setupVisuals();

        addObjectsToRoot();
        draw();
    }

    private void addObjectsToRoot() {
        root.getChildren().setAll(cityObjects);
        root.getChildren().addAll(cityNames);
    }


    private void draw() {
        stage.show();
    }

    private void loadCities() throws FileNotFoundException {

        //to-do relative path .-.
        String modelPath = "C:\\Users\\Krzysztof\\Downloads\\Studia\\PW-20Z\\PSZT\\Projekt2\\Ant-Colony-Algorithm\\src\\networkFiles\\janos-us-ca.txt";
        String line = "";
        String delims = "[ ]";

        Scanner scanner = new Scanner(new FileInputStream(modelPath));
        StringBuilder sb = new StringBuilder();

        //while file has a new line available to read
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            sb.append(line);
            sb.append("\n");
        }

        //limit our content only to nodes
        String capturedContent = sb.substring(sb.toString().indexOf("NODES") + 10, sb.toString().indexOf("# LINK SECTION") - 5);


        //while a new node is available
        while (capturedContent.contains("\n")) {

            int newlineIndex = capturedContent.indexOf("\n");

            //get 'line' from text
            line = capturedContent.substring(0, newlineIndex);

            //split line into separate strings
            String[] tokens = line.split(delims);

            //shorten string left to parse
            capturedContent = capturedContent.substring(newlineIndex+3);

            //create a new city object based on data in the string
            cities.add(new City(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]), tokens[0]));

        }

        //get last node (no "\n" symbol there)
        line = capturedContent;
        String[] tokens = line.split(delims);
        cities.add(new City(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]), tokens[0]));

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
