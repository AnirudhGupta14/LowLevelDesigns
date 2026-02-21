package Services;

public class Admin {
    String name;

    public Admin(String name) {
        this.name = name;
    }

    public void addEntrance(ParkingLot lot, Entrance entrance) {
        lot.entrances.add(entrance);
        System.out.println("Entrance added by Admin: " + entrance.id);
    }

    public void removeEntrance(ParkingLot lot, Entrance entrance) {
        lot.entrances.remove(entrance);
        System.out.println("Entrance removed by Admin: " + entrance.id);
    }

    public void addExit(ParkingLot lot, Exit exit) {
        lot.exits.add(exit);
        System.out.println("Exit added by Admin: " + exit.id);
    }

    public void removeExit(ParkingLot lot, Exit exit) {
        lot.exits.remove(exit);
        System.out.println("Exit removed by Admin: " + exit.id);
    }

    public void addFloor(ParkingLot lot, Floor floor) {
        lot.floors.add(floor);
        System.out.println("Floor " + floor.number + " added by Admin.");
    }
}