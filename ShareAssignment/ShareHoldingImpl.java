import java.io.Serializable;

public class ShareHoldingImpl implements ShareHolding, Serializable{
    
    private String name;
    private int quantity;
    private double price;
    //create constructor
    public ShareHoldingImpl(String name, int quantity, double price)
    {
        super();
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    
    //various getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    //to string used to print out the list
    @Override
    public String toString() {
        return "ShareHoldingImpl [name=" + name + ", quantity=" + quantity + ", price=" + price + "]";
    }
    

}
