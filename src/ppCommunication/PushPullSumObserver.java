package ppCommunication;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

public class PushPullSumObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private final int pid;

    public PushPullSumObserver (String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    public boolean execute() {
        System.out.println("\n Cycle No " + PushPullSumParameter.cycle);

        for (int i = 0; i < Network.size(); i++) {
            double MIN_LOWER = 0;
            double MAX_UPPER = 100;
            double generateRandom = CommonState.r.nextDouble();
            double randomDouble = MIN_LOWER + (MAX_UPPER - MIN_LOWER) * generateRandom;

            if(PushPullSumParameter.cycle == 1)
            {
                ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).value = randomDouble;
            }

            System.out.println("ID \t" +
                    ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).hashCode() +
                    " value \t " +
                    ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).value
            );
        }

        PushPullSumParameter.cycle++;


        return false;
    }
}
