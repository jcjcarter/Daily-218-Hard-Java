/**
 * Created by tz6ysq on 9/28/2015.
 */
public interface iElevator {

    void advance();

    boolean isIdle();

    boolean isGoing(D d);

    D getDirectionTo(int source);

    boolean isEmpty();

    boolean isHeadedTowards(int source);

    boolean hasPassedThrough(int dest);

    double getTimeTo(Call call);


}
