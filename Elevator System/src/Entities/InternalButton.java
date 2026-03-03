package Entities;

import Constants.ButtonStatus;

/**
 * Represents an internal button panel inside an elevator car.
 * Passengers press a floor number to set their destination.
 */
public class InternalButton {
    private int floor;
    private ButtonStatus status;

    public InternalButton(int floor) {
        this.floor = floor;
        this.status = ButtonStatus.UNPRESSED;
    }

    public void press() {
        this.status = ButtonStatus.PRESSED;
    }

    public void reset() {
        this.status = ButtonStatus.UNPRESSED;
    }

    public int getFloor() {
        return floor;
    }

    public ButtonStatus getStatus() {
        return status;
    }
}
