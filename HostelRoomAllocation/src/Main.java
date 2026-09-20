import java.util.Scanner;

public class Main {
    private static final String ADMIN_USERNAME = "admin001";
    private static final String ADMIN_PASSWORD = "admin001";
    private static final int MIN_USER_ID_LENGTH = 7;
    private static final int MAX_USER_ID_LENGTH = 10;

    public static void main(String[] args) {
        HostelSystem hostelSystem = new HostelSystem();
        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.println("========================================");
            System.out.println("     HOSTEL ROOM ALLOCATION SYSTEM");
            System.out.println("========================================");
            System.out.println("1. Admin Login");
            System.out.println("2. User Login");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int loginChoice = getIntInput(input);

            if (loginChoice == 3) {
                System.out.println("Exiting the system. Goodbye!");
                input.close();
                return;
            }

            if (loginChoice == 1) {
                adminLogin(hostelSystem, input);
            } else if (loginChoice == 2) {
                userLogin(hostelSystem, input);
            } else {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }
        }
    }

    private static void adminLogin(HostelSystem hostelSystem, Scanner input) {
        System.out.print("Enter admin username: ");
        String username = input.nextLine();

        System.out.print("Enter admin password: ");
        String password = input.nextLine();

        if (!ADMIN_USERNAME.equals(username) || !ADMIN_PASSWORD.equals(password)) {
            System.out.println("Invalid admin login.");
            return;
        }

        System.out.println("Admin login successful.");
        adminMenu(hostelSystem, input);
    }

    private static void userLogin(HostelSystem hostelSystem, Scanner input) {
        System.out.print("Enter roll number (alphanumeric, 7 to 10 chars): ");
        String rollNumber = input.nextLine();

        if (!isValidUserId(rollNumber)) {
            System.out.println("Invalid user login. Roll number must be 7 to 10 characters with letters and digits only.");
            return;
        }

        System.out.println("User login successful.");
        userMenu(hostelSystem, input, rollNumber);
    }

    private static void adminMenu(HostelSystem hostelSystem, Scanner input) {
        while (true) {
            System.out.println("\n--- ADMIN MENU ---");
            System.out.println("1. Register Student");
            System.out.println("2. Check Room Availability");
            System.out.println("3. Allocate Room");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(input);

            switch (choice) {
                case 1:
                    System.out.print("Enter student ID: ");
                    int studentId = getIntInput(input);

                    System.out.print("Enter student name: ");
                    String studentName = input.nextLine();

                    System.out.print("Enter phone number: ");
                    String phone = input.nextLine();

                    System.out.print("Enter course: ");
                    String course = input.nextLine();

                    hostelSystem.registerStudent(studentId, studentName, phone, course);
                    break;

                case 2:
                    hostelSystem.showAvailableRooms();
                    break;

                case 3:
                    System.out.print("Enter student ID: ");
                    int allocateStudentId = getIntInput(input);

                    System.out.print("Enter room number: ");
                    int allocateRoomNumber = getIntInput(input);

                    hostelSystem.allocateRoom(allocateStudentId, allocateRoomNumber);
                    break;

                case 4:
                    return;

                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 4.");
                    break;
            }
        }
    }

    private static void userMenu(HostelSystem hostelSystem, Scanner input, String rollNumber) {
        while (true) {
            System.out.println("\n--- USER MENU ---");
            System.out.println("1. View Available Rooms");
            System.out.println("2. Pay and Allocate Room");
            System.out.println("3. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = getIntInput(input);

            switch (choice) {
                case 1:
                    hostelSystem.showAvailableRooms();
                    break;

                case 2:
                    hostelSystem.showAvailableRooms();

                    System.out.print("Enter student name: ");
                    String studentName = input.nextLine();

                    System.out.print("Enter phone number: ");
                    String phoneNumber = input.nextLine();

                    System.out.print("Enter course: ");
                    String course = input.nextLine();

                    int studentId = getStudentIdFromRollNumber(rollNumber);
                    hostelSystem.registerStudentIfNotExists(studentId, studentName, phoneNumber, course);

                    System.out.print("Enter room number: ");
                    int roomNumber = getIntInput(input);

                    Room room = hostelSystem.findRoom(roomNumber);
                    if (room == null) {
                        System.out.println("Room not found.");
                        break;
                    }

                    System.out.println("This room needs yearly payment of: 100000");
                    System.out.print("Enter yearly payment amount: ");
                    double paymentAmount = input.nextDouble();
                    input.nextLine();

                    hostelSystem.allocateRoomByUser(studentId, roomNumber, paymentAmount);
                    break;

                case 3:
                    return;

                default:
                    System.out.println("Invalid choice. Please enter 1, 2, or 3.");
                    break;
            }
        }
    }

    private static boolean isValidUserId(String userId) {
        if (userId == null) {
            return false;
        }

        int length = userId.length();
        if (length < MIN_USER_ID_LENGTH || length > MAX_USER_ID_LENGTH) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            char ch = userId.charAt(i);
            if (!Character.isLetterOrDigit(ch)) {
                return false;
            }
        }

        return true;
    }

    private static int getStudentIdFromRollNumber(String rollNumber) {
        return Math.abs(rollNumber.hashCode());
    }

    private static int getIntInput(Scanner input) {
        while (true) {
            if (!input.hasNext()) {
                System.out.println("No more input available. Exiting the system.");
                System.exit(0);
            }

            String rawValue = input.next();
            try {
                return Integer.parseInt(rawValue);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }
}
