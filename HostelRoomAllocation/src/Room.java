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
        if (occupied >= 0 && occupied <= capacity) {
            this.occupied = occupied;
        }
    }

    public boolean occupyPlace() {
        if (!isAvailable()) {
            return false;
        }
        occupied++;
        return true;
    }

    public void vacatePlace() {
        if (occupied > 0) {
            occupied--;
        }
    }

    public int getAvailableSpaces() {
        return capacity - occupied;
    }

    public boolean isAvailable() {
        return occupied < capacity;
    }

    public int getFloor() {
        return (roomNumber - 101) / 20 + 1;
    }
}
