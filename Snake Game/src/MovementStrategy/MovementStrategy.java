package MovementStrategy;

import Helpers.Pair;

public interface MovementStrategy {
    Pair<Integer, Integer> getNextPosition(Pair<Integer, Integer> currentHead, String direction);
}
