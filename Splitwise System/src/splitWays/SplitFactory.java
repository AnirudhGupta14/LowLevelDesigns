package splitWays;

// Factory class for creating instances of different Split types
public class SplitFactory {
    /**
     - Factory method to create a Split instance based on the specified split type.
     - 	     - @param splitType The type of split to create ("EQUAL", "PERCENTAGE", "EXACT").
     - @return An instance of the corresponding Split implementation.
     - @throws IllegalArgumentException if the split type is unknown.
     */
    public static Split createSplit(String splitType) {
        // Throw an exception if the split type is invalid
        return switch (splitType) {
            case "EQUAL" -> new EqualSplit(); // Return an EqualSplit instance
            case "PERCENTAGE" -> new PercentageSplit(); // Return a PercentageSplit instance
            default -> throw new IllegalArgumentException("Unknown split type: " + splitType);
        };
    }
}