package Food;

class NormalFood extends FoodItem {
    public NormalFood(int row, int column) {
        super(row, column); // Call superclass constructor
        this.points = 1; // Assign point value
    }
}
