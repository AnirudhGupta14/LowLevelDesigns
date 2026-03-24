import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserRateLimiter {
    private final Map<String, RateLimiter> userLimits;

    public UserRateLimiter() {
        this.userLimits = new ConcurrentHashMap<>();
    }

    public void registerUser(String userId, RateLimiter assignedLimiterAlgorithm) {
        userLimits.putIfAbsent(userId, assignedLimiterAlgorithm);
    }

    public boolean isAllowed(String userId) {
        RateLimiter limiter = userLimits.get(userId);
        if (limiter == null) {
            System.err.println("User " + userId + " is not registered with an active rate limit policy.");
            return false;
        }
        return limiter.grantAccess();
    }
}
