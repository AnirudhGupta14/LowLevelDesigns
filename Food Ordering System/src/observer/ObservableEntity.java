package observer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ObservableEntity implements Observable {
    private final List<Observer> observers;

    public ObservableEntity() {
        this.observers = new CopyOnWriteArrayList<>();
    }

    @Override
    public void addObserver(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String eventType, Object data) {
        for (Observer observer : observers) {
            try {
                notifyObserver(observer, eventType, data);
            } catch (Exception e) {
                // Log error but continue with other observers
                System.err.println("Error notifying observer: " + e.getMessage());
            }
        }
    }

    @Override
    public List<Observer> getObservers() {
        return new ArrayList<>(observers);
    }

    protected abstract void notifyObserver(Observer observer, String eventType, Object data);

    public void clearObservers() {
        observers.clear();
    }

    public int getObserverCount() {
        return observers.size();
    }
}

