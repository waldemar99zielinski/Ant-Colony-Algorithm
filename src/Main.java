import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

    private static final int MAP_WIDTH = 1000;
    private static final int MAP_HEIGHT = 800;
    private static final int BORDER_OFFSET = 5;
    private static final int ANT_COUNT = 100;
    private static String startNode = "Miami";
    private static String endNode = "Miami";
    private static double ALPHA = 1.0;
    private static double BETA = 1.0;
    private static boolean isRunning = false;
    private static final String networkPath = ".\\src\\networkfiles\\janos-us-ca.txt";
    private static final String modelPath = ".\\src\\networkfiles\\D-D-M-N-C-A-N-N.txt";


    private Pane root = new Pane();
    private static TextField alphaInput = new TextField("Input Alpha");
    private static TextField betaInput  = new TextField("Input Beta");
    private static TextField startNodeInput  = new TextField("Input start node");
    private static TextField endNodeInput  = new TextField("Input end node");
    private static Button setButton = new Button("SET");
    private static Button runAlgorithmButton = new Button("RUN");
    private static Button stopAlgorithmButton = new Button("STOP");

    private Thread algorithm;

    NetworkAPI networkAPI;
    Colony c;
    LinkTracker linkTracker = null;

    Collection<City> cities = new ArrayList<>();
    Collection<CityLink> cityLinks = new ArrayList<>();

    Collection<Circle> cityObjects = new ArrayList<>();
    Collection<Text> cityNames = new ArrayList<>();
    Collection<Line> cityEdges = new ArrayList<>();
    Collection<Text> pheromoneValues = new ArrayList<>();


    public static void main(String[] args) { launch(args); }


    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Ant Colony - City Network Visualizer");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, MAP_WIDTH, MAP_HEIGHT, Color.ANTIQUEWHITE));

        try {
            loadNetwork();
            System.out.println("[INFO] Visuals network loaded");
        } catch (FileNotFoundException x) {
            System.err.println("[ERR] File not found");
        }


        setupFX();
        setupCityGraphics();
        eventSetup(primaryStage);


        root.getChildren().addAll(cityObjects);
        root.getChildren().addAll(cityEdges);
        root.getChildren().addAll(cityNames);
        root.getChildren().addAll(pheromoneValues);
        root.getChildren().addAll(alphaInput, betaInput, startNodeInput, endNodeInput);
        root.getChildren().addAll(setButton, runAlgorithmButton, stopAlgorithmButton);

        try {
            primaryStage.show();
        } catch (NullPointerException x) {
            System.out.println( "[ERR] Caught Nullptr");
        }


        algorithm = new Thread( () -> {
            setup();
            System.out.println("[INFO] Colony thread started.");
            while (isRunning) {

                c.makeAntsSelectNextNode();
                c.makeAntsTravel();
                c.pheromoneUpdate();
                updateAllEdges( networkAPI.getNetwork().links() );
                //linkTracker.trackLink();
                c.updateSolution();
                c.printSolution();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            networkAPI.saveNetwork();
        });

    }

    private void setup() {

        System.out.println("[INFO] Setup launched");
        setupLinkTracker("Calgary", "Winnipeg");
        networkAPI = new NetworkAPI(networkPath, modelPath);
        networkAPI.setupNetwork();
        c = new Colony(networkAPI, ANT_COUNT, startNode, endNode, ALPHA, BETA);
    }

    private void loadNetwork() throws FileNotFoundException {

        String line = "";
        Scanner scanner = new Scanner(new FileInputStream(networkPath));
        StringBuilder sb = new StringBuilder();

        //while file has a new line available to read
        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            sb.append(line);
            sb.append("\n");
        }

        //limit our content only to nodes
        String citySection =        sb.substring(sb.toString().indexOf("NODES") + 10, sb.toString().indexOf("# LINK SECTION") - 4);
        String cityLinksSection =   sb.substring(sb.toString().indexOf("LINKS") + 10, sb.toString().indexOf("# DEMAND SECTION") - 4);

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
                    cityLinks.add(new CityLink(tokens[0], src, dest));
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
                        cityLink.edgeUpdate(linkValue, ANT_COUNT);
                    } catch (IndexOutOfBoundsException x) {}
                    //lineLabels.add(cityLink.getPheromoneValue());
                    break;
                }
            }
        }

