package services;

import models.Seat;
import models.Show;
import models.User;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * Manages seat locking with automatic timeout expiry.
 * When a user initiates a booking, seats are locked for a configurable
 * duration.
 * If payment is not completed within the timeout, seats are automatically
 * released.
 *
 * Thread-safe: All lock/unlock operations are synchronized.
 */
public class SeatLockManager {

    // Represents a single seat lock
    private static class SeatLock {
        final User user;
        final LocalDateTime lockTime;
        final Duration timeout;

        SeatLock(User user, Duration timeout) {
            this.user = user;
            this.lockTime = LocalDateTime.now();
            this.timeout = timeout;
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(lockTime.plus(timeout));
        }
    }

    private static SeatLockManager instance;
    private final Duration lockTimeout;

    // Key: "showId:seatId" → SeatLock
    private final Map<String, SeatLock> locks = new ConcurrentHashMap<>();

    // Scheduler for automatic lock expiry
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "SeatLock-Cleanup");
        t.setDaemon(true);
        return t;
    });

    private SeatLockManager(Duration lockTimeout) {
        this.lockTimeout = lockTimeout;
        // Run cleanup every 30 seconds
        scheduler.scheduleAtFixedRate(this::cleanupExpiredLocks, 30, 30, TimeUnit.SECONDS);
    }

    public static synchronized SeatLockManager getInstance(Duration lockTimeout) {
        if (instance == null) {
            instance = new SeatLockManager(lockTimeout);
        }
        return instance;
    }

    /**
     * Attempts to lock a list of seats for a user.
     * Either ALL seats get locked or NONE (atomic operation).
     */
    public synchronized boolean lockSeats(Show show, List<Seat> seats, User user) {
        // First check if all seats can be locked
        for (Seat seat : seats) {
            String key = buildKey(show, seat);
            SeatLock existingLock = locks.get(key);
            if (existingLock != null && !existingLock.isExpired()
                    && !existingLock.user.equals(user)) {
                System.out.println("🔒 Seat " + seat + " is already locked by " + existingLock.user.getName());
                return false;
            }
        }

        // Lock all seats atomically
        for (Seat seat : seats) {
            String key = buildKey(show, seat);
            locks.put(key, new SeatLock(user, lockTimeout));
        }
        System.out.println("🔐 " + seats.size() + " seat(s) locked for " + user.getName()
                + " (timeout: " + lockTimeout.getSeconds() + "s)");
        return true;
    }

    /**
     * Manually unlock seats (on cancellation or confirmation).
     */
    public synchronized void unlockSeats(Show show, List<Seat> seats) {
        for (Seat seat : seats) {
            String key = buildKey(show, seat);
            locks.remove(key);
        }
        System.out.println("🔓 " + seats.size() + " seat(s) unlocked.");
    }

    /**
     * Check if a seat is currently locked (and not expired).
     */
    public boolean isLocked(Show show, Seat seat) {
        String key = buildKey(show, seat);
        SeatLock lock = locks.get(key);
        if (lock == null)
            return false;
        if (lock.isExpired()) {
            locks.remove(key);
            return false;
        }
        return true;
    }

    /**
     * Get the user who locked a specific seat.
     */
    public User getLockedByUser(Show show, Seat seat) {
        String key = buildKey(show, seat);
        SeatLock lock = locks.get(key);
        if (lock != null && !lock.isExpired()) {
            return lock.user;
        }
        return null;
    }

    /**
     * Get remaining lock time in seconds for a seat.
     */
    public long getRemainingLockSeconds(Show show, Seat seat) {
        String key = buildKey(show, seat);
        SeatLock lock = locks.get(key);
        if (lock == null || lock.isExpired())
            return 0;
        Duration remaining = Duration.between(LocalDateTime.now(), lock.lockTime.plus(lock.timeout));
        return Math.max(0, remaining.getSeconds());
    }

    /**
     * Clean up all expired locks and release associated seats.
     * Called periodically by the scheduler.
     */
    private void cleanupExpiredLocks() {
        List<String> expiredKeys = new ArrayList<>();
        for (Map.Entry<String, SeatLock> entry : locks.entrySet()) {
            if (entry.getValue().isExpired()) {
                expiredKeys.add(entry.getKey());
            }
        }
        for (String key : expiredKeys) {
            locks.remove(key);
            System.out.println("⏰ Lock expired and released: " + key);
        }
    }

    /**
     * Force-expire all locks for a show (for demo/testing).
     */
    public synchronized void forceExpireAllLocks(Show show) {
        List<String> toRemove = new ArrayList<>();
        String prefix = show.getId() + ":";
        for (String key : locks.keySet()) {
            if (key.startsWith(prefix)) {
                toRemove.add(key);
            }
        }
        toRemove.forEach(locks::remove);
        System.out.println("⚡ Force-expired " + toRemove.size() + " lock(s) for show: " + show.getId());
    }

    /**
     * Shutdown the background cleanup scheduler.
     */
    public void shutdown() {
        scheduler.shutdown();
    }

    private String buildKey(Show show, Seat seat) {
        return show.getId() + ":" + seat.getId();
    }
}
