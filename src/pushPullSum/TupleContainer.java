package pushPullSum;

public class TupleContainer {
    protected double sum;
    protected double weight;

    public TupleContainer(double sum, double weight){
        this.sum = sum;
        this.weight = weight;
    }

    public double getSum(){
        return sum;
    }

    public void setSum(double sum){
        this.sum = sum;
    }

    public double getWeight(){
        return weight;
    }

    public void setWeight(double weight){
        this.weight = weight;
    }
}
