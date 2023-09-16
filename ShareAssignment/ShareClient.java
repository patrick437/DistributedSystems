import java.net.MalformedURLException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;


public class ShareClient
{
    
    private String token;   
    Scanner scan = new Scanner(System.in);

    //method which asks for username and password and checks server for correct credentials
    public static String loginImpl(ShareServer server) throws RemoteException
    {
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String userUsername = scan.nextLine();
        System.out.print("Enter password: ");
        String usersPassword = scan.nextLine();
        String token = server.login(userUsername, usersPassword);
        
        return token;
    }

    //method which checks if the token has been changed or not starting off
    public static boolean checkToken(String token)
    {
        if(token.equals("incorrect username"))
        {
            System.out.println("Incorrect log in credentials");
            return false;
        }
        else
        {
            System.out.println("Login succesful your token lasts 5 minutes");
            return true;
        }
    }
    
    //print all options the user has and return number to represent each option
    public String printAllOptionsOfTrading()
    {
        Scanner scan = new Scanner(System.in); 
        String str = "";
        str += "Welcome to this trading platform please enter the desired number to carry out transactions\n";
        str += "1. Deposit Funds\n";
        str += "2. Withdraw Funds\n";
        str += "3. Purchase a certain number of shares\n";
        str += "4. Sell a certain number of shares\n";
        str += "5. Print out a list of shares that you currently own\n";
        str += "6. Exit the trading platform";
        
        System.out.println( str );
        System.out.print("");
        String numberEntered = scan.nextLine();
        
        return numberEntered;

    }

    public String getToken()
    {
        return token;
    }

    public static void main (String args[]) throws AccessException, RemoteException, NotBoundException, MalformedURLException
    {
        //set token to this string value to begin with to make sure it gets changed
       String token = "Incorrect username";

        ShareClient client = new ShareClient();//create client
        Registry registry = LocateRegistry.getRegistry(9100); //find the correct port on the registry
        ShareServer server = (ShareServer)registry.lookup("serverStub");//find the server
        System.out.println("Welcome to Wallstreet enter Username and password to begin your experience.");
        


        boolean loggedIn = false;
        while(!loggedIn)//if logged in ever goes false you will be asked to log in again
        {
            token = loginImpl(server);
            loggedIn = checkToken(token);
        }

    
        System.out.println("token: "+ token);
        boolean userActive = true;

     while(userActive)//while userActive is true keep repeating the while loop
    {
        String numberEntered = client.printAllOptionsOfTrading();

        if(numberEntered.equals("1"))
        {
            Scanner scan = new Scanner(System.in);
            System.out.print("Please enter the amount you want to deposit into the account: ");
            double depositedAmount = scan.nextDouble();
            loggedIn = server.depositFunds( token, depositedAmount);
            
        }
        else if(numberEntered.equals("2"))
        {
            Scanner scan = new Scanner(System.in);
            System.out.print("Plese enter the funds you want to withdraw from ypur account");
            double withdrawAmount = scan.nextDouble();

            loggedIn = server.withdraw(token, withdrawAmount);
            
        }
        else if(numberEntered.equals("3"))
        {
            Scanner scan = new Scanner(System.in);
            System.out.print("Please enter the name of the shares you want to purchase: ");
            String nameOfShares = scan.next();
            System.out.print("Now please enter the nmber of shares you want to buy: ");
            int amountOfShares = scan.nextInt();
            
            loggedIn = server.buyShares(token, amountOfShares, nameOfShares);
            
        }
        else if(numberEntered.equals("4"))
        {
            Scanner scan = new Scanner(System.in);
            System.out.print("Please enter the name of the shares you want to withdraw: ");
            String shareName = scan.nextLine();
            System.out.print("Please enter the amount of these shares you wish to sell: ");
            int amountSold = scan.nextInt();
            

            loggedIn = server.sellShares(token, shareName, amountSold);
            
        }
        else if(numberEntered.equals("5"))
        {
            System.out.print("All the shares you currently own are: ");
            System.out.println(server.getUserShares(token));
        }
        else if(numberEntered.equals("6") )
        {
            System.out.println("Thank you for trading please return soon");
            userActive = false;
        }
        else
        {
            System.out.println("Incorrect number was entered please retry and enter one of the numbers shown above"); //if the incorrect number was entereed retry the process tp see if the correct number will then be entered
            client.printAllOptionsOfTrading();
        }

        while(!loggedIn)//if logged in ever goes false you will be asked to log in again
        {
            token = loginImpl(server);
            loggedIn = checkToken(token);
        }

    }
}
       
        
}

    
