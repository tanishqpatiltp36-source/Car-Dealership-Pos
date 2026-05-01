package car.dealership;

public class Staff {
    String name;
    String role;
    double salary;

    public Staff(String name, String role, double salary) {
        this.name = name;
        this.role = role;
        this.salary = salary;
    }

    public void display() {
        System.out.println("Staff: " + name + " | Role: " + role + " | Salary: Rs." + salary);
    }
}