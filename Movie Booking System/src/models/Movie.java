package models;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.UUID;

@Getter
@Setter
public class Movie {
    private final String movieId;
    private final String title;
    private final Duration duration;
    private final String genre;
    private final String language;
    private final String certification;
    private final String description;

    public Movie(String title, Duration duration, String genre, String language,
                 String certification, String description) {
        this.movieId = UUID.randomUUID().toString();
        this.title = title;
        this.duration = duration;
        this.genre = genre;
        this.language = language;
        this.certification = certification;
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Movie movie = (Movie) obj;
        return movieId.equals(movie.movieId);
    }

    @Override
    public int hashCode() {
        return movieId.hashCode();
    }
}