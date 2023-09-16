import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.*;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.lang.model.util.ElementScanner14;

public class ShareServerImpl implements ShareServer {

    Scanner scan = new Scanner(System.in);
    Scanner scanner = new Scanner(System.in);
    private String username;
    private String password;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    private List<ShareImpl> allShares = new ArrayList<ShareImpl>();
    private static List<ShareHoldingImpl> userShares = new ArrayList<ShareHoldingImpl>();
    private double accountBalance;
    private String currentToken = "", userToken = "";

    //all variables required to create a share server
    ShareServerImpl(String username, String password, List<ShareImpl> allShares ) throws RemoteException
    {
        super();
        this.username = username;
        this.password = password;
        this.allShares = allShares;
        accountBalance = 0;//set the account balance to zero at start of trading

    }

    //log in method if you have enterred correct username log in is succesful and token is generated
    public String login(String userUsername, String userPassword)throws RemoteException{

        if(userUsername.equals(username) && userPassword.equals(password))
        {
            tokenGenerator();
            System.out.println("Log in succesful");
            userToken = currentToken;
            return userToken;
        }
        else{
            System.out.println("You have entered the wrong login credentials please retry");
            return "Incorrect login";
        }
    }

    //method which creates a random token
    public String generateToken(){
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        currentToken = base64Encoder.encodeToString(randomBytes);
        return currentToken;
    }

    //method which sets a timer to call a class every 5 minues whuch resets the token
    public void tokenGenerator()
    {
        Timer timer = new Timer();
        timer.schedule(new RegenerateTask(), 0 ,5*60*1000);
        generateToken();

    }

    private String getToken()
    {
        return currentToken;
    }
    //class which regenerates the token once called from the timer method
    private class RegenerateTask extends TimerTask{

        public void run(){
            generateToken();
        }
    }

        //same timer method but this time it is used every 1 minute to change the share price
    public void changeSharePrice()
    {
        Timer sharePriceTimer = new Timer();
        sharePriceTimer.schedule( new resetSharePrice(), 0, 1*60*1000);
    }

    //method which changes the share price eietehr up or down depending on random number
    public void changePrice(){

        for(int x=0; x<allShares.size(); x++)
        {
            double randomVariable = Math.random()*10;//random number between 1 and 10
            if(randomVariable<=5)//if random number lessthat or equal to 5 then increase share price
            {
                allShares.get(x).increaseSharePrice();
                
            }
            else{//else decrease share price
                allShares.get(x).decreaseSharePrice();
                
                
            }
            
        }
    }

    //class that is called by the timer to reset the share price
    public class resetSharePrice extends TimerTask{

        public void run(){
            changePrice();
        }
    }

    public String getCurrentToken()
    {
        return currentToken;
    }

    //method to deposit funds into the account
     public boolean depositFunds(String userToken, double anAmount)throws RemoteException
    {
        if(userToken.equals(currentToken))//if tokens match allow process to take place
        {
            accountBalance += anAmount; //add amount to bank account
            System.out.printf("Transaction succesful your balance is now %f\n", accountBalance);
            return true;
        }
        else
        {
            System.out.println("Your token has expired please log in to continue trading");
            return false;
        }
    }

    //method to withdraw money from the account
    public boolean withdraw(String userToken, double anAmount)
    {
        if(currentToken.equals(userToken))
        {
            if(anAmount <= accountBalance)//dom't let account go into overdraft
            {
                accountBalance -= anAmount;
                System.out.println("Transaction succesful " + anAmount + " has been withdrawn from your account" );
                System.out.println("Your account balance is: " + accountBalance);
            }
            else{
                System.out.println("Not enough funds to withdraw from this account");
            }
            return true;
        }
        else{
            System.out.println("Token has expired");
            return false;
        }
    }

    //method to buy shares
    public boolean buyShares(String userToken, int amountShares, String name)
    {
        boolean correctShare = false;
        if(currentToken.equals(userToken))
        {
            for(int x= 0; x < allShares.size(); x++)//check through names of all shares to see if we got a match
            {
                Share checkShare = allShares.get(x);
                
                if(checkShare.getName().equals(name))
                {
                    correctShare = true;//set boolean to true to show we got a match

                    if( amountShares > allShares.get(x).getQuantity()){ //make sure there is the required shares on the market
                        System.out.println("There is not enough shares on the market for you to purchase");
                    }

                    else{

                        double costOfTransaction = amountShares*allShares.get(x).getPrice();

                        if(costOfTransaction> accountBalance){
                            System.out.println("You do not have the required funds for this transaction");
                        }

                        else{
                            ShareHoldingImpl share = new ShareHoldingImpl(allShares.get(x).getName(),  amountShares, allShares.get(x).getPrice());
                            userShares.add(share); //add share to your holdings
                            accountBalance -= costOfTransaction; //reduce accountbalance by required amount
                            allShares.get(x).reduceVolume(amountShares); //reduce amount of shares on the market
                            System.out.println("Transaction has been succesful you have bought " + amountShares + " shares of " + allShares.get(x).getName() + " for " + allShares.get(x).getPrice());

                            
                        }
                        
                    }
                }
            } 

            if(!correctShare)  //if boolean is not set true then the name must be incorrect that user entered
            {
                System.out.println("Name of share is not on the system please recheck spelling and try again");
            }
            return true;
            
        }
        else{
            System.out.println("Token has expired please log in to continue trading");
            return false;
        }

    }

