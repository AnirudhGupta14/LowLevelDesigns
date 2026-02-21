package SchedulingStrategy;

import Services.Elevator;

public interface SchedulingStrategy {
    int getNextStop(Elevator elevator);
}