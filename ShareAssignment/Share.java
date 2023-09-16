import java.rmi.Remote;
//create a share interface
public interface Share extends Remote{

    //All required methods to use as a share
    double getPrice();
    String getName();
    int getTimeRemaining();
    double increaseSharePrice();
    double decreaseSharePrice();


}
