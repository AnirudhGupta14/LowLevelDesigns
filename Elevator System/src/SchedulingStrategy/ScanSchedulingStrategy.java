package SchedulingStrategy;

import Constants.Direction;
import Services.Elevator;
import Services.ElevatorRequest;

import java.util.PriorityQueue;
import java.util.Queue;

public class ScanSchedulingStrategy implements SchedulingStrategy {

    @Override
    public int getNextStop(Elevator elevator) {

        Direction elevatorDirection = elevator.getDirection();
        int currentFloor = elevator.getCurrentFloor();
        Queue<ElevatorRequest> requests = elevator.getRequests();

        if (requests.isEmpty())
            return currentFloor;

        PriorityQueue<ElevatorRequest> upQueue = new PriorityQueue<>();
        PriorityQueue<ElevatorRequest> downQueue = new PriorityQueue<>((a, b) -> Integer.compare(b.getFloor(), a.getFloor()));

        while (!requests.isEmpty()) {
            ElevatorRequest elevatorRequest = requests.poll();
            int floor = elevatorRequest.getFloor();
            if (floor > currentFloor)
                upQueue.add(elevatorRequest);
            else
                downQueue.add(elevatorRequest);
        }

        if (elevatorDirection == Direction.IDLE) {
            int nearestUpwardRequest =
                    upQueue.isEmpty() ? -1 : upQueue.peek().getFloor();
            int nearestDownwardRequest =
                    downQueue.isEmpty() ? -1 : downQueue.peek().getFloor();


            if (nearestUpwardRequest == -1) {
                elevator.setDirection(Direction.DOWN);
                return downQueue.poll().getFloor();
            } else if (nearestDownwardRequest == -1) {
                elevator.setDirection(Direction.UP);
                return upQueue.poll().getFloor();
            } else {
                if (Math.abs(nearestUpwardRequest - currentFloor)
                        < Math.abs(nearestDownwardRequest - currentFloor)) {
                    elevator.setDirection(Direction.UP);
                    return upQueue.poll().getFloor();
                } else {
                    elevator.setDirection(Direction.DOWN);
                    return downQueue.poll().getFloor();
                }
            }
        }

        if (elevatorDirection == Direction.UP) {
            return !upQueue.isEmpty() ? upQueue.poll().getFloor() : switchDirection(elevator, downQueue);
        }
        else {
            return !downQueue.isEmpty() ? downQueue.poll().getFloor() : switchDirection(elevator, upQueue);
        }
    }

    private int switchDirection(Elevator elevator, PriorityQueue<ElevatorRequest> requestsQueue) {
        elevator.setDirection(elevator.getDirection() == Direction.UP
                ? Direction.DOWN
                : Direction.UP);
        return requestsQueue.isEmpty() ? elevator.getCurrentFloor()
                : requestsQueue.poll().getFloor();
    }
}

