package services;

import models.Seat;
import models.SeatLock;
import models.Show;
import models.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SeatLockService {
    private final Map<String, SeatLock> locks = new ConcurrentHashMap<>();
    private final int defaultTimeoutMinutes = 5;

    public void lockSeat(Seat seat, Show show, User user, int timeoutInMinutes) {
        String lockKey = generateLockKey(seat, show);

        synchronized (this) {
            SeatLock existingLock = locks.get(lockKey);
            if (existingLock != null && !existingLock.isExpired()) {
                throw new IllegalStateException("Seat is already locked");
            }

            SeatLock newLock = new SeatLock(seat, show, user, timeoutInMinutes);
            locks.put(lockKey, newLock);
        }
    }

    public void unlockSeat(Seat seat, Show show, User user) {
        String lockKey = generateLockKey(seat, show);
        SeatLock lock = locks.get(lockKey);

        if (lock != null && lock.isLockedBy(user)) {
            locks.remove(lockKey);
        }
    }

    public void unlockSeats(List<Seat> seats, Show show, User user) {
        for (Seat seat : seats) {
            unlockSeat(seat, show, user);
        }
    }

    public boolean isSeatLocked(Seat seat, Show show) {
        String lockKey = generateLockKey(seat, show);
        SeatLock lock = locks.get(lockKey);
        return lock != null && !lock.isExpired();
    }

    public boolean isSeatLockedByUser(Seat seat, Show show, User user) {
        String lockKey = generateLockKey(seat, show);
        SeatLock lock = locks.get(lockKey);
        return lock != null && !lock.isExpired() && lock.isLockedBy(user);
    }

    public List<SeatLock> getExpiredLocks() {
        return locks.values().stream()
                .filter(SeatLock::isExpired)
                .collect(Collectors.toList());
    }

    public void cleanupExpiredLocks() {
        List<String> expiredLockKeys = locks.entrySet().stream()
                .filter(entry -> entry.getValue().isExpired())
                .map(Map.Entry::getKey)
                .toList();

        for (String lockKey : expiredLockKeys) {
            locks.remove(lockKey);
        }
    }

    public List<Seat> getLockedSeats(Show show) {
        return locks.values().stream()
                .filter(lock -> lock.getShow().equals(show) && !lock.isExpired())
                .map(SeatLock::getSeat)
                .collect(Collectors.toList());
    }

    private String generateLockKey(Seat seat, Show show) {
        return seat.getSeatId() + "_" + show.getShowId();
    }
}