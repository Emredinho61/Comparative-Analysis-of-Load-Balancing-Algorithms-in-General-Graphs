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

import static peersim.core.CommonState.r;
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
                } else if (node.getID() == 1) {
                    ((PushPullSumProtocol) node.getProtocol(pid)).setSum(31);
                } else if (node.getID() == 2) {
                    ((PushPullSumProtocol) node.getProtocol(pid)).setSum(69);
                } else {
                    ((PushPullSumProtocol) node.getProtocol(pid)).setSum(10);
                }
                // initial weight is 1, sum of all weights is n (number of nodes in the network)
                ((PushPullSumProtocol) node.getProtocol(pid)).setWeight(1);

                // Messages are Tuples of Sums and Weights of each node
                TupleContainer Messages = new TupleContainer(((PushPullSumProtocol) node.getProtocol(pid)).getSum(),
                        ((PushPullSumProtocol) node.getProtocol(pid)).getWeight());
                ((PushPullSumProtocol) node.getProtocol(pid)).setMessages(Messages);
                String output = "ID \t" +
                        Network.get(i).hashCode() +
                        " sum \t " +
                        ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getSum() +
                        " weight \t" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getWeight();


                System.out.println(output);
            } else {

                aggregateData((PushPullSumProtocol) node.getProtocol(pid));
                sendRequestData(node, pid);
                respondToRequests((PushPullSumProtocol) node.getProtocol(pid), pid);
                // we output the Hashcode, Sum and Weight of each Node in each cycle
                String output = "ID \t" +
                        Network.get(i).hashCode() +
                        " sum \t " +
                        ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getSum() +
                        " weight \t" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getWeight();


                System.out.println(output);


                // System.out.println("Messages node" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getMessage());
                // System.out.println("Messages Neighbor" + ((PushPullSumProtocol) nodeNeighbor.getProtocol(pid)).getMessage());
                System.out.println("Average" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getAverage());

                // writer.println(output);


            }

        }
        
        PushPullSumParameter.cycle++;


        return false;
    }

    private static boolean areEqualUpToNDecimalPlaces(double value1, double value2, int decimalPlaces) {
        // Scale values to the desired number of decimal places
        double scaleFactor = Math.pow(10, decimalPlaces);
        double scaledValue1 = Math.round(value1 * scaleFactor);
        double scaledValue2 = Math.round(value2 * scaleFactor);

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
        node.addSum(requestSum);
        node.addWeight(requestWeight);
    }

    private void substractSentData(double sentSum, double sentWeight, PushPullSumProtocol node) {
        node.subtractSum(sentSum);
        node.subtractWeight(sentWeight);
    }

    private void sendRequestData(Node node, int protocolID) {
        /*
        Procedure RequestData of the pseudocode in the Paper "Adding Pull to Push Sum for Approximate Data Aggregation"
         */
        PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(protocolID);
        int randomID = r.nextInt(Network.size());
        if (randomID != node.getID()) {
            Node neighbor = Network.get(randomID);
            System.out.println(neighbor.getID());
            PushPullSumProtocol neighborProtocol = (PushPullSumProtocol) neighbor.getProtocol(protocolID);
            // set of nodes that are calling our node u at round t

            double requestSum = (nodeProtocol.getSum() / 2);
            double requestWeight = (nodeProtocol.getWeight() / 2);


            // send (sum / 2) and (weight / 2)  to ourselves
            substractSentData(requestSum, requestWeight, nodeProtocol);

            // send (sum / 2) and (weight / 2)  to the random chosen node
            receiveRequestData(requestSum, requestWeight, neighborProtocol);

            // we also add the tuple of our new sums and weights to the set of messages
            nodeProtocol.setMessages(new TupleContainer(requestSum, requestWeight));
        } else {
            sendRequestData(node, pid);
        }
    }

    private void receiveResponseData(double replySum, double replyWeight, PushPullSumProtocol node) {
        /*
        Helper Method for ResponseData.
         */
        // same as receiveRequestData
        node.addSum(replySum);
        node.addWeight(replyWeight);
    }

    private void respondToRequests(PushPullSumProtocol node, int protocolID) {
        /*
        Procedure ResponseData of the pseudocode in the Paper "Adding Pull to Push Sum for Approximate Data Aggregation"
         */

        // we compute ((sum / 2) / |size of receivedNodes|) and ((weight / 2) / |size of receivedNodes|)9
        double replySum = (node.getSum() / 2) / node.getReceivedNodes().size();
        double replyWeight = (node.getWeight() / 2) / node.getReceivedNodes().size();
        // for all the nodes in the calling Node set we do:
        for (Node callerNode : node.getReceivedNodes()) {
            PushPullSumProtocol callerProtocol = (PushPullSumProtocol) callerNode.getProtocol(protocolID);
            // and send this info to the caller Node as a "reply"
            receiveResponseData(replySum, replyWeight, callerProtocol);
            substractSentData(replySum, replyWeight, node);
        }
    }

    private double calculateSumofSum(PushPullSumProtocol node) {
        /*
        A method that computes the Sum of all sums in the Set of tuples messages
        A Helper method for Aggregate
         */
        double sumOfSums = 0;
        for (TupleContainer oneTupleContainer : node.getMessage()) {
            sumOfSums += oneTupleContainer.getSum();
        }
        return sumOfSums;
    }

    private double calculateSumofWeights(PushPullSumProtocol node) {
        /*
        A method that computes the Sum of all weights in the Set of tuples messages.
        A Helper method for Aggregate
         */
        double sumOfWeights = 0;
        for (TupleContainer oneTupleContainer : node.getMessage()) {
            sumOfWeights += oneTupleContainer.getWeight();
        }
        return sumOfWeights;
    }

    private void aggregateData(PushPullSumProtocol node) {
        /*
        Procedure Aggregate of the Paper "Adding Pull to Push Sum for Approximate Data Aggregation"
         */
        double sumOfSumMessages = calculateSumofSum(node);
        double sumOfWeightMessages = calculateSumofWeights(node);
        // set the average which is defined as s_u/w_u at time t
        node.setAverage(sumOfSumMessages / sumOfWeightMessages);
        node.resetMessages();
    }
}
