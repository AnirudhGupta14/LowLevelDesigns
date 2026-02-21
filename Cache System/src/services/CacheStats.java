package services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CacheStats {
    private final long cacheHits;
    private final long cacheMisses;
    private final long dbHits;
    private final long dbMisses;
    private final int cacheSize;
    private final int cacheCapacity;
    private final boolean isFull;
}
