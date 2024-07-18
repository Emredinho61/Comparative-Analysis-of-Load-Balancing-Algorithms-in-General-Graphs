package loadBalancingProtocols;

import peersim.cdsim.CDProtocol;
import peersim.core.Linkable;
import peersim.core.Node;

import java.util.Set;
import java.util.HashSet;

public class PushPullSumProtocol implements CDProtocol, Linkable {

    protected double value;

    protected double sum;
    protected double weight;
    protected double average = 0;
    protected Set<Integer> messages = new HashSet<>();
    private Set<Node> receivedNodes = new HashSet<>();
    protected double pushSum;
    protected double pushWeight;
    protected double pullSum;
    protected double pullWeight;
    protected double newSum;
    protected double newWeight;
    protected double protocolid;


    public PushPullSumProtocol(String name) {

    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        resetMessages();
        this.protocolid = node.getID();
    }

    @Override
    public int degree() {
        return this.receivedNodes.size();
    }

    @Override
    public Node getNeighbor(int i) {
        int index = 0;
        for (Node neighbor : this.receivedNodes) {
            if (index == i) {
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

    public void addNewSum(double sum) {
        this.newSum += sum;
    }

    public void subtractNewSum(double sum) {
        this.newSum -= sum;
    }


    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getNewWeight() {
        return this.newWeight;
    }

    public void setNewWeight(double newWeight) {
        this.newWeight = newWeight;
    }

    public double getNewSum() {
        return this.newSum;
    }

    public void setNewSum(double newSum) {
        this.newSum = newSum;
    }

    public void addNewWeight(double newWeight) {
        this.newWeight += newWeight;
    }

    public double getPushSum() {
        return this.pushSum;
    }

    public double getPushWeight() {
        return this.pushWeight;
    }

    public double getPullSum() {
        return this.pullSum;
    }

    public double getPullWeight() {
        return this.pullWeight;
    }

    public void setPushSum(double pushSum) {
        this.pushSum = pushSum;
    }

    public void setPushWeight(double pushWeight) {
        this.pushWeight = pushWeight;
    }

    public void setPullSum(double pullSum) {
        this.pullSum = pullSum;
    }

    public void setPullWeight(double pullWeight) {
        this.pullWeight = pullWeight;
    }

    public void subtractNewWeight(double weight) {
        this.newWeight -= weight;
    }

    public Set<Integer> getMessage() {
        return messages;
    }

    public void setMessages(Integer messages) {
        this.messages.add(messages);
    }

    public void resetMessages() {
        this.messages = new HashSet<>();
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

    public void resetReceivedNodes() {
        this.receivedNodes = new HashSet<>();
 ;   }

    public boolean removeNeighbor(Node neighbour) {
        this.receivedNodes.remove(neighbour);
        return true;
    }

    public void addReceivedNode(Node node) {
        this.receivedNodes.add(node);
    }

    public double getProtcolid() {
        return this.protocolid;
    }
}
