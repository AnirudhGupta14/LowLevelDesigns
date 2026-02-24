package services;

import models.Theatre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages theatres and provides query methods.
 */
public class TheatreManager {
    private final List<Theatre> theatres = new ArrayList<>();

    public void addTheatre(Theatre theatre) {
        theatres.add(theatre);
        System.out.println("🏛️ Theatre added: " + theatre);
    }

    public List<Theatre> getTheatresByCity(String city) {
        return theatres.stream()
                .filter(t -> t.getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }

    public List<Theatre> getAllTheatres() {
        return new ArrayList<>(theatres);
    }
}
