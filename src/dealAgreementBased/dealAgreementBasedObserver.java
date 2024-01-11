package dealAgreementBased;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

public class dealAgreementBasedObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private final int pid;

    public dealAgreementBasedObserver(String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    public boolean execute() {
        System.out.println("\n Cycle No " + dealAgreementBasedParameter.cycle);

        for (int i = 0; i < Network.size(); i++) {
            double MIN_LOWER = 0;
            double MAX_UPPER = 100;
            double generateRandom = CommonState.r.nextDouble();
            double randomDouble = MIN_LOWER + (MAX_UPPER - MIN_LOWER) * generateRandom;
        }

        dealAgreementBasedParameter.cycle++;

        return false;
    }

    private Node minLoadNeighbor(Node minLoadNode) {
        return null;
    }

    private void sendFairProposal(Node receiverNode, double transferProposal) {

    }

    public Node findMaximalProposingTransfer() {
        return null;
    }

    public void agreeOnDeal(Node proposingNode) {

    }

    public void updateLoadAfterDeal() {

    }

    public void sendLoadToNeighbors() {
        
    }

}
