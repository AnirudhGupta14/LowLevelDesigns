package Services;

import Helpers.Pair;
import MovementStrategy.HumanMovementStrategy;
import MovementStrategy.MovementStrategy;

import java.util.*;

public class SnakeGame {
    private final GameBoard board;
    private final Deque<Pair<Integer, Integer>> snake;
    private final Set<Pair<Integer, Integer>> snakeMap;
    private Map<Pair<Integer, Integer>, Pair<Integer, Boolean>> foodMap;
    private Boolean isAlive;
    private Integer score;
    private MovementStrategy movementStrategy;

    public SnakeGame(int width, int height) {
        this.board = GameBoard.getInstance(width, height);

        this.snake = new LinkedList<>();
        this.snakeMap = new HashSet<>();
        this.foodMap = new HashMap<>();
        this.isAlive = true;
        this.score = 0;

        initilizeSnakeInitialPostion();
        setMovementStrategy(new HumanMovementStrategy());
    }

    private void initilizeSnakeInitialPostion() {
        Pair<Integer, Integer> initialPos = new Pair<>(0, 0);
        this.snake.addFirst(initialPos);
        this.snakeMap.add(initialPos);
    }

    public void setMovementStrategy(MovementStrategy strategy) {
        this.movementStrategy = strategy;
    }

    public Boolean getAlive() {
        return isAlive;
    }

    public void setAlive(Boolean alive) {
        isAlive = alive;
    }

    public Integer getScore() {
        return score;
    }

    public void addToFoodMap(Pair<Integer, Integer> key, int score) {
        Pair<Integer, Boolean> value = new Pair<>(score, false);
        foodMap.put(key, value);
    }

    public Integer getScoreValue(String input) {
        if ("bonus".equalsIgnoreCase(input)) {
            return 3;
        } else {
            return 1;
        }
    }

    public Integer move(String direction) {

        Pair<Integer, Integer> currentHead = this.snake.peekFirst();
        Pair<Integer, Integer> newHead = this.movementStrategy.getNextPosition(currentHead, direction);
        int newHeadRow = newHead.first;
        int newHeadColumn = newHead.second;

        boolean crossesBoundary = newHeadRow < 0 || newHeadRow >= this.board.getHeight() ||
                newHeadColumn < 0 || newHeadColumn >= this.board.getWidth();

        boolean bitesItself = this.snakeMap.contains(newHead);

        Pair<Integer, Integer> currentTail = this.snake.peekLast();

        if (crossesBoundary || bitesItself) {
            return -1;
        }

        boolean ateFood = this.foodMap.get(newHead).getSecond();

        if (ateFood) {
            this.snakeMap.remove(currentTail);
        } else {
            this.score += this.foodMap.get(newHead).getFirst();
        }
        snake.removeLast();
        snake.addFirst(newHead);
        this.snakeMap.add(newHead);

        return this.score;
    }
}