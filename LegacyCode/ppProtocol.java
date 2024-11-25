package loadBalancingProtocols;

import peersim.cdsim.CDProtocol;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;

public class ppProtocol implements CDProtocol, Linkable {

    protected double value;

    public ppProtocol(String name)
    {


    }

    @Override
    public void nextCycle(Node node, int protocolID) {
        int chosenNeighbor = CommonState.r.nextInt(Network.size());

        ppProtocol nodeNeighbor = ((ppProtocol) Network.get(chosenNeighbor).getProtocol(protocolID));

        double averageTemperature = (this.value + nodeNeighbor.value) / 2;
        this.value =  averageTemperature;
        nodeNeighbor.value = averageTemperature;

        System.out.println("This \t" + this.hashCode() + " value \t" + this.value
        +" - Neighbor \t" + nodeNeighbor.hashCode()  + " value \t" + nodeNeighbor.value + " Average = " + averageTemperature);


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
        ppProtocol node = null;

        try {

            node = (ppProtocol)super.clone();
        }
        catch( CloneNotSupportedException e ) { e.printStackTrace(); } // never happens

        return node;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
