package services;

import models.Movie;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class MovieService {
    private final Map<String, Movie> movies = new ConcurrentHashMap<>();

    public Movie createMovie(String title, Duration duration, String genre,
                             String language, String certification, String description) {
        Movie movie = new Movie(title, duration, genre, language, certification, description);
        movies.put(movie.getMovieId(), movie);
        return movie;
    }

    public Optional<Movie> getMovieById(String movieId) {
        return Optional.ofNullable(movies.get(movieId));
    }

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies.values());
    }

    public List<Movie> searchMoviesByTitle(String title) {
        return movies.values().stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(title.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<Movie> searchMoviesByGenre(String genre) {
        return movies.values().stream()
                .filter(movie -> movie.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toList());
    }

    public List<Movie> searchMoviesByLanguage(String language) {
        return movies.values().stream()
                .filter(movie -> movie.getLanguage().equalsIgnoreCase(language))
                .collect(Collectors.toList());
    }

    public void updateMovie(Movie movie) {
        movies.put(movie.getMovieId(), movie);
    }

    public void deleteMovie(String movieId) {
        movies.remove(movieId);
    }
}
