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
    protected Set<TupleContainer> messages;
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
            aggregateData();
            sendRequestData(node, Network.get(chosenNeighbor), protocolID);
            respondToRequests(node, protocolID);

            // we output hashcode, sum, weight from node and neighbornode
            String output = "This \t" + this.hashCode() + " sum \t" + this.sum + " weight " + this.weight
                    + " - Neighbor \t" + nodeNeighbor.hashCode() + " sum \t" + nodeNeighbor.sum + " weight " + nodeNeighbor.weight;

            System.out.println(output);
        }

    }


    private void receiveRequestData(double requestSum, double requestWeight) {
        /*
        Helper Method for RequestData.
         */
        // when receiving the Request data we just want to add the requested data to our sum
        sum += requestSum;
        weight += requestWeight;
    }

    private void sendRequestData(Node node, Node neighbor, int protocolID) {
        /*
        Procedure RequestData of the pseudocode in the Paper "Adding Pull to Push Sum for Approximate Data Aggregation"
         */
        PushPullSumProtocol neighborProtocol = (PushPullSumProtocol) neighbor.getProtocol(protocolID);
        double requestSum = sum / 2;
        double requestWeight = weight / 2;

        // send sum / 2 and weight / 2  to the random chosen node
        neighborProtocol.receiveRequestData(requestSum, requestWeight);

        // by sending the half of our sum and weights to the chosen nodes we only have half of it remaining so
        // we do not send it but just half it.
        sum = requestSum;
        weight = requestWeight;

        // set of nodes that are calling our node u at round t
        receivedNodes.add(node);

        // we also add the tuple of our new sums and weights to the set of messages
        messages.add(new TupleContainer(requestSum, requestWeight));
    }

    private void receiveResponseData(double replySum, double replyWeight) {
        /*
        Helper Method for ResponseData.
         */
        // same as receiveRequestData
        sum += replySum;
        weight += replyWeight;
    }

    private void respondToRequests(Node node, int protocolID) {
        /*
        Procedure ResponseData of the pseudocode in the Paper "Adding Pull to Push Sum for Approximate Data Aggregation"
         */
        // we compute ((sum / 2) / |size of receivedNodes|) and ((weight / 2) / |size of receivedNodes|)9
        double replySum = (sum / 2) / receivedNodes.size();
        double replyWeight = (weight / 2) / receivedNodes.size();
        // for all the nodes in the calling Node set we do:
        for (Node callerNode : receivedNodes) {
            PushPullSumProtocol callerProtocol = (PushPullSumProtocol) callerNode.getProtocol(protocolID);
            // and send this info to the caller Node as a "reply"
            callerProtocol.receiveResponseData(replySum, replyWeight);
        }
    }

    private double calculateSumofSum() {
        /*
        A method that computes the Sum of all sums in the Set of tuples messages
        A Helper method for Aggregate
         */
        double sumOfSums = 0;
        for (TupleContainer oneTupleContainer : messages) {
            sumOfSums += oneTupleContainer.getSum();
        }
        return sumOfSums;
    }

    private double calculateSumofWeights() {
        /*
        A method that computes the Sum of all weights in the Set of tuples messages.
        A Helper method for Aggregate
         */
        double sumOfWeights = 0;
        for (TupleContainer oneTupleContainer : messages) {
            sumOfWeights += oneTupleContainer.getWeight();
        }
        return sumOfWeights;
    }

    private void aggregateData() {
        /*
        Procedure Aggregate of the Paper "Adding Pull to Push Sum for Approximate Data Aggregation"
         */
        double sumOfSumMessages = calculateSumofSum();
        double sumOfWeightMessages = calculateSumofWeights();
        // set the average which is defined as s_u/w_u at time t
        average = sumOfSumMessages / sumOfWeightMessages;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Set<TupleContainer> getMessage() {
        return messages;
    }

    public void setMessages(Set<TupleContainer> messages) {
        this.messages = messages;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
