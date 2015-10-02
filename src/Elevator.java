import java.util.ArrayList;
import java.util.List;

/**
 * Created by tz6ysq on 9/28/2015.
 */
public class Elevator extends aElevator {
    String name;

    double floor, speed;

    int capacity;
    private double lastFloor;
    int linger = 0;
    D direction = D.IDLE;
    public List<Call> riders = new ArrayList<>();

    public Elevator(String args) {
String[] fields = args.split(" ");
        this.name = fields[0];
        this.capacity = Integer.parseInt(fields[1]);
        this.speed = Double.parseDouble(fields[2]);
        this.floor = this.lastFloor = Integer.parseInt(fields[3]);

    }

    @Override
    public void advance() {
        if (isIdle()) return;

        if (linger > 0){
            linger--;
            System.out.println("Elevator " + name + " is lingering");
            return;
        }

        lastFloor = floor;

        floor += speed * direction.factor;

        floor = (int)(Math.round(floor * 100));
        floor = floor / 100;
        System.out.println("Elevator " + name + " now at " + floor
        + "; last floor was " + lastFloor + "; direction = " + direction.name()
        + "; riders: " + riders.stream().map(r->r.rider + " (" + r.dest + ")")
        .collect(joining(", ")));
    }

    @Override
    public boolean isIdle() {
        return direction == D.IDLE;
    }

    @Override
    public boolean isGoing(D d) {
        return direction == d;
    }

    @Override
    public boolean isEmpty() {
        return riders.isEmpty();
    }

    @Override
    public boolean isHeadedTowards(int source) {
        return isGoing(D.DOWN) ? source < floor : floor < source;
    }

    @Override
    public boolean hasPassedThrough(int dest) {
        return super.hasPassedThrough(dest);
    }

    @Override
    public double getTimeTo(Call call) {

        if (Math.abs((double) call.source - floor) < 1) return 0;

        if (isHeadedTowards(call.source) || isIdle()){
            if (getDirectionTo(call.source) == call.direction){
                return (call.source - floor) / speed;
            }

            if (getDirectionTo(call.source) == D.DOWN){
                return (floor + call.source) / speed;
            }
            return ((floor - floor)+(floor - call.source))/ speed;
        }

        if (getDirectionTo(call.source) != call.direction){
            if (isGoing(D.DOWN)) return (floor + call.source) / speed;
            return 0;
        }
        else{
            if (isGoing(D.DOWN))
                return 0;
            return 0;
        }
    }

    D getDirection(int source){
        return floor > source ? D.DOWN : D.UP;
    }
}
