package pushPullSum;

import dealAgreementBased.dealAgreementBasedProtocol;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.lang.Math.round;
import static peersim.core.CommonState.r;
import static pushPullSum.PushPullSumParameter.cycle;
import static pushPullSum.PushPullSumParameter.sumsList;


public class PushPullSumObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private final int pid;

    public PushPullSumObserver(String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
        for (int i = 0; i < Network.size(); i++) {
            int MIN_LOWER = 0;
            int MAX_UPPER = 100;
            Random r = new Random();
            double randomNumber = r.nextInt(MAX_UPPER - MIN_LOWER) + MIN_LOWER;
            sumsList.add(randomNumber);
        }

    }

    @Override
    public boolean execute() {
        boolean converged = false;
        System.out.println("\n Cycle No " + PushPullSumParameter.cycle);

        // For now we generate a txt file using the PrintWriter and the FileWriter modules later on we want to
        // use the terminal to get the outputs
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
            if (PushPullSumParameter.cycle == 0) {
                // Sum is just a random double for now.
                // ((PushPullSumProtocol) node.getProtocol(pid)).setSum(randomNumber);
                // ((PushPullSumProtocol) node.getProtocol(pid)).setSum(sumsList.get(i));

                // For testing purposes we set the Sums manually
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
                // initial weight is 1, sum of all weights is n (number of nodes in the network)
                ((PushPullSumProtocol) node.getProtocol(pid)).setWeight(1);
                ((PushPullSumProtocol) node.getProtocol(pid)).setNewWeight(1);

                // Messages are Tuples of Sums and Weights of each node
                String output = "ID \t" +
                        Network.get(i).hashCode() +
                        " sum \t " +
                        ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getNewSum() +
                        " weight \t" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getNewWeight();
                System.out.println(output);
            } else {
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
        if (PushPullSumParameter.cycle != 0) {
            for (int i = 0; i < Network.size(); i++) {
                Node node = Network.get(i);
                // we output the Hashcode, Sum and Weight of each Node in each cycle
                ((PushPullSumProtocol) node.getProtocol(pid)).setSum(((PushPullSumProtocol) node.getProtocol(pid)).getNewSum());
                ((PushPullSumProtocol) node.getProtocol(pid)).setWeight(((PushPullSumProtocol) node.getProtocol(pid)).getNewWeight());
                ((PushPullSumProtocol) node.getProtocol(pid)).resetMessages();
                String output = "ID \t" +
                        Network.get(i).hashCode() +
                        " sum \t " +
                        ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getNewSum() +
                        " weight \t" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getNewWeight();
                System.out.println(output);

                // System.out.println("Messages node" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getMessage());
                // System.out.println("Messages Neighbor" + ((PushPullSumProtocol) nodeNeighbor.getProtocol(pid)).getMessage());
                System.out.println("Average" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getAverage());
                ((PushPullSumProtocol) node.getProtocol(pid)).resetMessages();

                // writer.println(output);
            }
        }

        PushPullSumParameter.cycle++;


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

    public void getNeighborsSet(Node node, int protocolID) {
        PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(protocolID);

        for (int i = 1; i < Network.size(); i++) {
            Node neighbor = Network.get(i);
            if (node != neighbor) {
                nodeProtocol.addNeighbor(neighbor);
            }
        }
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
        int randomID = r.nextInt(Network.size());
        if (randomID != node.getID()) {
            Node neighbor = Network.get(randomID);
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

    private void aggregateData(PushPullSumProtocol node, int pid) {
        /*
        Procedure Aggregate of the Paper "Adding Pull to Push Sum for Approximate Data Aggregation"
         */
        double sumOfSumMessages = calculateSumofSum(node, pid);
        double sumOfWeightMessages = calculateSumofWeights(node);

        System.out.println("SUM :" + sumOfSumMessages);
        System.out.println("WEIGHT " + sumOfWeightMessages);
        // set the average which is defined as s_u/w_u at time t
        node.setAverage(sumOfSumMessages / sumOfWeightMessages);
        // node.resetMessages();
    }
}
