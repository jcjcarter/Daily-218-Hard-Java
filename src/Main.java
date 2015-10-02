import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by tz6ysq on 9/28/2015.
 */
public class Main {
    static int maxFloor, minFloor, LINGER_TIME = 0;

    public static void main(String[] args) throws IOException, InterruptedException{
        Path fileName = new File(args[0]).toPath();

        int numElevators = Files.lines(fileName).limit(1).map(Integer::parseInt).findFirst().get();

        List<Elevator> elevators = Files.lines(fileName).skip(1).limit(numElevators)
                .map(Elevator::new).collect(toList());

        Map<Integer, List<Call>> presses = Files.lines(fileName).skip(2 + numElevators).
                map(Call::new).collect(groupingBy(e -> e.time, toList()));

        maxFloor = presses.values().stream().flatMap(l->l.stream().map(p->Integer.max(p.dest, p.source)))
        .max(Integer::compare).get();

        minFloor = 1;
        int maxTime = presses.keySet().stream().max(Integer::compare).get(), time = -1;
        final List<Call> queued = new ArrayList<>(), space = new ArrayList<>();

        while (time++ < maxTime || !queued.isEmpty()
                || elevators.stream().anyMatch(e->!e.riders.isEmpty())){
            System.out.println("Time: " + time);

            queued.foreach(Call::incrementQueueTime);
            System.out.println(queued.stream().
            map(p->p.rider + " waits on " + p.source + " to " + p.dest)
            .collect(joining(", ")));

            List<Call> newCalls = presses.getOrDefault(time, new ArrayList<>());
            if (!newCalls.isEmpty()){
                System.out.println("Riders " + newCalls.stream().map(p -> p.rider + " on floor "
                + p.source + " now waiting for " + p.dest).collect(joining(", ")));
            }

            queued.addAll(presses.getOrDefault(time, space));

            elevators.forEach(e -> e.riders.forEach(Call::incrementRidingTime));
            elevators.forEach(Elevator::advance);

            // Drop off passengers.
            elevators.forEach(final elevator -> {
                List<Call> toDropOff = elevator.riders.stream().filter(r -> elevator.hasPassThrough(r.dest))
                        .collect(toList());
                elevator.riders.removeAll(toDropOff);

                if (toDropOff.size() > 0){
                    System.out.println("Elevator " + elevator.name + " dropped off "
                    + toDropOff.stream().map(p -> p.rider).collect(joining(", ")));
                }
            });

            // Stop elevators who have lost their purpose in life.
            elevators.stream().filter(e-> e.isEmpty() && !e.isIdle()).forEach(final elevator -> {
                if (queued.stream().noneMatch(p -> elevator.isHeadedTowards(p.source))){
                    elevator.direction = D.IDLE;
                    System.out.println("Elevator " + elevator.name + " idling now.");
                }
            });

            // Pick up passed callers going the same direction
            elevators.stream().forEach(final elevator -> {
                List<Call> toPickUp = queued.stream()
                        .filter(press -> elevator.hasPassThrough(press.source))
                        && (elevator.isIdle() || elevator.isGoing(press.direction))).limit(
                        elevator.capacity - elevator.riders.size()).collect(toList());

                if (toPickUp.size() > 0){

                    if (elevator.isIdle()){
                        // There could be both up / down waiters at the same spot.
                        // the fair thing to do is too choose the first one.
                        D d = toPickUp.iterator().next().direction;
                        toPickUp = toPickUp.stream().filter(p-> p.direction == d).
                                collect(toList());
                    }
                    queued.removeAll(toPickUp);
                    System.out.println("Elevator " + elevator.name + "has picked up riders. "
                    + toPickUp.stream().map(p -> p.rider).collect(joining(", ")));
                    if (elevator.linger == 0) elevator.linger = LINGER_TIME;

                    elevator.riders.addAll(toPickUp = toPickUp.stream().
                    filter(p -> p.dest != p.source).collect(toList()));
                    if (elevator.isIdle() && !toPickUp.isEmpty())
                        elevator.direction = toPickUp.iterator().next().direciton;
                }

            });

            // sort the callers by first in first out grouping by floor / direction.
            List<Call> distinctQueue = queued.stream()
                    .map(p -> new Call(p.source, p.direction, -1)).distinct()
                    .collect(toList());

            // Dispatch any idle elevators who would reach a caller first.
            while (elevators.stream().anyMatch(Elevator::isIdle) && distinctQueue.size() > 0){
                Call call = distinctQueue.remove(0);
                elevators.stream().sorted(comparing(e -> e.getTimeTo(call) -
                e.speed)).findFirst()
                        .ifPresent(e -> {
                            if (e.isIdle())
                                e.getDirectionTo(call.source);
                        });
            }
        }
    }
}
