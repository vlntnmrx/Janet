package janet;

public class PWLinear extends Activation {

    public PWLinear(double low, double high) {
        super(low, high);
    }

    @Override
    public double function(double act) {
        return act > getHigh() ? getHigh() : (act < getLow() ? getLow() : act);
    }

    @Override
    public double derive(double act) {
        return act > getHigh() ? 0 : (act < getLow() ? 0 : 1);
    }
}
