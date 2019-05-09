package janet;

public class ReLu extends Activation {
    public ReLu(double low, double slope) {
        //Missuse the upper Bound as the slope
        super(low, slope);
    }

    @Override
    public double function(double act) {
        return act <= getLow() ? getLow() : getHigh() * act;
    }

    @Override
    public double derive(double act) {
        return act <= getLow() ? 0.0 : getHigh();
    }
}
