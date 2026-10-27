public class Student {
    private String studentId;
    private String name;
    private String phoneNumber;
    private String course;

    public Student(String studentId, String name, String phoneNumber, String course) {
        this.studentId = studentId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.course = course;
    }

    public String getStudentId() {
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
