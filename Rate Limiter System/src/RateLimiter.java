public interface RateLimiter {
    /**
     * Determines whether the current request is allowed to pass through
     * the rate limiter based on the specific algorithm being used.
     *
     * @return true if access is granted, false if rate limited (rejected)
     */
    boolean grantAccess();
}
