package ppCommunication;

import peersim.config.Configuration;
import peersim.config.ParsedProperties;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class PushPullSumObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private final int pid;

    public PushPullSumObserver(String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    public boolean execute() {
        System.out.println("\n Cycle No " + PushPullSumParameter.cycle);

        // For now we generate a txt file using the PrintWriter and the FileWriter modules later on we want to
        // use the terminal to get the outputs
        try (PrintWriter writer = new PrintWriter(new FileWriter("terminalOutput2.txt", true), true)) {
            for (int i = 0; i < Network.size(); i++) {
                double MIN_LOWER = 0;
                double MAX_UPPER = 100;

                // generate a random double
                double generateRandom = CommonState.r.nextDouble();
                double randomDouble = MIN_LOWER + (MAX_UPPER - MIN_LOWER) * generateRandom;

                // For the first cycle we set the initial Sum, Weight And the Set of Messages
                if (PushPullSumParameter.cycle == 1) {
                    // Sum is just a random double for now.
                    ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).setSum(randomDouble);

                    // initial weight is 1, sum of all weights is n (number of nodes in the network)
                    ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).setWeight(1);

                    // Messages are Tuples of Sums and Weights of each node
                    Set<TupleContainer> Messages = new HashSet<>(Arrays.asList(new TupleContainer(((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getSum(), ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getWeight())));
                    ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).setMessages(Messages);
                }


                // we output the Hashcode, Sum and Weight of each Node in each cycle
                String output = "ID \t" +
                        ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).hashCode() +
                        " sum \t " +
                        ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getSum() +
                        " weight \t" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getWeight();

                System.out.println(output);


                System.out.println(((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getMessage());
                System.out.println("Average" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getAverage());

                writer.println(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PushPullSumParameter.cycle++;


        return false;
    }
}
