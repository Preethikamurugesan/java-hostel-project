import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class HostelSystem {
    private static final int ROOM_CAPACITY = 3;
    private static final int FIRST_ROOM = 101;
    private static final int LAST_ROOM = 200;
    private static final Path DATA_FILE = Paths.get("hostel-data.txt");

    private final ArrayList<Room> rooms = new ArrayList<>();
    private final ArrayList<Student> students = new ArrayList<>();
    private final HashMap<String, Integer> studentRoom = new HashMap<>();
    private final HashSet<Integer> occupiedRooms = new HashSet<>();
    private final Queue<RoomRequest> pendingRequests = new LinkedList<>();

    public HostelSystem() {
        loadData();
        if (rooms.isEmpty()) {
            initializeRooms();
        }
        rebuildOccupancy();
        System.out.println(rooms.size() + " rooms are ready across five floors.");
    }

    private void initializeRooms() {
        for (int roomNumber = FIRST_ROOM; roomNumber <= LAST_ROOM; roomNumber++) {
            rooms.add(new Room(roomNumber, ROOM_CAPACITY, 100000.0));
        }
    }

    private void rebuildOccupancy() {
        occupiedRooms.clear();
        for (Room room : rooms) {
            room.setOccupied(0);
        }
        for (Integer roomNumber : studentRoom.values()) {
            Room room = findRoom(roomNumber);
            if (room != null && room.occupyPlace()) {
                occupiedRooms.add(roomNumber);
            }
        }
    }

    public boolean registerStudent(String studentId, String name, String phoneNumber, String course) {
        if (studentId == null || studentId.trim().isEmpty() || findStudent(studentId) != null) {
            System.out.println("Student ID is empty or already registered.");
            return false;
        }
        students.add(new Student(studentId.trim(), name.trim(), phoneNumber.trim(), course.trim()));
        saveData();
        System.out.println("Student registered successfully.");
        return true;
    }

    public Student findStudent(String studentId) {
        for (Student student : students) {
            if (student.getStudentId().equalsIgnoreCase(studentId)) {
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

    public void checkRoomAvailability(int roomNumber) {
        Room room = findRoom(roomNumber);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }
        System.out.println("Floor: " + room.getFloor());
        System.out.println("Room Number: " + room.getRoomNumber());
        System.out.println("Capacity: " + room.getCapacity());
        System.out.println("Occupied: " + room.getOccupied());
        System.out.println("Vacant: " + room.getAvailableSpaces());
        System.out.println("Status: " + (room.isAvailable() ? "Available" : "Full"));
    }

    public void showFloorAvailability(int floor) {
        if (floor < 1 || floor > 5) {
            System.out.println("Floor must be between 1 and 5.");
            return;
        }
        System.out.println("\nFloor " + floor + " Room Availability");
        System.out.println("Room No. | Capacity | Occupied | Vacant");
        for (Room room : rooms) {
            if (room.getFloor() == floor) {
                System.out.println(room.getRoomNumber() + " | " + room.getCapacity() + " | "
                        + room.getOccupied() + " | " + room.getAvailableSpaces());
            }
        }
    }

    public void showAvailableRooms() {
        for (int floor = 1; floor <= 5; floor++) {
            showFloorAvailability(floor);
        }
    }

    public void allocateRoom(String studentId, int roomNumber) {
        Student student = findStudent(studentId);
        Room room = findRoom(roomNumber);
        if (student == null) {
            System.out.println("Student not found. Register the student first.");
            return;
        }
        if (studentRoom.containsKey(student.getStudentId())) {
            System.out.println("Student is already allocated to room " + studentRoom.get(student.getStudentId()) + ".");
            return;
        }
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }
        if (!room.isAvailable()) {
            Room alternativeRoom = firstAvailableRoom();
            if (alternativeRoom == null) {
                enqueueRequest(student.getStudentId(), roomNumber);
                System.out.println("No room is available. The request has been added to the pending queue.");
                return;
            }
            room = alternativeRoom;
            System.out.println("Preferred room is full; using the first available room.");
        }
        assignStudent(student.getStudentId(), room);
        System.out.println("Student " + student.getStudentId() + " allocated to room "
            + room.getRoomNumber() + ".");
    }

    private void assignStudent(String studentId, Room room) {
        if (room.occupyPlace()) {
            studentRoom.put(studentId, room.getRoomNumber());
            occupiedRooms.add(room.getRoomNumber());
            saveData();
        }
    }

    private void enqueueRequest(String studentId, int preferredRoom) {
        for (RoomRequest request : pendingRequests) {
            if (request.studentId.equalsIgnoreCase(studentId)) {
                return;
            }
        }
        pendingRequests.offer(new RoomRequest(studentId, preferredRoom));
        saveData();
    }

    public void transferStudent(String studentId, int newRoomNumber) {
        Student student = findStudent(studentId);
        Integer currentRoomNumber = studentRoom.get(studentId);
        Room newRoom = findRoom(newRoomNumber);
        if (student == null || currentRoomNumber == null) {
            System.out.println("Student is not registered or has no current room allocation.");
            return;
        }
        if (newRoom == null) {
            System.out.println("Room not found.");
            return;
        }
        if (currentRoomNumber == newRoomNumber) {
            System.out.println("Student is already in that room.");
            return;
        }
        if (!newRoom.isAvailable()) {
            System.out.println("Destination room is full. Transfer was not made.");
            return;
        }
        Room currentRoom = findRoom(currentRoomNumber);
        currentRoom.vacatePlace();
        if (currentRoom.getOccupied() > 0) {
            occupiedRooms.add(currentRoomNumber);
        } else {
            occupiedRooms.remove(currentRoomNumber);
        }
        studentRoom.remove(student.getStudentId());
        assignStudent(student.getStudentId(), newRoom);
        processPendingRequests();
        saveData();
        System.out.println("Student transferred to room " + newRoomNumber + ".");
    }

    public void vacateRoom(String studentId) {
        Student student = findStudent(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        Integer roomNumber = studentRoom.remove(student.getStudentId());
        if (roomNumber == null) {
            System.out.println("Student does not have an allocated room.");
            return;
        }
        Room room = findRoom(roomNumber);
        if (room != null) {
            room.vacatePlace();
            if (room.getOccupied() == 0) {
                occupiedRooms.remove(roomNumber);
            } else {
                occupiedRooms.add(roomNumber);
            }
        }
        System.out.println("Room " + roomNumber + " vacated by " + student.getStudentId() + ".");
        processPendingRequests();
        saveData();
    }

    private void processPendingRequests() {
        while (!pendingRequests.isEmpty()) {
            RoomRequest request = pendingRequests.peek();
            Student student = findStudent(request.studentId);
            if (student == null || studentRoom.containsKey(request.studentId)) {
                pendingRequests.poll();
                continue;
            }
            Room room = findRoom(request.preferredRoom);
            if (room == null || !room.isAvailable()) {
                room = firstAvailableRoom();
            }
            if (room == null) {
                break;
            }
            pendingRequests.poll();
            assignStudent(request.studentId, room);
            System.out.println("Pending request served: " + request.studentId + " allocated to room "
                    + room.getRoomNumber() + ".");
        }
        saveData();
    }

    private Room firstAvailableRoom() {
        for (Room room : rooms) {
            if (room.isAvailable()) {
                return room;
            }
        }
        return null;
    }

    public void showPendingRequests() {
        if (pendingRequests.isEmpty()) {
            System.out.println("There are no pending room requests.");
            return;
        }
        System.out.println("Pending requests (first in, first out):");
        int position = 1;
        for (RoomRequest request : pendingRequests) {
            System.out.println(position++ + ". Student " + request.studentId
                    + ", preferred room " + request.preferredRoom);
        }
    }

    public void searchStudent(String studentId) {
        Student student = findStudent(studentId);
        if (student == null) {
            System.out.println("Student not found.");
            return;
        }
        System.out.println("Student ID: " + student.getStudentId());
        System.out.println("Name: " + student.getName());
        System.out.println("Phone: " + student.getPhoneNumber());
        System.out.println("Course: " + student.getCourse());
        Integer roomNumber = studentRoom.get(student.getStudentId());
        System.out.println("Room: " + (roomNumber == null ? "Not allocated" : roomNumber));
    }

    public void searchRoom(int roomNumber) {
        Room room = findRoom(roomNumber);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }
        checkRoomAvailability(roomNumber);
        System.out.println("Students in room:");
        boolean hasStudents = false;
        for (Student student : students) {
            if (Integer.valueOf(roomNumber).equals(studentRoom.get(student.getStudentId()))) {
                System.out.println(student.getStudentId() + " | " + student.getName()
                        + " | " + student.getCourse());
                hasStudents = true;
            }
        }
        if (!hasStudents) {
            System.out.println("None");
        }
    }

    public void showOccupancyReport() {
        int totalCapacity = 0;
        int totalOccupied = 0;
        System.out.println("\nHostel Occupancy Report");
        System.out.println("Room | Floor | Capacity | Occupied | Vacant");
        for (Room room : rooms) {
            totalCapacity += room.getCapacity();
            totalOccupied += room.getOccupied();
            System.out.println(room.getRoomNumber() + " | " + room.getFloor() + " | "
                    + room.getCapacity() + " | " + room.getOccupied() + " | "
                    + room.getAvailableSpaces());
        }
        System.out.println("Total rooms: " + rooms.size());
        System.out.println("Total capacity: " + totalCapacity);
        System.out.println("Occupied places: " + totalOccupied);
        System.out.println("Vacant places: " + (totalCapacity - totalOccupied));
        System.out.println("Unique occupied rooms: " + occupiedRooms.size());
        System.out.println("Pending requests: " + pendingRequests.size());
    }

    private void loadData() {
        if (!Files.exists(DATA_FILE)) {
            return;
        }
        try (BufferedReader reader = Files.newBufferedReader(DATA_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\\|", -1);
                try {
                    if (fields[0].equals("ROOM") && fields.length == 4) {
                        rooms.add(new Room(Integer.parseInt(fields[1]), Integer.parseInt(fields[2]),
                                Double.parseDouble(fields[3])));
                    } else if (fields[0].equals("STUDENT") && fields.length == 5) {
                        students.add(new Student(fields[1], fields[2], fields[3], fields[4]));
                    } else if (fields[0].equals("ALLOCATION") && fields.length == 3) {
                        studentRoom.put(fields[1], Integer.parseInt(fields[2]));
                    } else if (fields[0].equals("REQUEST") && fields.length == 3) {
                        pendingRequests.offer(new RoomRequest(fields[1], Integer.parseInt(fields[2])));
                    }
                } catch (NumberFormatException exception) {
                    System.out.println("Skipped an invalid saved record: " + line);
                }
            }
        } catch (IOException exception) {
            System.out.println("Could not load saved data: " + exception.getMessage());
        }
    }

    private void saveData() {
        try (BufferedWriter writer = Files.newBufferedWriter(DATA_FILE)) {
            for (Room room : rooms) {
                writer.write("ROOM|" + room.getRoomNumber() + "|" + room.getCapacity() + "|" + room.getYearlyFee());
                writer.newLine();
            }
            for (Student student : students) {
                writer.write("STUDENT|" + clean(student.getStudentId()) + "|" + clean(student.getName())
                        + "|" + clean(student.getPhoneNumber()) + "|" + clean(student.getCourse()));
                writer.newLine();
            }
            for (String studentId : studentRoom.keySet()) {
                writer.write("ALLOCATION|" + clean(studentId) + "|" + studentRoom.get(studentId));
                writer.newLine();
            }
            for (RoomRequest request : pendingRequests) {
                writer.write("REQUEST|" + clean(request.studentId) + "|" + request.preferredRoom);
                writer.newLine();
            }
        } catch (IOException exception) {
            System.out.println("Could not save hostel data: " + exception.getMessage());
        }
    }

    private String clean(String value) {
        return value.replace('|', ' ').replace('\n', ' ').replace('\r', ' ');
    }

    private static class RoomRequest {
        private final String studentId;
        private final int preferredRoom;

        private RoomRequest(String studentId, int preferredRoom) {
            this.studentId = studentId;
            this.preferredRoom = preferredRoom;
        }
    }
}
