import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ShareServer extends Remote{

   //all methods required to make the share server
   boolean depositFunds(String token, double anAmount)throws RemoteException;
   boolean withdraw(String token, double wathdrawAmount)throws RemoteException;
   String login(String userUsername, String userPassword)throws RemoteException;
   boolean buyShares(String token, int amountShares, String name )throws RemoteException;
   boolean sellShares(String token, String name, int quantity)throws RemoteException;
   List<ShareHoldingImpl> getUserShares(String userToken) throws RemoteException;
    
}
