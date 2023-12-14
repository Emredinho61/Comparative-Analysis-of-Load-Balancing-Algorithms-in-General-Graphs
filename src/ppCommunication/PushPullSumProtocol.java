package ppCommunication;

import peersim.cdsim.CDProtocol;
import peersim.config.Configuration;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.Set;
import java.util.HashSet;

public class PushPullSumProtocol implements CDProtocol, Linkable {

    protected double value;

    private static final String PAR_PID = "protocol";
    private static final String PAR_CYCLES = "cycles";

    protected double sum;
    protected double weight;
    private Set<Node> receivedNodes = new HashSet<>();


    public PushPullSumProtocol(String name) {
        this.sum = 50.0;
        this.weight = 1;

    }

    @Override
    public void nextCycle(Node node, int protocolID) {

        int chosenNeighbor = CommonState.r.nextInt(Network.size());

        PushPullSumProtocol nodeNeighbor = ((PushPullSumProtocol) Network.get(chosenNeighbor).getProtocol(protocolID));
        sendRequestData(node, Network.get(chosenNeighbor), protocolID);
        respondToRequests(node, protocolID);


        String output = "This \t" + this.hashCode() + " sum \t" + this.sum + " weight " + this.weight
                + " - Neighbor \t" + nodeNeighbor.hashCode() + " sum \t" + nodeNeighbor.sum + " weight " +  nodeNeighbor.weight;

        System.out.println(output);

    }


    private void receiveRequestData(double requestSum, double requestWeight) {
        sum += requestSum;
        weight += requestWeight;
    }

    private void sendRequestData(Node node, Node neighbor, int protocolID) {
        PushPullSumProtocol neighborProtocol = (PushPullSumProtocol) neighbor.getProtocol(protocolID);
        double requestSum = sum;
        double requestWeight = weight;
        neighborProtocol.receiveRequestData(requestSum, requestWeight);
        receivedNodes.add(node);
    }

    private void receiveResponseData(double replySum, double replyWeight) {
        sum += replySum;
        weight += replyWeight;
    }

    private void respondToRequests(Node node, int protocolID) {
        for (Node callerNode : receivedNodes) {
            PushPullSumProtocol callerProtocol = (PushPullSumProtocol) callerNode.getProtocol(protocolID);
            double replySum = sum / 2;
            double replyWeight = sum / 2;
            callerProtocol.receiveResponseData(replySum, replyWeight);

            sum = sum / 2;
            weight = weight / 2;
        }
    }

    private void aggregateData(Node node) {
        // TODO: Write an average function
    }

    @Override
    public int degree() {
        return 0;
    }

    @Override
    public Node getNeighbor(int i) {
        return null;
    }

    @Override
    public boolean addNeighbor(Node neighbour) {
        return false;
    }

    @Override
    public boolean contains(Node neighbor) {
        return false;
    }

    @Override
    public void pack() {

    }

    @Override
    public void onKill() {

    }

    @Override
    public Object clone() {
        PushPullSumProtocol node = null;

        try {

            node = (PushPullSumProtocol) super.clone();
            node.receivedNodes = new HashSet<>();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } // never happens

        return node;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
