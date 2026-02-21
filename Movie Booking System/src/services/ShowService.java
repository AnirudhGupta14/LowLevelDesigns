package services;

import models.Movie;
import models.Screen;
import models.Show;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ShowService {
    private final Map<String, Show> shows = new ConcurrentHashMap<>();

    public Show createShow(Movie movie, Screen screen, LocalDateTime startTime) {
        if (!isShowTimeAvailable(screen, startTime, movie.getDuration())) {
            throw new IllegalArgumentException("Show time conflicts with existing shows");
        }

        Show show = new Show(movie, screen, startTime);
        shows.put(show.getShowId(), show);
        return show;
    }

    public List<Show> getAllShows() {
        return (List<Show>) shows.values();
    }

    public Optional<Show> getShowById(String showId) {
        return Optional.ofNullable(shows.get(showId));
    }

    public List<Show> getShowsByMovie(Movie movie) {
        return shows.values().stream()
                .filter(show -> show.getMovie().equals(movie))
                .collect(Collectors.toList());
    }

    public List<Show> getShowsByScreen(Screen screen) {
        return shows.values().stream()
                .filter(show -> show.getScreen().equals(screen))
                .collect(Collectors.toList());
    }

    public List<Show> getShowsByTheatreAndDate(String theatreId, LocalDateTime date) {
        return shows.values().stream()
                .filter(show -> show.getScreen().getTheatre().getTheatreId().equals(theatreId))
                .filter(show -> show.getStartTime().toLocalDate().equals(date.toLocalDate()))
                .collect(Collectors.toList());
    }

    public boolean isShowTimeAvailable(Screen screen, LocalDateTime startTime, java.time.Duration duration) {
        LocalDateTime endTime = startTime.plus(duration);

        return shows.values().stream()
                .filter(show -> show.getScreen().equals(screen))
                .noneMatch(show -> {
                    LocalDateTime existingStart = show.getStartTime();
                    LocalDateTime existingEnd = show.getEndTime();

                    // Check for overlap
                    return (startTime.isBefore(existingEnd) && endTime.isAfter(existingStart));
                });
    }

    public void updateShow(Show show) {
        shows.put(show.getShowId(), show);
    }

    public void deleteShow(String showId) {
        shows.remove(showId);
    }
}