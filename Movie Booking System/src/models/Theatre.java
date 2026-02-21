package models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class Theatre {
    private final String theatreId;
    private final String name;
    private final String address;
    private final List<Screen> screens;

    public Theatre(String name, String address) {
        this.theatreId = UUID.randomUUID().toString();
        this.name = name;
        this.address = address;
        this.screens = new ArrayList<>();
    }

    public void addScreen(Screen screen) {
        screens.add(screen);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Theatre theatre = (Theatre) obj;
        return theatreId.equals(theatre.theatreId);
    }

    @Override
    public int hashCode() {
        return theatreId.hashCode();
    }
}