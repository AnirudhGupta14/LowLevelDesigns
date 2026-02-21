package models;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    private String registrationNumber;
    private String color;
    private Integer price;
    private Integer manufacturingYear;
    private String carCompany;
    private List<BookingPeriod> bookings;

    public Vehicle(String registrationNumber, String color, Integer price, Integer manufacturingYear, String carCompany) {
        this.registrationNumber = registrationNumber;
        this.color = color;
        this.price = price;
        this.manufacturingYear = manufacturingYear;
        this.carCompany = carCompany;
        this.bookings = new ArrayList<>();
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getManufacturingYear() {
        return manufacturingYear;
    }

    public void setManufacturingYear(Integer manufacturingYear) {
        this.manufacturingYear = manufacturingYear;
    }

    public String getCarCompany() {
        return carCompany;
    }

    public void setCarCompany(String carCompany) {
        this.carCompany = carCompany;
    }

    public List<BookingPeriod> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingPeriod> bookings) {
        this.bookings = bookings;
    }
}
