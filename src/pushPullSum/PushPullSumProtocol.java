package pushPullSum;

import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.Linkable;
import peersim.core.Node;

import java.util.Set;
import java.util.HashSet;

public class PushPullSumProtocol implements CDProtocol, Linkable {

    protected double value;

    protected double sum;
    protected double weight;
    protected double average = 0;
    protected Set<TupleContainer> messages = new HashSet<>();
    private Set<Node> receivedNodes = new HashSet<>();


    public PushPullSumProtocol(String name) {

    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        int linkableID = FastConfig.getLinkable(protocolID);
        Linkable linkable = (Linkable) node.getProtocol(linkableID);
        for (int i = 0; i < linkable.degree(); ++i) {
            Node peer = linkable.getNeighbor(i);
            if(node != peer){
                linkable.addNeighbor(peer);
            }
        }
    }


    @Override
    public int degree() {
        return this.receivedNodes.size();
    }

    @Override
    public Node getNeighbor(int i) {
        int index = 0;
        for(Node neighbor : this.receivedNodes){
            if(index == i){
                return neighbor;
            }
            index++;
        }
        return null;
    }

    @Override
    public boolean addNeighbor(Node neighbour) {
        this.receivedNodes.add(neighbour);
        return true;
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

    public void addSum(double sum) {
        this.sum += sum;
    }

    public void subtractSum(double sum) {
        this.sum -= sum;
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

    public void subtractWeight(double weight) {
        this.weight -= weight;
    }

    public Set<TupleContainer> getMessage() {
        return messages;
    }

    public void setMessages(TupleContainer messages) {
        this.messages.add(messages);
    }

    public void resetMessages() {
        this.messages.clear();
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
