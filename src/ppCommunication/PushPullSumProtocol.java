package ppCommunication;

import peersim.cdsim.CDProtocol;
import peersim.core.CommonState;
import peersim.core.Linkable;
import peersim.core.Network;
import peersim.core.Node;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PushPullSumProtocol implements CDProtocol, Linkable {

    protected double value;

    public PushPullSumProtocol(String name)
    {


    }

    @Override
    public void nextCycle(Node node, int protocolID) {

        try(PrintWriter writer = new PrintWriter(new FileWriter("terminalOutput.txt", true), false)){
            int chosenNeighbor = CommonState.r.nextInt(Network.size());

            PushPullSumProtocol nodeNeighbor = ((PushPullSumProtocol) Network.get(chosenNeighbor).getProtocol(protocolID));

            double averageTemperature = (this.value + nodeNeighbor.value) / 2;
            this.value =  averageTemperature;
            nodeNeighbor.value = averageTemperature;
            String output = "This \t" + this.hashCode() + " value \t" + this.value
                    +" - Neighbor \t" + nodeNeighbor.hashCode()  + " value \t" + nodeNeighbor.value + " Average = " + averageTemperature;

            System.out.println(output);
            writer.println(output);
        }catch (IOException e){
            e.printStackTrace();
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

            node = (PushPullSumProtocol)super.clone();
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
