package services;

import entities.Locker;
import entities.Package;
import enums.LockerState;
import strategy.LockerAssignmentStrategy;
import java.util.ArrayList;
import java.util.List;

public class LockerService {
    private static LockerService instance;
    private List<Locker> lockers;
    private LockerAssignmentStrategy assignmentStrategy;

    private LockerService() {
        this.lockers = new ArrayList<>();
    }

    public static synchronized LockerService getInstance() {
        if (instance == null) {
            instance = new LockerService();
        }
        return instance;
    }

    public void setAssignmentStrategy(LockerAssignmentStrategy assignmentStrategy) {
        this.assignmentStrategy = assignmentStrategy;
    }

    public void addLocker(Locker locker) {
        this.lockers.add(locker);
    }

    public Locker assignLocker(Package pkg) {
        if (assignmentStrategy == null) {
            throw new IllegalStateException("LockerAssignmentStrategy is not set.");
        }

        List<Locker> availableLockers = new ArrayList<>();
        for (Locker l : lockers) {
            if (l.getState() == LockerState.AVAILABLE) {
                availableLockers.add(l);
            }
        }

        return assignmentStrategy.assignLocker(pkg, availableLockers);
    }

    public Locker getLockerById(String lockerId) {
        for (Locker l : lockers) {
            if (l.getId().equals(lockerId)) {
                return l;
            }
        }
        return null;
    }
}
