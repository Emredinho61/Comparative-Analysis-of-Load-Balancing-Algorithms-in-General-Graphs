package dealAgreementBased;

import peersim.config.Configuration;
import peersim.core.*;

import javax.swing.*;

import java.util.Random;

import static peersim.core.CommonState.r;


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
            int MIN_LOWER = 0;
            int MAX_UPPER = 100;
            Random r = new Random();


            Node node = Network.get(i);
            dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);
            initNeighbors(node, pid);


            if (dealAgreementBasedParameter.cycle == 0) {
                int randomNumber = (int) (Math.random() * 100);
                // ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(randomNumber);
                // for testing purposes I am setting the Loads manually
                if(node.getID() == 0){
                    ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(10);
                } else if (node.getID() == 1) {
                    ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(31);
                } else if (node.getID() == 2) {
                    ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(69);
                }else {
                    ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(10);
                }
            } else {
                // If a neighbor with minimal load is found, send a fair transfer proposal
                Node minLoadNeighborofNode = findMinLoadNeighbor(node, pid);

                dealAgreementBasedProtocol minLoadNeighborofNodeProtocol = (dealAgreementBasedProtocol) minLoadNeighborofNode.getProtocol(pid);
                if ((nodeProtocol.getLoad() - minLoadNeighborofNodeProtocol.getLoad()) > 0) {
                    this.transferProposal = (nodeProtocol.getLoad() - minLoadNeighborofNodeProtocol.getLoad()) / 2;
                    // minLoadNeighborofNodeProtocol.setProposal(this.transferProposal);
                    sendFairProposal(minLoadNeighborofNodeProtocol, node, this.transferProposal);

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
        }


        dealAgreementBasedParameter.cycle++;

        return false;
    }

    private void initNeighbors(Node node, int pid) {
        dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);
        for (int i = 0; i < Network.size(); i++) {
            Node neighbor = Network.get(i);
            if (!node.equals(neighbor)) {
                nodeProtocol.addNeighbor(neighbor);
            } else {
                nodeProtocol.removeNeighbor(neighbor);
            }
        }
    }

    private Node findMinLoadNeighbor2(Node node, int pid) {
        dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);
        double minLoad = Double.MAX_VALUE;
        Node minLoadNeighbor = null;
        for (Node oneNode : nodeProtocol.getNeighbors()) {
            dealAgreementBasedProtocol neighborProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);
            if (neighborProtocol.getLoad() < minLoad) {
                minLoad = neighborProtocol.getLoad();
                minLoadNeighbor = oneNode;
            }
        }
        return minLoadNeighbor;
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

        dealAgreementBasedProtocol nodeProtocol = ((dealAgreementBasedProtocol) node.getProtocol(protocolID));;

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
