package dealAgreementBased;

import peersim.config.Configuration;
import peersim.core.*;

import javax.swing.*;


public class dealAgreementBasedObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private static final String PAR_K = "rnd.k";
    private final int pid;
    private final int k;

    private double transferProposal = 0.0;

    public dealAgreementBasedObserver(String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
        k = Configuration.getInt(name + "." + PAR_K);
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

            // getNeighborsSet(node, pid);

            dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);

            if (dealAgreementBasedParameter.cycle == 1) {
                ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(randomDouble);
            }

            Node minLoadNeighborofNode = findMinLoadNeighbor(node, pid);

            // If a neighbor with minimal load is found, send a fair transfer proposal
            if (minLoadNeighborofNode != null) {
                dealAgreementBasedProtocol minLoadNeighborofNodeProtocol = (dealAgreementBasedProtocol) minLoadNeighborofNode.getProtocol(pid);
                if ((nodeProtocol.getLoad() - minLoadNeighborofNodeProtocol.getLoad()) > 0) {
                    this.transferProposal = (nodeProtocol.getLoad() - minLoadNeighborofNodeProtocol.getLoad()) / 2;
                    minLoadNeighborofNodeProtocol.setProposal(this.transferProposal);
                    sendFairProposal(nodeProtocol, minLoadNeighborofNode, this.transferProposal);
                }
            }
        }
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);

            if (!nodeProtocol.getAllTransferProposals().isEmpty()) {
                Node maxProposingNode = findMaximalProposingTransfer(node, pid);
                dealAgreementBasedProtocol maxProposingNodeProtocol = (dealAgreementBasedProtocol) maxProposingNode.getProtocol(pid);
                double transferValue = (maxProposingNodeProtocol.getLoad() - nodeProtocol.getLoad()) / 2;
                updateLoadAfterDeal(node, maxProposingNode, transferValue, pid);

            }
            System.out.println(node.hashCode() + " load " + nodeProtocol.getLoad());
            System.out.println(node.hashCode() + " degree " + nodeProtocol.degree());

        }


        dealAgreementBasedParameter.cycle++;

        return false;
    }

    public void getNeighborsSet(Node node, int protocolID) {
        // method to connect an complete Graph
        dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(protocolID);
        for (int i = 0; i < Network.size(); i++) {
            Node neighbor = Network.get(i);
            // we don't want self-loops
            if (node != neighbor) {
                nodeProtocol.addNeighbor(neighbor);
                nodeProtocol.addNewNeighbors(neighbor);
            }
        }
    }

    private Node findMinLoadNeighbor(Node node, int pid) {
        // method to find the Node with minimal load
        dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);

        double minLoad = Double.MAX_VALUE;
        Node minLoadNeighbor = null;

        // we look in the neighbors Set for the minimal Load
        for (int i = 0; i < nodeProtocol.degree(); i++) {
            Node neighbor = nodeProtocol.getNeighbor(i);
            dealAgreementBasedProtocol neighborProtocol = (dealAgreementBasedProtocol) neighbor.getProtocol(pid);
            if (neighborProtocol.getLoad() < minLoad) {
                // update minload and minLoadNeighbor if previous minimum is bigger than current nodes load
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

        // now we look in the proposals set for the maximal value
        for (TupleContainer oneTupleContainer : nodeProtocol.getAllTransferProposals()) {
            if (oneTupleContainer.getTransferProposal() > maxProposal) {
                // update maxProposal and maxProposingNode if proposal we look at is bigger than the current maxproposal
                maxProposal = oneTupleContainer.getTransferProposal();
                maxProposingNode = oneTupleContainer.getProposingNode();
            }
        }
        return maxProposingNode;

    }

    private void updateLoadAfterDeal(Node receivingNode, Node sendingNode, double transferValue, int protocolID) {
        // method to update to new load values
        dealAgreementBasedProtocol receivingNodeProtocol = (dealAgreementBasedProtocol) receivingNode.getProtocol(protocolID);
        dealAgreementBasedProtocol sendingNodeProtocol = (dealAgreementBasedProtocol) sendingNode.getProtocol(protocolID);
        sendingNodeProtocol.subtractLoad(transferValue);
        receivingNodeProtocol.addLoad(transferValue);

    }

}
