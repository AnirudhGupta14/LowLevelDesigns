package strategy;

import entities.Locker;
import entities.Package;
import java.util.List;

public class SizeBasedLockerAssignmentStrategy implements LockerAssignmentStrategy {

    @Override
    public Locker assignLocker(Package pkg, List<Locker> availableLockers) {
        for (Locker locker : availableLockers) {
            // In a real system, you might want logic that finds a locker >= package size
            // For simplicity, we just look for exact match or larger.
            if (locker.getSize().compareTo(pkg.getSize()) >= 0) {
                return locker;
            }
        }
        return null;
    }
}