    //method to sell the shares
    public boolean sellShares(String token, String name, int quantity)throws RemoteException
    {
        //create the object of share holding to be removed
        ShareHoldingImpl shareRemoved = new ShareHoldingImpl("", 0,0);
        boolean correctShare2 = false;

        if(currentToken.equals(userToken))
        {

            for(ShareHoldingImpl share : userShares)//iterate through array to check for the share name
            {
                int x=0;
                
                if(share.getName().equals(name))
                {
                    
                    correctShare2 = true;
                    shareRemoved = userShares.get(x);
                    double sharePrice = 0;

                    for(int y=0; y<allShares.size(); y++)
                    {
                        if(allShares.get(y).getName().equals(name))
                        {   
                            sharePrice = allShares.get(y).getPrice();//find the share price at the current market value from uswr shares
                            allShares.get(y).increaseVolume(quantity);
                        }
                    }

                    accountBalance += sharePrice*quantity;//add this amount to your bank account
                    System.out.println("You have sold " + quantity + " of " + name + " for " + sharePrice);
                }
            }
            if(correctShare2)
            {   //if getting rid of all of chosen shares then delete from list
                if(shareRemoved.getQuantity() == quantity)
                {
                    userShares.remove(shareRemoved);//if you are selling all of this share then remove from list
                }
                else{//if getting rid of a few of chosen shares then reset the quantity of this share
                    int newQuantity = shareRemoved.getQuantity() - quantity;
                    shareRemoved.setQuantity(newQuantity);;
                }
                
            }

            else if(!correctShare2)
            {
                System.out.println("The name of this share doesn't exist please check that your spelling is correct");
            }
            return true;

        }
        else{
            System.out.println("Your token has expired please log in to continue trading");
            return false;
        }
    }


    public List<ShareHoldingImpl> getUserShares(String userToken)//method that prints out how  much holdings the customer has
    {
        if(userToken.equals(currentToken))
        {
            return userShares;
        }
        else{
            System.out.println("Your token has expireed please login to continue trading");
            List<ShareHoldingImpl> tokenExpired = new ArrayList<ShareHoldingImpl>();
            return tokenExpired;
        }
       
    }

    public double getUserBalance()//method that gets the users balance
    {
        return accountBalance;
    }

    public static void main(String args[]) throws FileNotFoundException, IOException
    {
        //read in from CSV file all of the different shares
        String readInFromCSV = "";
        List<ShareImpl> allShares = new ArrayList<ShareImpl>();
        BufferedReader buffReader = new BufferedReader(new FileReader("allShares.csv"));
        while((readInFromCSV = buffReader.readLine()) != null)
        {
            String[] share = readInFromCSV.split(",");
            int quantity = Integer.parseInt(share[1]); 
            double price = Float.parseFloat(share[2]);
            int timeLeft = Integer.parseInt(share[3]);
            ShareImpl newShare = new ShareImpl(share[0],  price, quantity, timeLeft);
            allShares.add(newShare);//add all share objects to list until there is no ;ines to read in
            
        }   
        System.out.println(allShares);
        buffReader.close();

        
        try{//create shareserver object
            ShareServerImpl shareServer = new ShareServerImpl("userName", "passWord", allShares);
            shareServer.changeSharePrice();//start the iterating of each shareprice
            System.out.println("Instance of share server created");
            ShareServer stub = (ShareServer)UnicastRemoteObject.exportObject(shareServer,  0);//create instance of object
            Registry registry = LocateRegistry.createRegistry(9100);//locate it on port on the regustry
            registry.bind("serverStub", stub);
            System.out.println("name rebind completed");
            System.out.println("Server ready for requests");
        }
        catch(Exception exc)
        {
            System.out.println("Error in main - " + exc.toString());
        }

        
    }
    
    
}
