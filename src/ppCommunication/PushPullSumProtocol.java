package ppCommunication;

import peersim.cdsim.CDProtocol;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;

import java.util.Set;
import java.util.HashSet;

public class PushPullSumProtocol implements CDProtocol, Linkable {

    protected double value;

    private static final String PAR_PID = "protocol";
    private static final String PAR_CYCLES = "cycles";

    protected double sum;
    protected double weight;
    protected double average = 0;
    protected Set<TupleContainer> messages = new HashSet<>();
    private Set<Node> receivedNodes = new HashSet<>();


    public PushPullSumProtocol(String name) {

    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        // generate a random number in the range of the network size
        int chosenNeighbor = CommonState.r.nextInt(Network.size());

        // use the random number as index number of the Network to get a "random" Node
        PushPullSumProtocol nodeNeighbor = ((PushPullSumProtocol) Network.get(chosenNeighbor).getProtocol(protocolID));

        // We don't want nodes to interact with themselves so we exclude this case when aggregating
        if (nodeNeighbor != node.getProtocol(protocolID)) {
            // aggregateData();
            // sendRequestData(node, Network.get(chosenNeighbor), protocolID);
            // respondToRequests(node, protocolID);

            // we output hashcode, sum, weight from node and neighbornode
            String output = "This \t" + this.hashCode() + " sum \t" + this.sum + " weight " + this.weight
                    + " - Neighbor \t" + nodeNeighbor.hashCode() + " sum \t" + nodeNeighbor.sum + " weight " + nodeNeighbor.weight;

            System.out.println(output);
        }

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
            // node.receivedNodes = new HashSet<>();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } // never happens

        return node;
    }

    // GETTER AND SETTER
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public void addSum(double value) {
        this.value += value;
    }


    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void addWeight(double weight) {
        this.weight += weight;
    }

    public Set<TupleContainer> getMessage() {
        return messages;
    }

    public void setMessages(TupleContainer messages) {
        this.messages.add(messages);
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }

    public Set<Node> getReceivedNodes() {
        return this.receivedNodes;
    }

    public void addReceivedNode(Node node) {
        this.receivedNodes.add(node);
    }

}
