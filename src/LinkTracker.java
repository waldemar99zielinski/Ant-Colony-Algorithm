import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

public final class LinkTracker {

    private CityLink cityLink;
    private Collection<Double> cityLinkValueHistory;
    private long start;
    private long end;
    private double alpha;
    LinkTracker(CityLink cityLink, double alpha) {
        this.cityLink = cityLink;
        this.alpha = alpha;
        cityLinkValueHistory = new ArrayList<>();
        start = System.currentTimeMillis();
    }

    public void trackLink() {
        double val = Double.parseDouble( cityLink.getPheromoneValue().getText() );
        cityLinkValueHistory.add(val);
    }


    public Collection<Double> getCityLinkValueHistory() { return cityLinkValueHistory; }

    public void createChart(Stage stage) {}

    public void saveToFile(int ANT_COUNT) {
        end = System.currentTimeMillis();
        long timeElapsed = end - start;

        String src = cityLink.getSrc().getName();
        String dest = cityLink.getDest().getName();
        String filename = src + "_" + dest + "_" + String.valueOf(ANT_COUNT) + "ants_" + alpha + "_" + timeElapsed;
        StringBuilder sb = new StringBuilder();
                    //maffija



        for (Double d : cityLinkValueHistory) {
            sb.append( String.valueOf(d) + "\r\n");
        }



        try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
            out.print(sb.toString());
            System.out.println("[INFO] LinkTracker " + cityLink.getSrc().getName() + "-" + cityLink.getDest().getName() + " saved to file");
            System.out.println("[INFO] Alpha: "+ alpha + ", Elapsed time: " + timeElapsed/1000 + "s");
        } catch (IOException x) {
            x.printStackTrace();
        }
    }
}
