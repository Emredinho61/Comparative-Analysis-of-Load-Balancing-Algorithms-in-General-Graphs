package loadBalancingProtocols;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.round;
import static loadBalancingProtocols.loadBalancingParameters.*;
import static peersim.core.CommonState.r;


public class PushPullSumObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private final int pid;

    public PushPullSumObserver(String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
        /*
        for (int i = 0; i < Network.size(); i++) {
            int MIN_LOWER = 0;
            int MAX_UPPER = 100;
            Random r = new Random();
            double randomNumber = r.nextInt(MAX_UPPER - MIN_LOWER) + MIN_LOWER;
            loads_sumsList.add(randomNumber);
        }
         */
    }

    @Override
    public boolean execute() {


        // we generate a txt file using the PrintWriter and the FileWriter modules
        try (PrintWriter writer = new PrintWriter(new FileWriter(String.format("simulationResults/terminalOutput_PPS_%d.txt", Network.size()), true), false)) {
            System.out.println("\n Cycle No " + loadBalancingParameters.cyclePPS);
            if (loadBalancingParameters.cyclePPS != 0) {
                String outpuCycle = "Cycle No.: " + cyclePPS;
                writer.println(outpuCycle);
            } else {
                // String outputConfig = "Config: " + Network.size() + String.format(" Ring of Cliques %dx%d", m_cliqueAmount, n_CliqueSize);
                // String outputConfig = "Config: " + Network.size() + " Closed Chain Graph";
                // String outputConfig = "Config: " + Network.size() + " Star Graph";
                // String outputConfig = "Config: " + Network.size() + String.format(" Lollipop Graph (%d, %d)", m_cliqueSize, n_PathSize);
                String outputConfig = "Config: " + Network.size() + String.format(" Torus Graph %dx%d", m_height, n_width);
                // String outputConfig = "Config: " + Network.size() + " Fully Connected Graph";
                writer.println(outputConfig);
            }
            for (int i = 0; i < Network.size(); i++) {
                int MIN_LOWER = 0;
                int MAX_UPPER = 100;
                Random r = new Random();

                // generate a random double
                double generateRandom = CommonState.r.nextDouble();
                int randomNumber = r.nextInt(MAX_UPPER - MIN_LOWER) + MIN_LOWER;
                int chosenNeighbor = CommonState.r.nextInt(Network.size());

                // use the random number as index number of the Network to get a "random" Node
                Node node = Network.get(i);
                // getNeighborsSet(node, pid);

                // For the first cycle we set the initial Sum, Weight And the Set of Messages
                if (loadBalancingParameters.cyclePPS == 0) {

                    // For Fully Connected:
                    // initNeighborsFullyConnect(node, pid);
                    // End Of Fully Connected

                    // For Torus:
                    ((PushPullSumProtocol) node.getProtocol(pid)).resetReceivedNodes();
                    initNeighborsTorus(node, pid, loadBalancingParameters.m_height, loadBalancingParameters.n_width);
                    // End of Grid

                    // For Lollipop Graph Use:
                    // ((PushPullSumProtocol) node.getProtocol(pid)).resetReceivedNodes();
                    // initLollipopGraph(node, pid, loadBalancingParameters.m_cliqueSize, n_PathSize);
                    // End of Lollipop Graph

                    // For Star Graph Use:
                    // ((PushPullSumProtocol) node.getProtocol(pid)).resetReceivedNodes();
                    // initStar(node, pid);
                    // End of Star Graph

                    // For Chain Graph
                    // ((PushPullSumProtocol) node.getProtocol(pid)).resetReceivedNodes();
                    // initChain(node, pid);
                    // End of Chain

                    // For Ring of Cliques
                    // ((PushPullSumProtocol) node.getProtocol(pid)).resetReceivedNodes();
                    // initRingOfClique(node, pid, m_cliqueAmount, n_CliqueSize);
                    // End of Ring of Cliques


                    // Sum is just a random double for now.
                    // ((PushPullSumProtocol) node.getProtocol(pid)).setSum(randomNumber);
                    ((PushPullSumProtocol) node.getProtocol(pid)).setSum(loads_sumsList.get(i));
                    ((PushPullSumProtocol) node.getProtocol(pid)).setNewSum(loads_sumsList.get(i));

                    // For testing purposes we set the Sums manually
                    /*
                    if (node.getID() == 0) {
                        ((PushPullSumProtocol) node.getProtocol(pid)).setSum(10);
                        ((PushPullSumProtocol) node.getProtocol(pid)).setNewSum(10);
                    } else if (node.getID() == 1) {
                        ((PushPullSumProtocol) node.getProtocol(pid)).setSum(31);
                        ((PushPullSumProtocol) node.getProtocol(pid)).setNewSum(31);
                    } else if (node.getID() == 2) {
                        ((PushPullSumProtocol) node.getProtocol(pid)).setSum(69);
                        ((PushPullSumProtocol) node.getProtocol(pid)).setNewSum(69);
                    } else {
                        ((PushPullSumProtocol) node.getProtocol(pid)).setSum(10);
                        ((PushPullSumProtocol) node.getProtocol(pid)).setNewSum(10);
                    }
                     */


                    // initial weight is 1, sum of all weights is n (number of nodes in the network)
                    ((PushPullSumProtocol) node.getProtocol(pid)).setWeight(1);
                    ((PushPullSumProtocol) node.getProtocol(pid)).setNewWeight(1);

                    // Messages are Tuples of Sums and Weights of each node
                    String outputSumWeight = "ID " + Network.get(i).hashCode() + "\t sum " + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getNewSum() + "\t weight " + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getNewWeight();
                    System.out.println(outputSumWeight);

                } else {
                    // System.out.println("Node " + node.getID() + " Neighbor " + ((PushPullSumProtocol) node.getProtocol(pid)).getReceivedNodes());
                    ((PushPullSumProtocol) node.getProtocol(pid)).setPushSum(((PushPullSumProtocol) node.getProtocol(pid)).getSum() / 2);
                    ((PushPullSumProtocol) node.getProtocol(pid)).setPushWeight(((PushPullSumProtocol) node.getProtocol(pid)).getWeight() / 2);
                    // System.out.println("PushSum " + ((PushPullSumProtocol) node.getProtocol(pid)).getPushSum() + " PushWeight " + ((PushPullSumProtocol) node.getProtocol(pid)).getPushWeight());
                    aggregateData((PushPullSumProtocol) node.getProtocol(pid), pid);
                    sendRequestData(node, pid, ((PushPullSumProtocol) node.getProtocol(pid)).getPushSum(), ((PushPullSumProtocol) node.getProtocol(pid)).getPushWeight());
                }

            }
            for (int i = 0; i < Network.size(); i++) {
                Node node = Network.get(i);
                respondToRequests((PushPullSumProtocol) node.getProtocol(pid), pid, ((PushPullSumProtocol) node.getProtocol(pid)).getPushSum(), ((PushPullSumProtocol) node.getProtocol(pid)).getPushWeight());

            }
            if (loadBalancingParameters.cyclePPS != 0) {
                for (int i = 0; i < Network.size(); i++) {
                    Node node = Network.get(i);
                    int prevRound = cyclePPS - 1;
                    // we output the Hashcode, Sum and Weight of each Node in each cycle
                    String outputSumWeight = "ID " + Network.get(i).hashCode() + "\t sum " + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getSum() + "\t weight " + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getWeight();
                    System.out.println(outputSumWeight);
                    ((PushPullSumProtocol) node.getProtocol(pid)).setSum(((PushPullSumProtocol) node.getProtocol(pid)).getNewSum());
                    ((PushPullSumProtocol) node.getProtocol(pid)).setWeight(((PushPullSumProtocol) node.getProtocol(pid)).getNewWeight());
                    ((PushPullSumProtocol) node.getProtocol(pid)).resetMessages();

                    System.out.println("Average  " + prevRound + ": " + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getAverage());
                    String outputAverage = "\t Average " + +((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getAverage();
                    ((PushPullSumProtocol) node.getProtocol(pid)).resetMessages();

                    writer.print(outputSumWeight);
                    writer.println(outputAverage);
                }
            }
            String output = "MSE: " + MeanSquaredError(pid);
            System.out.println(output);
            writer.println(output);
            loadBalancingParameters.cyclePPS++;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean areEqualUpToNDecimalPlaces(double value1, double value2, int decimalPlaces) {
        // Scale values to the desired number of decimal places
        double scaleFactor = Math.pow(10, decimalPlaces);
        double scaledValue1 = round(value1 * scaleFactor);
        double scaledValue2 = round(value2 * scaleFactor);

        // Compare scaled values
        return Double.compare(scaledValue1, scaledValue2) == 0;
    }


    private void receiveRequestData(double requestSum, double requestWeight, PushPullSumProtocol node) {
        /*
        Helper Method for RequestData.
         */
        // when receiving the Request data we just want to add the requested data to our sum
        node.addNewSum(requestSum);
        node.addNewWeight(requestWeight);
    }

    private void substractSentData(double sentSum, double sentWeight, PushPullSumProtocol node) {
        node.subtractNewSum(sentSum);
        node.subtractNewWeight(sentWeight);
    }

    private void sendRequestData(Node node, int protocolID, double requestSum, double requestWeight) {
        /*
        Procedure RequestData of the pseudocode in the Paper "Adding Pull to Push Sum for Approximate Data Aggregation"
         */
        PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(protocolID);
        List<Node> nodeList = new ArrayList<>(nodeProtocol.getReceivedNodes());
        int randomID = r.nextInt(nodeList.size());
        if (randomID != node.getID()) {
            Node neighbor = nodeProtocol.getNeighbor(randomID);
            PushPullSumProtocol neighborProtocol = (PushPullSumProtocol) neighbor.getProtocol(protocolID);

            // send (sum / 2) and (weight / 2)  to ourselves
            substractSentData(requestSum, requestWeight, nodeProtocol);
            // send (sum / 2) and (weight / 2)  to the random chosen node
            receiveRequestData(requestSum, requestWeight, neighborProtocol);

            // we also express the communication by adding it to the messages set
            neighborProtocol.setMessages((int) node.getID());
            nodeProtocol.setMessages((int) node.getID());

        } else if (Network.size() == 1) {
            System.out.println("Network size equals one so there is no need to balance.");
        } else {
            sendRequestData(node, pid, requestSum, requestWeight);
        }
    }

    private void receiveResponseData(double replySum, double replyWeight, PushPullSumProtocol node) {
        /*
        Helper Method for ResponseData.
         */
        // same as receiveRequestData
        node.addNewSum(replySum);
        node.addNewWeight(replyWeight);
    }

    private void respondToRequests(PushPullSumProtocol node, int protocolID, double respondSum, double respondWeight) {
        /*
        Procedure ResponseData of the pseudocode in the Paper "Adding Pull to Push Sum for Approximate Data Aggregation"
         */

        // we compute ((sum / 2) / |size of receivedNodes|) and ((weight / 2) / |size of receivedNodes|)9
        double replySum = (respondSum) / node.getMessage().size();
        double replyWeight = (respondWeight) / node.getMessage().size();
        // for all the nodes in the calling Node set we do:
        for (int callerNodeID : node.getMessage()) {
            Node callerNode = Network.get((int) callerNodeID);
            PushPullSumProtocol callerProtocol = (PushPullSumProtocol) callerNode.getProtocol(protocolID);
            // and send this info to the caller Node as a "reply"
            receiveResponseData(replySum, replyWeight, callerProtocol);
            substractSentData(replySum, replyWeight, node);
        }
    }

    private double calculateSumofSum(PushPullSumProtocol node, int pid) {
        /*
        A method that computes the Sum of all sums in the Set of tuples messages
        A Helper method for Aggregate
         */
        double sumOfSums = 0;
        for (Integer oneId : node.getMessage()) {
            Node messagingNode = Network.get(oneId);
            sumOfSums += ((PushPullSumProtocol) messagingNode.getProtocol(pid)).getSum();
        }
        return sumOfSums;
    }

    private double calculateSumofWeights(PushPullSumProtocol node) {
        /*
        A method that computes the Sum of all weights in the Set of tuples messages.
        A Helper method for Aggregate
         */
        double sumOfWeights = 0;
        for (Integer oneId : node.getMessage()) {
            Node messagingNode = Network.get(oneId);
            sumOfWeights += ((PushPullSumProtocol) messagingNode.getProtocol(pid)).getWeight();
        }
        return sumOfWeights;
    }

    private double roundValue(double value) {
        return (double) Math.round(value * 100d) / 100d;
    }

    private double calculateNetworksAverage(int pid) {
        double totalSums = 0.0;
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(pid);
            totalSums += nodeProtocol.getNewSum();
        }
        return (totalSums / Network.size());
    }

    private double MeanSquaredError(int pid) {
        double networkAverage = calculateNetworksAverage(pid);
        double MSE = 0.0;
        double avgDif = 0.0;
        for (int i = 0; i < Network.size(); i++) {
            Node node = Network.get(i);
            PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(pid);
            avgDif += Math.pow(networkAverage - nodeProtocol.getAverage(), 2);
        }
        MSE = avgDif / Network.size();
        return MSE;
    }

    private void aggregateData(PushPullSumProtocol node, int pid) {
        /*
        Procedure Aggregate of the Paper "Adding Pull to Push Sum for Approximate Data Aggregation"
         */
        // set the average which is defined as s_u/w_u at time t
        node.setAverage(node.getSum() / node.getWeight());
        // node.resetMessages();
    }

    // Methods for connecting different Network Types
    private void initNeighborsFullyConnect(Node node, int pid) {
        // connecting the graph such that it is a complete (fully connected grpah)
        PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(pid);
        for (int i = 0; i < Network.size(); i++) {
            Node neighbor = Network.get(i);
            if (!node.equals(neighbor)) {
                nodeProtocol.addNeighbor(neighbor);
            } else {
                nodeProtocol.removeNeighbor(neighbor);
            }
        }
    }

    private void initNeighborsTorus(Node node, int pid, int m_height, int n_width) {
        // links a (m,n)-Torus with m being the height and n being the width
        int nodeId = (int) node.getID();
        int row = nodeId / n_width;
        int col = nodeId % n_width;
        PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(pid);

        // Add right neighbor
        int rightCol = (col + 1) % n_width;
        Node rightNeighbor = Network.get(row * n_width + rightCol);
        nodeProtocol.addNeighbor(rightNeighbor);

        // Add bottom neighbor
        int bottomRow = (row + 1) % m_height;
        Node bottomNeighbor = Network.get(bottomRow * n_width + col);
        nodeProtocol.addNeighbor(bottomNeighbor);

        // Add left neighbor
        int leftCol = (col - 1 + n_width) % n_width;
        Node leftNeighbor = Network.get(row * n_width + leftCol);
        nodeProtocol.addNeighbor(leftNeighbor);

        // Add top neighbor
        int topRow = (row - 1 + m_height) % m_height;
        Node topNeighbor = Network.get(topRow * n_width + col);
        nodeProtocol.addNeighbor(topNeighbor);
    }

    private void initLollipopGraph(Node node, int pid, int m_cliqueSize, int n_pathSize) {
        // links a Lollipop Graph with cliquesize of m and pathsize of n
        PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(pid);
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
        PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(pid);
        if (node.getID() == 0) {
            for (int i = 1; i < Network.size(); i++) {
                // node 0 is linked to all the other neighors
                nodeProtocol.addNeighbor(Network.get(i));
            }
        } else {
            // all nodes are neighbors of node 0 except node 0
            nodeProtocol.addNeighbor(Network.get(0));
        }
    }

    private void initChain(Node node, int pid) {
        // links a  closed (last and first node are connected) chain (path graph)
        PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(pid);
        double nodeID = node.getID();

        // draw a closed chain
        if (nodeID == 0 && Network.size() >= 2) {
            nodeProtocol.addNeighbor(Network.get(1));
            nodeProtocol.addNeighbor(Network.get(Network.size() - 1));
        } else if (nodeID > 0 && nodeID != Network.size() - 1) {
            // adding prev. and following neighbors
            nodeProtocol.addNeighbor(Network.get((int) nodeID + 1));
            nodeProtocol.addNeighbor(Network.get((int) nodeID - 1));
        } else {
            // last node
            nodeProtocol.addNeighbor(Network.get((int) nodeID - 1));
            // if network is closed then the first and last nodes are connected
            nodeProtocol.addNeighbor(Network.get(0));
        }
    }

    private void initRingOfClique(Node node, int pid, int numCliques, int cliqueSize) {
        int nodeId = (int) node.getID();
        PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(pid);
        int cliqueIndex = nodeId / cliqueSize;
        int posInClique = nodeId % cliqueSize;

        // first/last nodes of cliques
        int cliqueStartIndex = cliqueIndex * cliqueSize;
        int cliqueEndIndex = cliqueStartIndex + cliqueSize - 1;

        // Clique connections excluding the bridge nodes within the same clique
        for (int i = cliqueStartIndex; i <= cliqueEndIndex; i++) {
            if (i != nodeId && !(posInClique == 0 && i == cliqueEndIndex) && !(posInClique == cliqueSize - 1 && i == cliqueStartIndex)) {
                Node cliqueNode = Network.get(i);
                nodeProtocol.addNeighbor(cliqueNode);
            }
        }

        // Connect first node of current clique to the last node of the previous clique
        if (posInClique == 0) {
            int prevCliqueIndex = (cliqueIndex - 1 + numCliques) % numCliques;
            int prevCliqueEndIndex = prevCliqueIndex * cliqueSize + cliqueSize - 1;
            Node prevCliqueLastNode = Network.get(prevCliqueEndIndex);
            nodeProtocol.addNeighbor(prevCliqueLastNode);
        }

        // Connect last node of current clique to the first node of the next clique
        if (posInClique == cliqueSize - 1) {
            int nextCliqueIndex = (cliqueIndex + 1) % numCliques;
            int nextCliqueStartIndex = nextCliqueIndex * cliqueSize;
            Node nextCliqueFirstNode = Network.get(nextCliqueStartIndex);
            nodeProtocol.addNeighbor(nextCliqueFirstNode);
        }

        // Ensure first node of the first clique connects with the last node of the last clique
        if (nodeId == 0) {
            Node lastCliqueLastNode = Network.get((numCliques - 1) * cliqueSize + cliqueSize - 1);
            nodeProtocol.addNeighbor(lastCliqueLastNode);
        }

        // Ensure last node of the last clique connects with the first node of the first clique
        if (nodeId == (numCliques - 1) * cliqueSize + cliqueSize - 1) {
            Node firstCliqueFirstNode = Network.get(0);
            nodeProtocol.addNeighbor(firstCliqueFirstNode);
        }
    }

}
