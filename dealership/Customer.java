package car.dealership;

public class Customer {
    String name;
    String phone;

    public Customer(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public void display() {
        System.out.println("Customer: " + name + " | Phone: " + phone);
    }
}