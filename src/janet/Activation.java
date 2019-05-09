package janet;

public abstract class Activation {
    private double low;
    private double high;

    abstract public double function(double act);

    abstract public double derive(double act);

    public Activation(double low, double high) {
        this.low = low;
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }
}
