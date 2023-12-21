package ppCommunication;

import peersim.config.Configuration;
import peersim.config.ParsedProperties;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class PushPullSumObserver implements Control {
    private static final String PAR_PROT = "protocol";
    private final int pid;

    public PushPullSumObserver(String name) {
        pid = Configuration.getPid(name + "." + PAR_PROT);
    }

    @Override
    public boolean execute() {
        System.out.println("\n Cycle No " + PushPullSumParameter.cycle);

        // For now we generate a txt file using the PrintWriter and the FileWriter modules later on we want to
        // use the terminal to get the outputs
        try (PrintWriter writer = new PrintWriter(new FileWriter("terminalOutput2.txt", true), true)) {
            for (int i = 0; i < Network.size(); i++) {
                double MIN_LOWER = 0;
                double MAX_UPPER = 100;

                // generate a random double
                double generateRandom = CommonState.r.nextDouble();
                double randomDouble = MIN_LOWER + (MAX_UPPER - MIN_LOWER) * generateRandom;
                int chosenNeighbor = CommonState.r.nextInt(Network.size());

                // use the random number as index number of the Network to get a "random" Node
                Node node = Network.get(i);
                Node nodeNeighbor = Network.get(chosenNeighbor);

                // For the first cycle we set the initial Sum, Weight And the Set of Messages
                if (PushPullSumParameter.cycle == 1) {
                    // Sum is just a random double for now.
                    ((PushPullSumProtocol) node.getProtocol(pid)).setSum(randomDouble);

                    // initial weight is 1, sum of all weights is n (number of nodes in the network)
                    ((PushPullSumProtocol) node.getProtocol(pid)).setWeight(1);

                    // Messages are Tuples of Sums and Weights of each node
                    TupleContainer Messages = new TupleContainer(((PushPullSumProtocol) node.getProtocol(pid)).getSum(), ((PushPullSumProtocol) node.getProtocol(pid)).getWeight());
                    ((PushPullSumProtocol) node.getProtocol(pid)).setMessages(Messages);
                }

                aggregateData((PushPullSumProtocol) node.getProtocol(pid));
                sendRequestData(node, nodeNeighbor, pid);
                respondToRequests((PushPullSumProtocol) node.getProtocol(pid), pid);


                // we output the Hashcode, Sum and Weight of each Node in each cycle
                String output = "ID \t" +
                        ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).hashCode() +
                        " sum \t " +
                        ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getSum() +
                        " weight \t" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getWeight();

                System.out.println(output);


                System.out.println(((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getMessage());
                System.out.println("Average" + ((PushPullSumProtocol) Network.get(i).getProtocol(pid)).getAverage());

                writer.println(output);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        PushPullSumParameter.cycle++;


        return false;
    }

    private void receiveRequestData(double requestSum, double requestWeight, PushPullSumProtocol node) {
        /*
        Helper Method for RequestData.
         */
        // when receiving the Request data we just want to add the requested data to our sum
        node.addSum(requestSum);
        node.addSum(requestWeight);
    }

    private void sendRequestData(Node node, Node neighbor, int protocolID) {
        /*
        Procedure RequestData of the pseudocode in the Paper "Adding Pull to Push Sum for Approximate Data Aggregation"
         */
        PushPullSumProtocol nodeProtocol = (PushPullSumProtocol) node.getProtocol(protocolID);

        double requestSum = nodeProtocol.getSum() / 2;
        double requestWeight = nodeProtocol.getWeight() / 2;

        // send sum / 2 and weight / 2  to the random chosen node
        receiveRequestData(requestSum, requestWeight, nodeProtocol);

        // by sending the half of our sum and weights to the chosen nodes we only have half of it remaining so
        // we do not send it but just half it.
        nodeProtocol.setSum(requestSum);
        nodeProtocol.setWeight(requestWeight);

        // set of nodes that are calling our node u at round t
        nodeProtocol.addReceivedNode(neighbor);

        // we also add the tuple of our new sums and weights to the set of messages
        nodeProtocol.setMessages(new TupleContainer(requestSum, requestWeight));
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
            receiveResponseData(replySum, replyWeight, node);
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
    }
}
