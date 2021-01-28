
import javafx.application.Application;
import javafx.stage.Stage;
import sndlib.core.io.*;
import sndlib.core.model.Model;
import sndlib.core.network.CapacityModule;
import sndlib.core.network.Network;

import java.io.*;


public class Start extends Application {

//    public static void main(String[] args) throws IOException {
//
//
//
//
//
//        //Routing cost = pheromone
//        //Setup cost  = cost
//        NetworkAPI networkAPI = new NetworkAPI(".\\networkFiles\\janos-us-ca.txt",".\\networkFiles\\D-D-M-N-C-A-N-N.txt");
//
//        //System.out.println(networkAPI.getNodeLinks("Dallas").get(0).getId());
//        //var a = networkAPI.getNodeLinks("Dallas");
//       // a.get(0).setSetupCost(12.0);
//
//        //networkAPI.setLinkPheromone("Dallas", "Denver", 1.25);
//        networkAPI.setupNetwork();
//        Colony c = new Colony(networkAPI, 100,"Sacrameto", "Winnipeg", 1, 1);
//        c.run();
//        networkAPI.saveNetwork();
//        Visualizer visualizer = new Visualizer();
//        visualizer.start();
//    }
    public static void main(String[] args)
{
    launch(args);
}

    @Override
    public void start(Stage stage) throws Exception {
        NetworkAPI networkAPI = new NetworkAPI("C:\\Users\\Krzysztof\\Downloads\\Studia\\PW-20Z\\PSZT\\Projekt2\\Ant-Colony-Algorithm\\src\\networkFiles\\janos-us-ca.txt","C:\\Users\\Krzysztof\\Downloads\\Studia\\PW-20Z\\PSZT\\Projekt2\\Ant-Colony-Algorithm\\src\\networkFiles/D-D-M-N-C-A-N-N.txt");

        //System.out.println(networkAPI.getNodeLinks("Dallas").get(0).getId());
        //var a = networkAPI.getNodeLinks("Dallas");
        // a.get(0).setSetupCost(12.0);

        //networkAPI.setLinkPheromone("Dallas", "Denver", 1.25);
        networkAPI.setupNetwork();
        Colony c = new Colony(networkAPI, 100,"Sacrameto", "Winnipeg", 1, 1);
        c.run();
        networkAPI.saveNetwork();
        Visualizer visualizer = new Visualizer(stage);

    }
}
