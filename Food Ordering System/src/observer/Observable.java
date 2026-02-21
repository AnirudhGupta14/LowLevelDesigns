package observer;

import java.util.List;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String eventType, Object data);
    List<Observer> getObservers();
}

