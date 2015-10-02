/**
 * Created by tz6ysq on 9/28/2015.
 */
public abstract class aCall {
    double quueTime, rideTime;
    String rider;
    int time, source, dest;
    public D direction;

    public aCall(D direction, int source, int time) {
        this.direction = direction;
        this.source = source;
        this.time = time;
    }

    public aCall(String args){
        String fields[] = args.split(" ");
        this.rider = fields[0];
        this.time = Integer.parseInt(fields[1]);
        this.source = Integer.parseInt(fields[2]);
        this.dest = Integer.parseInt(fields[3]);
        direction = source > dest ? D.DOWN : D.UP;
    }

    void incrementQueueTime(){quueTime++;}

    void incrementRidingTime(){rideTime++;}

    public boolean equals(Object p){
        aCall p2 = (aCall) p;
        return this.source == p2.source && direction == p2.direction && time == p2.time && dest == p2.dest;
    }
}
