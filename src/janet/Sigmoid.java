package janet;

public class Sigmoid extends Activation {
    public Sigmoid(double low, double high) {
        super(low, high);
    }

    @Override
    public double function(double act) {
        return (getHigh() - getLow()) * (1.0 / (1.0 + Math.exp(0.0 - act))) + getLow();
    }

    @Override
    public double derive(double act) {
        return function(act) * (1.0 - function(act));
    }
}
