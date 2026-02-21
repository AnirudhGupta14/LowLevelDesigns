package Services;

import Constants.Direction;
import ElevatorObserver.ElevatorObserver;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Elevator {
    private int id;
    private int currentFloor;
    private Direction direction;
    private List<ElevatorObserver> observers;
    private Queue<ElevatorRequest> requests;

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Elevator(int id) {
        this.id = id;
        this.currentFloor = 0;
        this.direction = Direction.IDLE;
        this.observers = new ArrayList<>();
        this.requests = new LinkedList<>();
    }

    public int getCurrentFloor() {
        return currentFloor;
    }

    public Direction getDirection() {
        return direction;
    }

    public List<ElevatorObserver> getObservers() {
        return observers;
    }

    public Queue<ElevatorRequest> getRequests() {
        return requests;
    }

    public int getId() {
        return id;
    }

    // Add an observer to monitor elevator events
    public void addObserver(ElevatorObserver observer) {
        observers.add(observer);
    }

    // Remove an observer
    public void removeObserver(ElevatorObserver observer) {
        observers.remove(observer);
    }

    // Notify all observers about a state change
    private void notifyDirectionChange(Direction direction) {
        for (ElevatorObserver observer : observers) {
            observer.onElevatorDirectionChange(this, direction);
        }
    }

    // Notify all observers about a floor change
    private void notifyFloorChange(int floor) {
        for (ElevatorObserver observer : observers) {
            observer.onElevatorFloorChange(this, floor);
        }
    }

    public void addRequest(ElevatorRequest elevatorRequest) {
        // Avoid duplicate requests
        if (!requests.contains(elevatorRequest)) {
            requests.add(elevatorRequest);
        }
    }

    // Move the elevator to the next stop as decided by the scheduling strategy
    public void moveToNextStop(int nextStop) {
        while (currentFloor != nextStop) {
            // Update floor based on direction
            if (direction == Direction.UP) {
                currentFloor++;
            } else {
                currentFloor--;
            }
            // Notify observers about the floor change
            notifyFloorChange(currentFloor);
            // Complete arrival once the target floor is reached
            if (currentFloor == nextStop) {
                completeArrival();
                return;
            }
        }
    }

    private void completeArrival() {
        // Remove the current floor from the requests queue
        requests.removeIf(request -> request.getFloor() == currentFloor);
    }
}