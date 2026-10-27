import java.util.Scanner;

public class Main {
    private static final String ADMIN_USERNAME = "admin001";
    private static final String ADMIN_PASSWORD = "admin001";
    private static final int MIN_USER_ID_LENGTH = 7;
    private static final int MAX_USER_ID_LENGTH = 10;

    public static void main(String[] args) {
        HostelSystem hostelSystem = new HostelSystem();
        Scanner input = new Scanner(System.in);
        try {
            while (true) {
                System.out.println("\n=== HOSTEL ROOM ALLOCATION SYSTEM ===");
                System.out.println("1. Admin Login");
                System.out.println("2. User Login");
                System.out.println("3. Exit");
                int choice = getIntInput(input, "Enter your choice: ");
                if (choice == 1) {
                    adminLogin(hostelSystem, input);
                } else if (choice == 2) {
                    userLogin(hostelSystem, input);
                } else if (choice == 3) {
                    System.out.println("Exiting the system. Goodbye!");
                    return;
                } else {
                    System.out.println("Choose 1, 2, or 3.");
                }
            }
        } catch (EndOfInputException exception) {
            System.out.println("Input ended. Exiting the system.");
        } finally {
            input.close();
        }
    }

    private static void adminLogin(HostelSystem hostelSystem, Scanner input) {
        String username = readText(input, "Enter admin username: ");
        String password = readText(input, "Enter admin password: ");
        if (!ADMIN_USERNAME.equals(username) || !ADMIN_PASSWORD.equals(password)) {
            System.out.println("Invalid admin login.");
            return;
        }
        System.out.println("Admin login successful.");
        adminMenu(hostelSystem, input);
    }

    private static void userLogin(HostelSystem hostelSystem, Scanner input) {
        String rollNumber = readText(input, "Enter roll number (7 to 10 letters or digits): ");
        if (!isValidUserId(rollNumber)) {
            System.out.println("Invalid roll number. Use 7 to 10 letters and digits only.");
            return;
        }
        System.out.println("User login successful.");
        userMenu(hostelSystem, input, rollNumber);
    }

    private static void adminMenu(HostelSystem hostelSystem, Scanner input) {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Register student");
            System.out.println("2. View floor availability");
            System.out.println("3. Allocate room");
            System.out.println("4. Transfer student");
            System.out.println("5. Vacate room");
            System.out.println("6. Search student");
            System.out.println("7. Search room");
            System.out.println("8. Occupancy report");
            System.out.println("9. View pending requests");
            System.out.println("10. Back");
            int choice = getIntInput(input, "Enter your choice: ");
            switch (choice) {
                case 1:
                    registerStudent(hostelSystem, input);
                    break;
                case 2:
                    showFloor(hostelSystem, input);
                    break;
                case 3:
                    allocateStudent(hostelSystem, input);
                    break;
                case 4:
                    String transferId = readText(input, "Enter student ID or roll number: ");
                    int newRoom = getIntInput(input, "Enter destination room number: ");
                    hostelSystem.transferStudent(transferId, newRoom);
                    break;
                case 5:
                    hostelSystem.vacateRoom(readText(input, "Enter student ID or roll number: "));
                    break;
                case 6:
                    hostelSystem.searchStudent(readText(input, "Enter student ID or roll number: "));
                    break;
                case 7:
                    hostelSystem.searchRoom(getIntInput(input, "Enter room number: "));
                    break;
                case 8:
                    hostelSystem.showOccupancyReport();
                    break;
                case 9:
                    hostelSystem.showPendingRequests();
                    break;
                case 10:
                    return;
                default:
                    System.out.println("Choose a number from 1 to 10.");
            }
        }
    }

    private static void userMenu(HostelSystem hostelSystem, Scanner input, String rollNumber) {
        while (true) {
            System.out.println("\n--- USER MENU ---");
            System.out.println("1. View floor availability");
            System.out.println("2. Register and request a room");
            System.out.println("3. View my details and allocation");
            System.out.println("4. Back");
            int choice = getIntInput(input, "Enter your choice: ");
            switch (choice) {
                case 1:
                    showFloor(hostelSystem, input);
                    break;
                case 2:
                    Student student = hostelSystem.findStudent(rollNumber);
                    if (student == null) {
                        String name = readText(input, "Enter your name: ");
                        String phone = readText(input, "Enter phone number: ");
                        String course = readText(input, "Enter course: ");
                        if (!hostelSystem.registerStudent(rollNumber, name, phone, course)) {
                            break;
                        }
                    }
                    showFloor(hostelSystem, input);
                    int roomNumber = getIntInput(input, "Enter preferred room number: ");
                    hostelSystem.allocateRoom(rollNumber, roomNumber);
                    break;
                case 3:
                    hostelSystem.searchStudent(rollNumber);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Choose a number from 1 to 4.");
            }
        }
    }

    private static void registerStudent(HostelSystem hostelSystem, Scanner input) {
        String studentId = readText(input, "Enter student ID or roll number: ");
        String name = readText(input, "Enter student name: ");
        String phone = readText(input, "Enter phone number: ");
        String course = readText(input, "Enter course: ");
        hostelSystem.registerStudent(studentId, name, phone, course);
    }

    private static void allocateStudent(HostelSystem hostelSystem, Scanner input) {
        String studentId = readText(input, "Enter student ID or roll number: ");
        int roomNumber = getIntInput(input, "Enter room number: ");
        hostelSystem.allocateRoom(studentId, roomNumber);
    }

    private static void showFloor(HostelSystem hostelSystem, Scanner input) {
        int floor = getIntInput(input, "Enter floor (1-5, or 0 for all floors): ");
        if (floor == 0) {
            hostelSystem.showAvailableRooms();
        } else {
            hostelSystem.showFloorAvailability(floor);
        }
    }

    private static boolean isValidUserId(String userId) {
        if (userId == null || userId.length() < MIN_USER_ID_LENGTH
                || userId.length() > MAX_USER_ID_LENGTH) {
            return false;
        }
        for (int index = 0; index < userId.length(); index++) {
            if (!Character.isLetterOrDigit(userId.charAt(index))) {
                return false;
            }
        }
        return true;
    }

    private static String readText(Scanner input, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!input.hasNextLine()) {
                throw new EndOfInputException();
            }
            String value = input.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("Input cannot be empty.");
        }
    }

    private static int getIntInput(Scanner input, String prompt) {
        while (true) {
            String rawValue = readText(input, prompt);
            try {
                return Integer.parseInt(rawValue);
            } catch (NumberFormatException exception) {
                System.out.println("Invalid input. Enter a whole number.");
            }
        }
    }

    private static class EndOfInputException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }
}
