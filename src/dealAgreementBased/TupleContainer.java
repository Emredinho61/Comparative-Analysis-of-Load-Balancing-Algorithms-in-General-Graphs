package dealAgreementBased;

import peersim.core.Node;

public class TupleContainer {
    protected double transferProposal;
    protected Node proposingNode;

    public TupleContainer(Node proposingNode, double transferProposal){
        this.proposingNode = proposingNode;
        this.transferProposal = transferProposal;
    }

    public double getTransferProposal(){
        return this.transferProposal;
    }

    public void setTransferProposal(double transferProposal){
        this.transferProposal = transferProposal;
    }

    public Node getProposingNode(){
        return this.proposingNode;
    }

    public void setProposingNode(Node proposingNode){
        this.proposingNode = proposingNode;
    }
}

