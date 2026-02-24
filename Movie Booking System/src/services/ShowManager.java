package services;

import models.Movie;
import models.Show;
import models.Theatre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages all shows across theatres and screens.
 */
public class ShowManager {
    private final List<Show> shows = new ArrayList<>();

    public void addShow(Show show) {
        shows.add(show);
        System.out.println("🎬 Show added: " + show);
    }

    public List<Show> getShowsForMovie(Movie movie) {
        return shows.stream()
                .filter(s -> s.getMovie().getId().equals(movie.getId()))
                .collect(Collectors.toList());
    }

    public List<Show> getShowsInTheatre(Theatre theatre) {
        return shows.stream()
                .filter(s -> theatre.getScreens().stream()
                        .anyMatch(screen -> screen.getId().equals(s.getScreen().getId())))
                .collect(Collectors.toList());
    }

    public List<Show> getAllShows() {
        return new ArrayList<>(shows);
    }
}
