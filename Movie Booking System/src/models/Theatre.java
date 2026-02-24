package models;

import java.util.ArrayList;
import java.util.List;

public class Theatre {
    private final String id;
    private final String name;
    private final String city;
    private final List<Screen> screens;

    public Theatre(String id, String name, String city) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.screens = new ArrayList<>();
    }

    public void addScreen(Screen screen) {
        screens.add(screen);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public List<Screen> getScreens() {
        return new ArrayList<>(screens);
    }

    @Override
    public String toString() {
        return name + ", " + city + " (" + screens.size() + " screens)";
    }
}
