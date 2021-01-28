import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;


import sndlib.core.network.Link;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class Main extends Application {

    private static final int MAP_WIDTH = 800;
    private static final int MAP_HEIGHT = 800;
    private static final int BORDER_OFFSET = 5;

    private static final String networkPath = "C:\\Users\\Krzysztof\\IdeaProjects\\Ant-Colony-Network\\src\\networkfiles\\janos-us-ca.txt";
    private static final String modelPath = "C:\\Users\\Krzysztof\\IdeaProjects\\Ant-Colony-Network\\src\\networkfiles\\D-D-M-N-C-A-N-N.txt";

    private Pane root = new Pane();
    NetworkAPI networkAPI;
    Colony c;
    Collection<City> cities = new ArrayList<>();
    Collection<CityLink> cityLinks = new ArrayList<>();

    Collection<Circle> cityObjects = new ArrayList<>();
    Collection<Text> cityNames = new ArrayList<>();
    Collection<Line> cityEdges = new ArrayList<>();
    Collection<Text> pheromoneValues = new ArrayList<>();

    ObservableList<Circle> points;// = FXCollections.observableArrayList(cityObjects);
    ObservableList<Text> pointLabels;// = FXCollections.observableArrayList(cityNames);
    ObservableList<Line> lines;// = FXCollections.observableArrayList(cityEdges);
    ObservableList<Text> lineLabels;


    @Override
    public void start(Stage primaryStage) throws Exception {


        primaryStage.setTitle("Ant Colony - City Network Visualizer");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, MAP_WIDTH, MAP_HEIGHT, Color.WHITESMOKE));

        setup();
        points = FXCollections.observableArrayList(cityObjects);
        pointLabels = FXCollections.observableArrayList(cityNames);
        lines = FXCollections.observableArrayList(cityEdges);
        lineLabels = FXCollections.observableArrayList(pheromoneValues);
//
//        root.getChildren().addAll(points);
//        root.getChildren().addAll(pointLabels);
//        root.getChildren().addAll(lines);
//        root.getChildren().addAll(lineLabels);

        root.getChildren().addAll(cityObjects);
        root.getChildren().addAll(cityEdges);
        root.getChildren().addAll(cityNames);
        root.getChildren().addAll(pheromoneValues);
        primaryStage.show();

        // separate non-FX thread
        // runnable for that thread
        new Thread(() -> {
            //for (int i = 0; i < 20; i++) {
            int i = 0;
            while (true) {

                c.makeAntsSelectNextNode();
                c.makeAntsTravel();
                c.pheromoneUpdate();

                updateAllEdges(networkAPI.getNetwork().links() );

                c.updateSolution();
                //++i;

                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }


        }).start();
        //colonyLoop(c);
        networkAPI.saveNetwork();
    }

    private void setup() {
        try {
            loadNetwork();
        } catch (FileNotFoundException x) {
            System.out.println(x.toString());
        }

        setupCityGraphics();

        networkAPI = new NetworkAPI(networkPath, modelPath);
        networkAPI.setupNetwork();

        c = new Colony(networkAPI, 100,"SanFrancisco", "Detroit", 1.25, 1);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void colonyLoop(Colony c) {

        while (c.getNumberOfSolution() < 1) {
            c.makeAntsSelectNextNode();
            c.makeAntsTravel();
            c.pheromoneUpdate();

            updateAllEdges(networkAPI.getNetwork().links() );

            c.updateSolution();

        }
    }
    private void loadNetwork() throws FileNotFoundException {
        String line = "";
        Scanner scanner = new Scanner(new FileInputStream(networkPath));


        StringBuilder sb = new StringBuilder();

        //while file has a new line available to read
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            //System.out.println(line);
            sb.append(line);
            sb.append("\n");
        }

        //limit our content only to nodes
        String citySection = sb.substring(sb.toString().indexOf("NODES") + 10, sb.toString().indexOf("# LINK SECTION") - 4);

        String cityLinksSection = sb.substring(sb.toString().indexOf("LINKS") + 10, sb.toString().indexOf("# DEMAND SECTION") - 4);


        loadCities(citySection);
        mapCitiesToCords();
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
    }

    private void loadCityLinks(String cityLinksSection) {
        String line = "";
        String delims = "[ ]";

        while (cityLinksSection.contains("\n")) {
            int newlineIndex = cityLinksSection.indexOf("\n");
            line = cityLinksSection.substring(0, newlineIndex + 3);

            String[] tokens = line.split(delims);

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
                    cityLinks.add(new CityLink(tokens[0],src, dest));
                    break;
                }

            }

            cityLinksSection = cityLinksSection.substring(newlineIndex+3);
        }




        pheromoneValues.clear();
        for(CityLink link : cityLinks) {
            cityEdges.add(link.getEdge());
            pheromoneValues.add(link.getPheromoneValue());
        }


    }

    private void mapCitiesToCords() {
        double minX = 1000;
        double minY = 1000;
        double maxX = -1000;
        double maxY = -1000;
        double cityLat, cityLong;
        double percentageX, percentageY;

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

        //calculate relative screen position
        for(City city : cities) {
            percentageX = ((city.getLongitude() - minX) / (maxX - minX));
            percentageY = ((city.getLatitude() - minY) / (maxY - minY));

            city.setX(MAP_WIDTH * percentageX);
            city.setY(MAP_HEIGHT * (1 - percentageY));
        }
    }

    private void setupCityGraphics() {
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


    private void updateAllEdges(Collection<Link> links) {
        String linkIdentifier = "";
        Double linkValue;
        for(Link link : links) {
            linkIdentifier = link.getId();
            linkValue = link.getRoutingCost();

            for(CityLink cityLink : cityLinks) {
                if (linkIdentifier.equals(cityLink.getIdentifier())) {
                    try {
                        cityLink.edgeUpdate(linkValue);
                    } catch (IndexOutOfBoundsException x) {}
                    //lineLabels.add(cityLink.getPheromoneValue());
                    break;
                }
            }
        }

        pheromoneValues.clear();
        lineLabels.clear();
        //System.out.println("There are " + cityLinks.size() + " citylinks");
        //for(CityLink cityLink : cityLinks)
            //lineLabels.add(cityLink.getPheromoneValue());
    }

    private void resetList() {
//        cityEdges.clear();
        lines.clear();
        lineLabels.clear();
        for(CityLink link : cityLinks) {
            lines.add(link.getEdge());
            lineLabels.add(link.getPheromoneValue());
        }
    }
//    private void updateEdge();

}