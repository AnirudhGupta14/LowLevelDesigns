package models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class SeatLock {
    private final String lockId;
    private final Seat seat;
    private final Show show;
    private final User user;
    private final LocalDateTime lockTime;
    private final int timeoutInMinutes;

    public SeatLock(Seat seat, Show show, User user, int timeoutInMinutes) {
        this.lockId = UUID.randomUUID().toString();
        this.seat = seat;
        this.show = show;
        this.user = user;
        this.lockTime = LocalDateTime.now();
        this.timeoutInMinutes = timeoutInMinutes;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(lockTime.plusMinutes(timeoutInMinutes));
    }

    public boolean isLockedBy(User user) {
        return this.user.equals(user);
    }
}