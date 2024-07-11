package loadBalancingProtocols;

public class TupleContainer {
    protected double sum;
    protected double weight;
    protected double protocolid;

    public TupleContainer(double protocolid, double sum, double weight) {
        this.protocolid = protocolid;
        this.sum = sum;
        this.weight = weight;
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

    public double getProtocolid() {
        return protocolid;
    }

}
