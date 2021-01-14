
import sndlib.core.io.*;
import sndlib.core.model.Model;
import sndlib.core.network.CapacityModule;
import sndlib.core.network.Network;

import java.io.*;


public class Start {

    public static void main(String[] args) throws IOException {

        Reader modelReader = new FileReader("/home/waldemar/IdeaProjects/mrk/src/networkFiles/D-D-M-N-C-A-N-N.txt");
        Reader networkReader = new FileReader("/home/waldemar/IdeaProjects/mrk/src/networkFiles/janos-us-ca.txt");


        SNDlibParser parser = SNDlibIOFactory.newParser(SNDlibIOFormat.NATIVE);
        Network network = null;
        Model model = null;
        try {
            network = parser.parseNetwork(networkReader);
            model = parser.parseModel(modelReader);
        }
        catch(SNDlibParseException | IOException spx) {
            System.err.println("could not parse network or model file: " + spx);
        }

        network.getLink("L121").addModule(new CapacityModule(21.0, 12.0));
        network.getLink("L121").removeModule(new CapacityModule(21.0, 12.0));
        network.getLink("L121").setSetupCost(69.0);
        System.out.println(network.getLink("L121"));
        // get the appropriate writer, e.g. for the native format
        SNDlibWriter writer = SNDlibIOFactory.newWriter(SNDlibIOFormat.NATIVE);

        Writer networkWriter =  new FileWriter("network.txt");
        Writer modelWriter = new FileWriter("model.txt");
        try {
            writer.writeNetwork(network, networkWriter);
            writer.writeModel(model, modelWriter);
            networkWriter.close();
            modelWriter.close();
        }
        catch(SNDlibWriteException swx) {
            System.err.println("could not write network or model file: " + swx);
        }

        System.out.println("Hello World!"); // Display the string.
    }
}
