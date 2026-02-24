package models;

import enums.SeatCategory;

public abstract class Seat {
    private final String id;
    private final int row;
    private final int col;
    private final SeatCategory category;
    private final double price;

    public Seat(String id, int row, int col, SeatCategory category, double price) {
        this.id = id;
        this.row = row;
        this.col = col;
        this.category = category;
        this.price = price;
    }

    public String getId() { return id; }
    public int getRow() { return row; }
    public int getCol() { return col; }
    public SeatCategory getCategory() { return category; }
    public double getPrice() { return price; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seat)) return false;
        Seat seat = (Seat) o;
        return id.equals(seat.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return category + "-R" + row + "C" + col + " ($" + price + ")";
    }
}
