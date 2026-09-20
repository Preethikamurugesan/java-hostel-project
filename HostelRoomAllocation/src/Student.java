public class Student {
    private int studentId;
    private String name;
    private String phoneNumber;
    private String course;

    public Student(int studentId, String name, String phoneNumber, String course) {
        this.studentId = studentId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.course = course;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCourse() {
        return course;
    }
}
