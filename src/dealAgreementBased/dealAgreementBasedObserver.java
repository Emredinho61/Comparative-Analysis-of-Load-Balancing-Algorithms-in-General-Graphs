package dealAgreementBased;

import peersim.config.Configuration;
import peersim.core.*;
import pushPullSum.PushPullSumParameter;
import pushPullSum.PushPullSumProtocol;

import java.util.*;

public class dealAgreementBasedObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private static final String PAR_K = "rnd.k";
    private final int pid;
    private final int k;

    public dealAgreementBasedObserver(String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
        k = Configuration.getInt(name + "." +PAR_K);
    }

    @Override
    public boolean execute() {
        System.out.println("\n Cycle No " + dealAgreementBasedParameter.cycle);

        for (int i = 0; i < Network.size(); i++) {
            double MIN_LOWER = 0;
            double MAX_UPPER = 100;

            // generate a random double
            double generateRandom = CommonState.r.nextDouble();
            double randomDouble = MIN_LOWER + (MAX_UPPER - MIN_LOWER) * generateRandom;


            Node node = Network.get(i);

            getNeighborsSet(node, pid);
            dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);

            if (dealAgreementBasedParameter.cycle == 1) {
                // Load is just a random double for now.
                ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(randomDouble);
            }

            // Find a neighbor with minimal load
            Node minLoadNeighborofNode = findMinLoadNeighbor(node, pid);
            // System.out.println(minLoadNeighborofNode);

            // If a neighbor with minimal load is found, send a fair transfer proposal
            if (minLoadNeighborofNode != null) {
                dealAgreementBasedProtocol minLoadNeighborofNodeProtocol = (dealAgreementBasedProtocol) minLoadNeighborofNode.getProtocol(pid);
                if ((nodeProtocol.getLoad() - minLoadNeighborofNodeProtocol.getLoad()) > 0) {
                    double transferProposal = (nodeProtocol.getLoad() - minLoadNeighborofNodeProtocol.getLoad()) / 2;
                    minLoadNeighborofNodeProtocol.setProposal(transferProposal);
                    sendFairProposal(nodeProtocol, minLoadNeighborofNode, transferProposal);
                }

            }
        }
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);

            if (!nodeProtocol.getAllTransferProposals().isEmpty()) {
                Node maxProposingNode = findMaximalProposingTransfer(node, pid);
                // agreeOnDeal(node, maxProposingNode, pid);
                updateLoadAfterDeal(node, maxProposingNode, pid);
            }

            System.out.println(node.hashCode() + " load " + nodeProtocol.getLoad());
        }


        dealAgreementBasedParameter.cycle++;

        return false;
    }

    public void getNeighborsSet(Node node, int protocolID) {
        // Get the protocol implementing the Linkable interface
        dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(protocolID);

        for (int i = k; i <= CommonState.r.nextInt(Network.size()); i++) {
            Node neighbor = Network.get(i);
            nodeProtocol.addNeighbor(neighbor);
        }
    }

    private Node findMinLoadNeighbor(Node node, int pid) {
        dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);

        double minLoad = Double.MAX_VALUE;
        Node minLoadNeighbor = null;

        for (int i = 0; i < nodeProtocol.degree(); i++) {
            Node neighbor = nodeProtocol.getNeighbor(i);
            dealAgreementBasedProtocol neighborProtocol = (dealAgreementBasedProtocol) neighbor.getProtocol(pid);

            if (neighborProtocol.getLoad() < minLoad) {
                minLoad = neighborProtocol.getLoad();
                minLoadNeighbor = neighbor;
            }
        }

        return minLoadNeighbor;
    }

    private void sendFairProposal(dealAgreementBasedProtocol calledNode, Node callerNode, double transferProposal) {
        calledNode.addToTransferProposalsSet(callerNode, transferProposal);
    }

    private Node findMaximalProposingTransfer(Node node, int protocolID) {
        double maxProposal = Double.MIN_VALUE;
        Node maxProposingNode = null;
        dealAgreementBasedProtocol nodeProtocol = ((dealAgreementBasedProtocol) node.getProtocol(protocolID));
        for (TupleContainer oneTupleContainer : nodeProtocol.getAllTransferProposals()) {
            if (oneTupleContainer.getTransferProposal() > maxProposal) {
                maxProposal = oneTupleContainer.getTransferProposal();
                maxProposingNode = oneTupleContainer.getProposingNode();
            }
        }
        return maxProposingNode;
    }

    private void updateLoadAfterDeal(Node receivingNode, Node sendingNode, int protocolID) {
        dealAgreementBasedProtocol receivingNodeProtocol = (dealAgreementBasedProtocol) receivingNode.getProtocol(protocolID);
        dealAgreementBasedProtocol sendingNodeProtocol = (dealAgreementBasedProtocol) sendingNode.getProtocol(protocolID);
        double transferLoad = (sendingNodeProtocol.getLoad() - receivingNodeProtocol.getLoad()) / 2;
        sendingNodeProtocol.subtractLoad(transferLoad);
        receivingNodeProtocol.addLoad(transferLoad);
    }


}