//        pheromoneValues.clear();
//        lineLabels.clear();
    }

    private void eventSetup(Stage primaryStage) {
        primaryStage.setOnCloseRequest(event -> {
            //linkTracker.saveToFile(ANT_COUNT);
            Platform.exit();
            System.exit(0);
        });

    }

    private void setupLinkTracker(String trackerStart, String trackerEnd) {
        for(CityLink link : cityLinks) {
            if (link.getSrc().getName().equals(trackerStart) && link.getDest().getName().equals(trackerEnd)) {
                linkTracker = new LinkTracker(link, ALPHA);
                System.out.println("[INFO] LinkTracker set to track " + trackerStart + "-" + trackerEnd);
                break;
            }
        }
    }

    private void setupFX() {
        int btnOffset = 30;
        alphaInput.setLayoutY((double)(3 * MAP_HEIGHT / 4) - btnOffset);
        alphaInput.setLayoutX( 20 );
        alphaInput.setStyle("-fx-text-fill: white; -fx-background-color: black;");

        betaInput.setLayoutY((double)(3 * MAP_HEIGHT / 4));
        betaInput.setLayoutX( 20 );
        betaInput.setStyle("-fx-text-fill: white; -fx-background-color: black;");

        startNodeInput.setLayoutY((double)(3 * MAP_HEIGHT / 4) + btnOffset);
        startNodeInput.setLayoutX( 20 );
        startNodeInput.setStyle("-fx-text-fill: white; -fx-background-color: black;");

        endNodeInput.setLayoutY((double)(3 * MAP_HEIGHT / 4) + 2* btnOffset);
        endNodeInput.setLayoutX( 20 );
        endNodeInput.setStyle("-fx-text-fill: white; -fx-background-color: black;");

        setButton.setLayoutY((double)(3 * MAP_HEIGHT / 4) + 3 * btnOffset);
        setButton.setLayoutX( 20 );
        setButton.setStyle("-fx-pref-width: 60; -fx-text-fill: white; -fx-background-color: #ff9633;");

        runAlgorithmButton.setLayoutY((double)(3 * MAP_HEIGHT / 4) + 3 * btnOffset);
        runAlgorithmButton.setLayoutX( 110 );
        runAlgorithmButton.setStyle("-fx-pref-width: 60; -fx-text-fill: white; -fx-background-color: #629602;");

        stopAlgorithmButton.setLayoutY((double)(3 * MAP_HEIGHT / 4) + 4 * btnOffset);
        stopAlgorithmButton.setLayoutX( 20 );
        stopAlgorithmButton.setStyle("-fx-pref-width: 150; -fx-text-fill: white; -fx-background-color: #ff5833;");

        setButton.setOnAction(event -> {
            try {
                ALPHA = Double.parseDouble( alphaInput.getText() );
            } catch (NumberFormatException x) {
                System.err.println("[ERR] Invalid parameters. Try again");
                return;
            }
            try {
                BETA = Double.parseDouble( betaInput.getText() );
            } catch (NumberFormatException x) {
                System.err.println("[ERR] Invalid parameters. Try again");
                return;
            }


            startNode = startNodeInput.getText();
            endNode = endNodeInput.getText();

            boolean fkup = true;
            for (City city : cities) {
                if ( city.getName().equals(startNode)) {
                    fkup = false;
                    break;
                }
            }
            if (fkup) {
                System.err.println("[ERR] Invalid parameters. Try again");
                return;
            }

            fkup = true;
            for (City city : cities) {
                if ( city.getName().equals(endNode)) {
                    fkup = false;
                    break;
                }
            }
            if (fkup) {
                System.err.println("[ERR] Invalid parameters. Try again");
                return;
            }

            isRunning = true;
            System.out.println("[INFO] Parameters set correctly");
        });

        runAlgorithmButton.setOnAction( event -> {
            if (isRunning)
                algorithm.start();
            else
                System.err.println("[ERR] Parameters not set correctly!");
        });

        stopAlgorithmButton.setOnAction( event -> {
            isRunning = false;
        });
    }
}
