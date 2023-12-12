package ppCommunication;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;

public class ppObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private final int pid;

    public ppObserver (String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    public boolean execute() {
        System.out.println("\n Cycle No " + ppParameter.cycle);

        for (int i = 0; i < Network.size(); i++) {
            double MIN_LOWER = 0;
            double MAX_UPPER = 100;
            double generateRandom = CommonState.r.nextDouble();
            double randomDouble = MIN_LOWER + (MAX_UPPER - MIN_LOWER) * generateRandom;

            if(ppParameter.cycle == 1)
            {
                ((ppProtocol) Network.get(i).getProtocol(pid)).value = randomDouble;
            }

            System.out.println("ID \t" +
                    ((ppProtocol) Network.get(i).getProtocol(pid)).hashCode() +
                    " value \t " +
                    ((ppProtocol) Network.get(i).getProtocol(pid)).value
            );
        }

        ppParameter.cycle++;

        return false;
    }
}
