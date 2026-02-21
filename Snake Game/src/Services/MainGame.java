package Services;

import Helpers.Pair;
import MovementStrategy.HumanMovementStrategy;

import java.util.Scanner;

public class MainGame {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        int width = scanner.nextInt();
        int height = scanner.nextInt();

        SnakeGame snakeGame = new SnakeGame(width, height);

        while (snakeGame.getAlive()) {
            String request = scanner.nextLine();

            switch (request) {
                case "foodAdd":
                    String type = scanner.nextLine();
                    int row = scanner.nextInt();
                    int col = scanner.nextInt();
                    int score = snakeGame.getScoreValue(type);
                    snakeGame.addToFoodMap(new Pair<>(row, col),score);
                    break;

                case "moveStrategySelect":
                    snakeGame.setMovementStrategy(new HumanMovementStrategy());
                    break;

                case "moveSnake":
                    String newDirection = scanner.nextLine();
                    snakeGame.move(newDirection);
                    break;

                case "score":
                    snakeGame.getScore();
                    break;

                case "quit":
                    snakeGame.setAlive(false);
                    break;

                default:
                    System.out.println("Invalid request.");
                    break;
            }
        }
    }
}