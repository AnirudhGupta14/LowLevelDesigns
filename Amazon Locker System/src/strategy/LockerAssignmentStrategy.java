package strategy;

import entities.Locker;
import entities.Package;
import java.util.List;

public interface LockerAssignmentStrategy {
    Locker assignLocker(Package pkg, List<Locker> availableLockers);
}
