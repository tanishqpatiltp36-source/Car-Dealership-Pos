package car.dealership;

public class Car {
    String model;
    double price;
    int qty;

    //constructor
    public Car(String model, double price, int qty){
        this.model = model;
        this.price = price;
        this.qty = qty;
    }
    public void display(){
        System.out.println("Model:"+ model +"|Price:"+ price +"|Qty:"+qty);
    }
}
