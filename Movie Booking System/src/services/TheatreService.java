package services;

import enums.SeatCategory;
import models.Screen;
import models.Seat;
import models.Theatre;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TheatreService {

    private final Map<String, Theatre> theatres = new ConcurrentHashMap<>();
    private final Map<String, Screen> screens = new ConcurrentHashMap<>();
    private final Map<String, Seat> seats = new ConcurrentHashMap<>();

    public Theatre createTheatre(String name, String address) {
        Theatre theatre = new Theatre(name, address);
        theatres.put(theatre.getTheatreId(), theatre);
        return theatre;
    }

    public Screen createScreen(String name, Theatre theatre) {
        Screen screen = new Screen(name, theatre);
        screens.put(screen.getScreenId(), screen);
        theatre.addScreen(screen);
        return screen;
    }

    public Seat createSeat(int row, int column, SeatCategory category, Screen screen) {
        Seat seat = new Seat(row, column, category, screen);
        seats.put(seat.getSeatId(), seat);
        screen.addSeat(seat);
        return seat;
    }

    public Optional<Theatre> getTheatreById(String theatreId) {
        return Optional.ofNullable(theatres.get(theatreId));
    }

    public List<Screen> getScreensByTheatre(Theatre theatre) {
        return screens.values().stream()
                .filter(screen -> screen.getTheatre().equals(theatre))
                .collect(Collectors.toList());
    }

    public List<Seat> getSeatsByScreen(Screen screen) {
        return seats.values().stream()
                .filter(seat -> seat.getScreen().equals(screen))
                .collect(Collectors.toList());
    }

    public void updateTheatre(Theatre theatre) {
        theatres.put(theatre.getTheatreId(), theatre);
    }

    public void deleteTheatre(String theatreId) {
        theatres.remove(theatreId);
    }
}
