import java.util.ArrayList;
import java.util.HashMap;

public class HostelSystem {
    private ArrayList<Room> rooms;
    private ArrayList<Student> students;
    private HashMap<Integer, Integer> studentRoom;

    public HostelSystem() {
        this.rooms = new ArrayList<>();
        this.students = new ArrayList<>();
        this.studentRoom = new HashMap<>();
        initializeRooms();
    }

    private void initializeRooms() {
        for (int roomNumber = 101; roomNumber <= 300; roomNumber++) {
            rooms.add(new Room(roomNumber, 3, 100000.0));
        }
        System.out.println("200 rooms were initialized successfully.");
    }

    public void addRoom(int roomNumber, int capacity, double yearlyFee) {
        Room room = new Room(roomNumber, capacity, yearlyFee);
        rooms.add(room);
        System.out.println("Room added successfully.");
    }

    public void registerStudent(int studentId, String name, String phoneNumber, String course) {
        Student student = new Student(studentId, name, phoneNumber, course);
        students.add(student);
        System.out.println("Student registered successfully.");
    }

    public void registerStudentIfNotExists(int studentId, String name, String phoneNumber, String course) {
        if (findStudent(studentId) == null) {
            registerStudent(studentId, name, phoneNumber, course);
        }
    }

    public void checkRoomAvailability(int roomNumber) {
        Room room = findRoom(roomNumber);

        if (room == null) {
            System.out.println("Room not found.");
            return;
        }

        int availableSpaces = room.getAvailableSpaces();

        System.out.println("Room Number: " + room.getRoomNumber());
        System.out.println("Capacity: " + room.getCapacity());
        System.out.println("Occupied: " + room.getOccupied());
        System.out.println("Available Spaces: " + availableSpaces);
        System.out.println("Yearly Fee: 100000");

        if (room.isAvailable()) {
            System.out.println("Status: Available");
        } else {
            System.out.println("Status: Full");
        }
    }

    public void showAvailableRooms() {
        if (rooms.isEmpty()) {
            System.out.println("No rooms added yet.");
            return;
        }

        int totalRooms = rooms.size();
        int availableRooms = 0;

        System.out.println("\nRoom Availability Summary");
        System.out.println("Total Rooms: " + totalRooms);
        System.out.println("Room No. | Capacity | Occupied | Vacant");

        for (Room room : rooms) {
            if (room.isAvailable()) {
                availableRooms++;
            }

            System.out.println(room.getRoomNumber() + " | "
                    + room.getCapacity() + " | "
                    + room.getOccupied() + " | "
                    + room.getAvailableSpaces());
        }

        System.out.println("Available Rooms: " + availableRooms);
    }

    public void allocateRoom(int studentId, int roomNumber) {
        Student student = findStudent(studentId);
        Room room = findRoom(roomNumber);

        if (student == null) {
            System.out.println("Student not found.");
            return;
        }

        if (room == null) {
            System.out.println("Room not found.");
            return;
        }

        if (!room.isAvailable()) {
            System.out.println("Room is full.");
            return;
        }

        room.setOccupied(room.getOccupied() + 1);
        studentRoom.put(studentId, roomNumber);

        System.out.println("Room allocated successfully.");
    }

    public void allocateRoomByUser(int studentId, int roomNumber, double paymentAmount) {
        Student student = findStudent(studentId);
        Room room = findRoom(roomNumber);

        if (student == null) {
            System.out.println("Student not found. Please register first.");
            return;
        }

        if (room == null) {
            System.out.println("Room not found.");
            return;
        }

        if (!room.isAvailable()) {
            System.out.println("Room is full.");
            return;
        }

        if (paymentAmount < room.getYearlyFee()) {
            System.out.println("Payment is insufficient. Please pay at least " + room.getYearlyFee());
            return;
        }

        room.setOccupied(room.getOccupied() + 1);
        studentRoom.put(studentId, roomNumber);
        System.out.println("Payment received successfully.");
        System.out.println("Room allocated successfully.");
    }

    public Student findStudent(int studentId) {
        for (Student student : students) {
            if (student.getStudentId() == studentId) {
                return student;
            }
        }
        return null;
    }

    public Room findRoom(int roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber() == roomNumber) {
                return room;
            }
        }
        return null;
    }
}
