import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    private static final double QUARTERPI = Math.PI / 4.0;

    Collection<City> cities = new ArrayList<>();

    Collection<Circle> citiesv2 = new ArrayList<>();
    Collection<Text> cities_names = new ArrayList<>();

    private Rectangle rect;
    private Pane root = new Pane();
    private Stage stage;


    public Visualizer(Stage stage) {
        this.stage = stage;
        rect = new Rectangle();
        rect.setX(400.0f);
        rect.setY(400.0f);
        rect.setWidth(400.0f);
        rect.setHeight(400.0f);
        rect.setFill(Color.TOMATO);

        Scene scene = new Scene(root, MAP_WIDTH, MAP_HEIGHT, Color.NAVAJOWHITE);
        stage.setTitle("XDDD PLES");
        stage.setScene(scene);
        stage.setResizable(false);

        try {
            loadCities();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (City city : cities) {
            Circle circle = new Circle();
            Text text = new Text();

            circle.setCenterX(city.getX());
            circle.setCenterY(city.getY());
            circle.setRadius(5.0f);
            circle.setFill(Color.PURPLE);

            text.setText(city.getName());
            text.setX(city.getX());
            text.setY(city.getY());
            text.setFill(Color.BLACK);
            citiesv2.add(circle);
            cities_names.add(text);
        }
        drawCircles();
        draw();
    }

    public void drawCircles() {
//        for(Circle city : citiesv2) {
//
//        }
        root.getChildren().setAll(citiesv2);
        root.getChildren().addAll(cities_names);
    }

    public void draw() {
        stage.show();
    }

    private void loadCities() throws FileNotFoundException {
        String modelPath = "C:\\Users\\Krzysztof\\Downloads\\Studia\\PW-20Z\\PSZT\\Projekt2\\Ant-Colony-Algorithm\\src\\networkFiles\\janos-us-ca.txt";

        Scanner scanner = new Scanner(new FileInputStream(modelPath));
        StringBuilder sb = new StringBuilder();

        String line = "";

        while (scanner.hasNextLine()) {
            line = scanner.nextLine();
            sb.append(line);
            sb.append("\n");
        }

        String capturedContent = sb.substring(sb.toString().indexOf("NODES") + 8, sb.toString().indexOf("# LINK SECTION") - 4);

        String delims = "[ ]";
        capturedContent = capturedContent.substring(2);
         while (capturedContent.contains("\n")) {
            int newlineIndex = capturedContent.indexOf("\n");

            line = capturedContent.substring(0, newlineIndex);

            String[] tokens = line.split(delims);

            for(int i =0; i < tokens.length; i++)
                System.out.println("Line: " + tokens[i] + "," );

            capturedContent = capturedContent.substring(newlineIndex+3);

            cities.add(new City(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]), tokens[0]));

         }

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

         minX -= 5;
         minY -= 5;
         maxX += 5;
         maxY += 5;

         double percentageX, percentageY;

         for(City city : cities) {
             percentageX = ((city.getLongitude() - minX) / (maxX - minX));
             percentageY = ((city.getLatitude() - minY) / (maxY - minY));

             //System.out.println(percentageY);
             city.setX(MAP_WIDTH * percentageX);
             city.setY(MAP_HEIGHT * (1 - percentageY));
         }
    }



}
