package dealAgreementBased;

import peersim.cdsim.CDProtocol;
import peersim.config.FastConfig;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class dealAgreementBasedProtocol implements CDProtocol, Linkable {

    protected double value;
    protected double load;
    protected double transferProposal;

    Set<TupleContainer> transferProposals = new HashSet<>();

    protected Set<Node> neighbors = new HashSet<>();

    public dealAgreementBasedProtocol(String name) {


    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        System.out.println("Node " + node.getID() + " Load: " + getLoad());
        resetTransferProposals();
    }

    @Override
    public int degree() {
        return this.neighbors.size();
    }

    @Override
    public Node getNeighbor(int i) {
        int index = 0;
        for (Node neighbor : this.neighbors) {
            if (index == i) {
                return neighbor;
            }
            index++;
        }
        return null;
    }

    @Override
    public boolean addNeighbor(Node neighbour) {
        this.neighbors.add(neighbour);
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
        dealAgreementBasedProtocol node = null;

        try {
            node = (dealAgreementBasedProtocol) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        } // never happens

        return node;
    }

    public boolean removeNeighbor(Node neighbour) {
        this.neighbors.remove(neighbour);
        return true;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getLoad() {
        return this.load;
    }

    public void setLoad(double load) {
        this.load = load;
    }

    public void subtractLoad(double loadToSubstract) {
        this.load -= loadToSubstract;
    }

    public void addLoad(double loadToAdd) {
        this.load += loadToAdd;
    }


    public double getTransferProposal() {
        return this.transferProposal;
    }

    public void setProposal(double transferProposal) {
        this.transferProposal = transferProposal;
    }


    public Set<Node> getNeighbors() {
        return this.neighbors;
    }

    public Set<Node> resetNeighbors() {
        return new HashSet<>();
    }

    public void addNewNeighbors(Node node) {
        this.neighbors.add(node);
    }

    public void addToTransferProposalsSet(Node proposingNode, double transferProposal) {
        this.transferProposals.add(new TupleContainer(proposingNode, transferProposal));
    }

    public Set<TupleContainer> getAllTransferProposals() {
        return this.transferProposals;
    }

    public void resetTransferProposals() {
        this.transferProposals = new HashSet<>();
    }

}
