/**
 * Created by tz6ysq on 9/28/2015.
 */
public enum D {
    IDLE(0),
    UP(1),
    DOWN(-1);

    public double factor;
    D(int i){
        this.factor = i;
    }
}
