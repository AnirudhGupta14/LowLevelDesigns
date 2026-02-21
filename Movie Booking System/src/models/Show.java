package models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class Show {
    private final String showId;
    private final Movie movie;
    private final Screen screen;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public Show(Movie movie, Screen screen, LocalDateTime startTime) {
        this.showId = UUID.randomUUID().toString();
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
        this.endTime = startTime.plus(movie.getDuration());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Show show = (Show) obj;
        return showId.equals(show.showId);
    }

    @Override
    public int hashCode() {
        return showId.hashCode();
    }
}