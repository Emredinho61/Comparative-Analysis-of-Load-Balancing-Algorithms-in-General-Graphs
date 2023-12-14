package ppCommunication;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PushPullSumObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private final int pid;

    public PushPullSumObserver(String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    public boolean execute() {
        System.out.println("\n Cycle No " + PushPullSumParameter.cycle);
        try (PrintWriter writer = new PrintWriter(new FileWriter("terminalOutput2.txt", true), true)) {
            for (int i = 0; i < Network.size(); i++) {
                double MIN_LOWER = 0;
                double MAX_UPPER = 100;
                double generateRandom = CommonState.r.nextDouble();
                double randomDouble = MIN_LOWER + (MAX_UPPER - MIN_LOWER) * generateRandom;

                if (PushPullSumParameter.cycle == 1) {
                    ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).sum = randomDouble;
                    ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).weight = randomDouble;
                }

                String output = "ID \t" +
                        ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).hashCode() +
                        " sum \t " +
                        ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).sum + " degree \t" +
                        " weight \t" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).weight;

                System.out.println(output);
                writer.println(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PushPullSumParameter.cycle++;


        return false;
    }
}
