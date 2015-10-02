/**
 * Created by tz6ysq on 9/28/2015.
 */
public abstract class aElevator implements iElevator {

    public aElevator() {
    }

    @Override
    public D getDirectionTo(int source){
        return null;
    }

    @Override
    public void advance() {

    }

    @Override
    public boolean isIdle() {
        return false;
    }

    @Override
    public boolean isGoing(D d) {
        return false;
    }



    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isHeadedTowards(int source) {
        return false;
    }

    @Override
    public boolean hasPassedThrough(int dest) {
        return false;
    }

    @Override
    public double getTimeTo(Call call) {
        return 0;
    }
}
