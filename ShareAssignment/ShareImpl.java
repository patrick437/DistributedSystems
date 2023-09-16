import java.io.Serializable;
import java.rmi.Remote;

public class ShareImpl implements Share, Serializable{

    //create required variables to describe a share
    private String name;
    private double price;
    private int quantity;
    private int timeRemaining;

    public ShareImpl(String name, double price,int quantity, int timeRemaining)
    {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.timeRemaining = timeRemaining;
    }

    @Override
    public double getPrice() {
        return price;
    }

    
    @Override
    public String getName() {
        return name;
    }

    //method which increases the share price by 5%
    public double increaseSharePrice()
    {
        price += price*.05;
        return price;
    }

    //method that decreases the share price by 5%
    public double decreaseSharePrice()
    {
        price -= price*.05;
        return price;
    }

    @Override
    public int getTimeRemaining() {
        return timeRemaining;
    }

    //Method which reduces the volume of the shares in the market
    public void reduceVolume(int amountToBeReduced)
    {
        quantity -= amountToBeReduced;
    }

    public void increaseVolume(int amountToBeIncreased)
    {
        quantity += amountToBeIncreased;
    }

    //to string method which will be used to print ut each share in the user array
    @Override
    public String toString() {
        return "ShareImpl [name=" + name + ", price=" + price + ", quantity=" + quantity + ", timeRemaining="
                + timeRemaining + "]";
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }


}