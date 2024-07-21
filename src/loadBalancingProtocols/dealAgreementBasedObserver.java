package loadBalancingProtocols;

import dealAgreementBased.TupleContainer;
import peersim.config.Configuration;
import peersim.core.*;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import static loadBalancingProtocols.loadBalancingParameters.cycleDB;
import static loadBalancingProtocols.loadBalancingParameters.loads_sumsList;


public class dealAgreementBasedObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private static final String PAR_K = "rnd.k";
    private final int pid;
    private final int k;

    private double transferProposal = 0.0;

    public dealAgreementBasedObserver(String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
        k = Configuration.getInt(name + "." + PAR_K);
        for (int i = 0; i < Network.size(); i++) {
            int MIN_LOWER = 0;
            int MAX_UPPER = 100;
            Random r = new Random();
            double randomNumber = r.nextInt(MAX_UPPER - MIN_LOWER) + MIN_LOWER;
            loads_sumsList.add(randomNumber);
        }

    }

    @Override
    public boolean execute() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(String.format("simulationResults/terminalOutput_DAB_%d.txt", Network.size()), true), false)) {
            System.out.println("\n Cycle No " + loadBalancingParameters.cycleDB);
            if (loadBalancingParameters.cycleDB == 0) {
                System.out.println(" ");
            } else {
                String outpuCycle = "Cycle No.: " + loadBalancingParameters.cycleDB;
                writer.println(outpuCycle);
                System.out.println("MSE: " + MeanSquaredError(pid));
                String outputMSE = "MSE: " + MeanSquaredError(pid);
                writer.println(outputMSE);
            }

            for (int i = 0; i < Network.size(); i++) {
                int MIN_LOWER = 0;
                int MAX_UPPER = 100;
                Random r = new Random();


                Node node = Network.get(i);
                dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);
                // For fully connecting use:
                // initNeighborsFullyConnect(node, pid);
                // End Of fully connecting

                // For Grid use:
                // nodeProtocol.resetNeighbors();
                // initNeighborsGrid(node, pid, loadBalancingParameters.m_height, loadBalancingParameters.n_width);
                // END of Grid code

                // For Lollipop Graph use:
                nodeProtocol.resetNeighbors();
                initLollipopGraph(node, pid, loadBalancingParameters.m_cliqueSize, loadBalancingParameters.n_PathSize);
                // END of Lolliopop Graph

                if (loadBalancingParameters.cycleDB == 0) {
                    int randomNumber = (int) (Math.random() * 100);
                    // ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(randomNumber);
                    ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(loads_sumsList.get(i));
                    // for testing purposes I am setting the Loads manually
                    /*
                    if (node.getID() == 0) {
                        ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(10);
                    } else if (node.getID() == 1) {
                        ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(31);
                    } else if (node.getID() == 2) {
                        ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(69);
                    } else {
                        ((dealAgreementBasedProtocol) node.getProtocol(pid)).setLoad(10);
                    }
                    */

                } else {
                    System.out.println("Parameter for Round " + cycleDB + "\t ID " + node.getID() + " load " + nodeProtocol.getLoad());
                    String ouputLoad = "ID " + node.getID() + "\t load " + nodeProtocol.getLoad();
                    writer.println(ouputLoad);
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
            loadBalancingParameters.cycleDB++;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
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
        for (Node neighbor : nodeProtocol.getNeighbors()) {
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
        ;

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

    private double calculateNetworksAverage(int pid) {
        double totalSums = 0.0;
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);
            totalSums += nodeProtocol.getLoad();
        }
        return (totalSums / Network.size());
    }

    private double MeanSquaredError(int pid) {
        double networkAverage = calculateNetworksAverage(pid);
        double MSE = 0.0;
        double avgDif = 0.0;
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);
            avgDif += Math.pow(networkAverage - nodeProtocol.getLoad(), 2);
        }
        MSE = avgDif / Network.size();
        return MSE;
    }

    private void updateLoadAfterDeal(Node receivingNode, Node sendingNode, double transferValue, int protocolID) {
        // method to update to new load values
        dealAgreementBasedProtocol receivingNodeProtocol = (dealAgreementBasedProtocol) receivingNode.getProtocol(protocolID);
        dealAgreementBasedProtocol sendingNodeProtocol = (dealAgreementBasedProtocol) sendingNode.getProtocol(protocolID);
        sendingNodeProtocol.subtractLoad(transferValue);
        receivingNodeProtocol.addLoad(transferValue);
    }


    // Methods for connecting different Network Types

    private void initNeighborsFullyConnect(Node node, int pid) {
        /*
        Fully connecting the Graph
         */
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

    private void initNeighborsGrid(Node node, int pid, int m_height, int n_width) {
        // links a (m,n)-Grid with m being the height and n being the width
        int nodeId = (int) node.getID();
        int row = nodeId / n_width;
        int col = nodeId % n_width;
        dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);
        // Add right neighbor
        if (col + 1 < n_width) {
            Node neighbor = Network.get(nodeId + 1);
            nodeProtocol.addNeighbor(neighbor);
        }
        // Add bottom neighbor
        if (row + 1 < m_height) {
            Node neighbor = Network.get(nodeId + n_width);
            nodeProtocol.addNeighbor(neighbor);
        }
        // Add left neighbor
        if (col - 1 >= 0) {
            Node neighbor = Network.get(nodeId - 1);
            nodeProtocol.addNeighbor(neighbor);
        }
        // Add top neighbor
        if (row - 1 >= 0) {
            Node neighbor = Network.get(nodeId - n_width);
            nodeProtocol.addNeighbor(neighbor);
        }
    }

    private void initLollipopGraph(Node node, int pid, int m_cliqueSize, int n_pathSize) {
        // links a Lollipop Graph with cliquesize of m and pathsize of n
        dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);
        double nodeId = node.getID();
        if (m_cliqueSize + n_pathSize != Network.size()) {
            System.out.println("Sum of Clique size and Path size do not match Network size");
        } else if (nodeId == 0) {
            // to one side for the first node we link a path
            nodeProtocol.addNeighbor(Network.get(1));
            // to the other side a clique is linked
            for (int i = m_cliqueSize + 1; i < Network.size(); i++) {
                nodeProtocol.addNeighbor(Network.get(i));
            }
        } else if (nodeId > 0 && nodeId <= n_pathSize - 1) {
            // previous and next nodes are added to neihbors
            nodeProtocol.addNeighbor(Network.get((int) nodeId + 1));
            nodeProtocol.addNeighbor(Network.get((int) nodeId - 1));
        } else if (nodeId == n_pathSize) {
            // for last path element we do not need to connect it to next element
            nodeProtocol.addNeighbor(Network.get((int) nodeId - 1));
        } else {
            // clique linking
            for (int i = m_cliqueSize + 1; i < Network.size(); i++) {
                nodeProtocol.addNeighbor(Network.get(0));
                for (int j = m_cliqueSize + 1; j < Network.size(); j++) {
                    if (i != nodeId && j != nodeId && i != j) {
                        nodeProtocol.addNeighbor(Network.get(j));
                    }
                }
            }
        }
    }

    private void initStar(Node node, int pid) {
        // links a star graph with one central node (Node with ID 0)
        dealAgreementBasedProtocol nodeProtocol = (dealAgreementBasedProtocol) node.getProtocol(pid);
        if (node.getID() == 0) {
            for (int i = 1; i < Network.size(); i++) {
                nodeProtocol.addNeighbor(Network.get(i));
            }
        } else {
            nodeProtocol.addNeighbor(Network.get(0));
        }
    }

}
