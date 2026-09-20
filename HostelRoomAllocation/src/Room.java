public class Room {
    private int roomNumber;
    private int capacity;
    private int occupied;
    private double yearlyFee;

    public Room(int roomNumber, int capacity, double yearlyFee) {
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.yearlyFee = yearlyFee;
        this.occupied = 0;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getOccupied() {
        return occupied;
    }

    public double getYearlyFee() {
        return yearlyFee;
    }

    public void setOccupied(int occupied) {
        this.occupied = occupied;
    }

    public int getAvailableSpaces() {
        return capacity - occupied;
    }

    public boolean isAvailable() {
        return occupied < capacity;
    }
}
