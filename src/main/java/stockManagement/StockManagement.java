/**
 * @Author: Vinit Kumar
 * @created: 05-JAN-2022
 * Stock Management for 1 user using json
 */
//import Packages
package stockManagement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Scanner;

public class StockManagement {
    public static Scanner sc = new Scanner(System.in);
    public static JSONArray stockList = new JSONArray();

    public static void main(String[] args) {
        System.out.println("********* Stock Management *********");
        Amount userA = new Amount(5000, "User A");
        transactionUtility(); //to clear and update logs on each new run
        getInputFromUser(userA); //menu display
    }

    /**
     * Displays menu options Stock and User Balance
     * call methods based on options selected using switch case
     */
    private static void getInputFromUser(Amount userA) {
        System.out.println("Which operation do you want to perform ?\n1.Add Stock \n2.Print stock report \n3.Buy " +
                "Stock \n4.Sell Stock \n5.View Balance \n6.Credit Amount \n7.Debit Amount \n8.Exit");
        int ch = sc.nextInt();
        switch (ch) {
            case 1:
                addStock(userA);
                break;
            case 2:
                printStock(userA);
                break;
            case 3:
                buyStock(userA);
                break;
            case 4:
                sellStock(userA);
                break;
            case 5:
                System.out.println("Balance is: " + userA.getAmount());
                getInputFromUser(userA);
                break;
            case 6:
                userA.amountCredit();
                getInputFromUser(userA);
                break;
            case 7:
                userA.amountDebit();
                getInputFromUser(userA);
                break;
            case 8:
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice");
                break;
        }

    }

    /**
     * Prints available stocks
     * Read JSON and parse in Array List
     * iterate and print
     */
    public static void printStock(Amount userA) {
        System.out.println("***** print stock *******");
        JSONParser jsonParser = new JSONParser();
        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("src/data/stock.json"));
            for (int i = 0; i < jsonArray.size(); i++) {
                System.out.printf("******** Stock %s ********\n", i + 1);
                JSONObject obj = (JSONObject) jsonArray.get(i);
                String name = (String) obj.get("name");
                long shares = (long) obj.get("no_of_shares");
                Double price = (Double) obj.get("price");
                System.out.println("Stock Name : " + name);
                System.out.println("Number of Shares : " + shares);
                System.out.println("Stock price : " + price);
            }

        } catch (FileNotFoundException e) {
//e.printStackTrace();
            System.out.println("File Not Found");
        } catch (IOException e) {
//e.printStackTrace();
            System.out.println("File IO Exception");
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        getInputFromUser(userA);
    }

    /**
     * Adds stocks to JSON
     * creates new json object with user input
     * adds new stock to Array
     * creates log
     * add new Stock by file operation
     */
    public static void addStock(Amount userA) {
        System.out.println("******* Add stock *******");
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Stock Name : ");
        String stockName = sc.nextLine();
        System.out.println("Enter number of shares: ");
        int noOfShares = sc.nextInt();
        System.out.println("Enter share price: ");
        double sharePrice = sc.nextDouble();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", stockName); // Assiging the accepted value to the JSON Object
        jsonObject.put("no_of_shares", noOfShares);
        jsonObject.put("price", sharePrice);
        stockList.add(jsonObject); // Adding the JSON Object into the JSON Array..
        transaction("Add", stockName, (long) noOfShares, sharePrice);
        try {
            FileWriter file = new FileWriter("src/data/stock.json");
            file.write(stockList.toJSONString());
            file.close();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("Stock Added Successfully!!!");
        getInputFromUser(userA);
    }

    /**
     * Increases no of shares when a stock is bought
     * parse json for available stocks list
     * takes input for stock
     * validates
     * on successfully validation,buys shares
     * creates log by method
     * deduct amount from user account
     */
    public static void buyStock(Amount userA) {
        System.out.println("***** buy stock *******");
        System.out.println("Choose stock: ");
        int size = 0; //variable declaration
        JSONParser jsonParser = new JSONParser(); // object creation
        /**
         * parse json and display menu
         */
        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("src/data/stock.json")); // read stock
            // from json
            size = jsonArray.size(); //get size of stock
            for (int i = 0; i < jsonArray.size(); i++) { // create menu of stock to choose from
                JSONObject obj = (JSONObject) jsonArray.get(i);
                System.out.printf("%s. Name: %s, Available share: %s, at Buy Price %s\n", i + 1,
                        (String) obj.get("name"),
                        (long) obj.get("no_of_shares"), (Double) obj.get("price"));
            }
            /**
             * take user input for stock and validate
             */
            int stock = sc.nextInt();// inputs stock to buy
            if ((stock - 1) > size - 1 && stock != 0) { //validates if stock exits
                System.out.println("Stock not valid"); // invalid stock
            } else { // valid stock
                System.out.println("Enter no shares to be bought: ");
                /**
                 * take amount user want to buy and fetches related data from json
                 */
                long buyShares = sc.nextLong(); // input amount of shares
                JSONObject updateStock = (JSONObject) jsonArray.get(stock - 1); // element to be updated
                double price = (Double) updateStock.get("price");
                double amountUsed = buyShares * price;
                /**
                 * validate user balance
                 */
                if (amountUsed > userA.getAmount()) { // validate if user has balance to buy stock
                    System.out.printf("Total cost of %s share(s) is more then account balance.", buyShares);
                    System.out.println("Please reduce no of stock or increase credit."); // insufficient balance
                } else {
                    /**
                     * update logs,amount of user and shares
                     */
                    transaction("Buy", (String) updateStock.get("name"), buyShares, amountUsed); // create log
                    updateStock.put("no_of_shares", buyShares + (long) updateStock.get("no_of_shares")); // update value
                    userA.amountBuy(amountUsed);// update amount
                    System.out.println("Bought Successfully"); // successfully bought message
                }
                /**
                 * update shares in json
                 */
                try {
                    FileWriter file = new FileWriter("src/data/stock.json"); // updates stock json with new shares
                    file.write(jsonArray.toJSONString());
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (IOException e) {
            System.out.println("File IO Exception");
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        getInputFromUser(userA);
    }

    /**
     * Decreases no of shares when a stock is sold
     * parse json for available stocks list
     * takes input for stocks
     * validates
     * on successfully validation, sells shares with profit of 20 per stock
     * creates logs by method
     * added amount from user account
     */
    public static void sellStock(Amount userA) {
        System.out.println("***** sell stock *******");
        System.out.println("Choose stock: ");
        int sellFactor = 20, size = 0; //variable declaration
        JSONParser jsonParser = new JSONParser(); //json object
        /**
         * parse json and display menu
         */
        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("src/data/stock.json")); // read json
            size = jsonArray.size(); //gets size of stock
            for (int i = 0; i < jsonArray.size(); i++) { // create menu to choose stock from
                JSONObject obj = (JSONObject) jsonArray.get(i);
                System.out.printf("%s. Name: %s, Available share: %s, at Sell Price %s\n", i + 1,
                        (String) obj.get("name"),
                        (long) obj.get("no_of_shares"), ((Double) obj.get("price") + sellFactor)); //shows updated price
            }
            /**
             * take user input for stock and validate
             */
            int stock = sc.nextInt(); //input stocks
            if ((stock - 1) > size - 1 && stock != 0) { // validate stock
                System.out.println("Stock not valid"); //invalid stocks
            } else {
                System.out.println("Enter no shares to be Sold: ");
                /**
                 * take amount user want to buy and fetches related data from json
                 */
                long sellShares = sc.nextLong(); // input stock to be sold
                JSONObject updateStock = (JSONObject) jsonArray.get(stock - 1); // element to be updated
                long availableShares = (long) updateStock.get("no_of_shares");
                double price = (Double) updateStock.get("price");
                /**
                 * validate available shares
                 */
                if (sellShares > availableShares) { //validates if shares are available to be sold
                    System.out.printf("Total available share(s) is less then %s shares to be sold.", sellShares);
                    System.out.println("Please reduce no of shares to be sold."); // insufficient shares
                } else {
                    /**
                     * update logs,amount of user and shares
                     */
                    transaction("Sell", (String) updateStock.get("name"), sellShares,
                            (price + sellFactor) * sellShares); //create log
                    updateStock.put("no_of_shares", (long) updateStock.get("no_of_shares") - sellShares); //update share
                    userA.amountSell((price + sellFactor) * sellShares); // update user balance
                    System.out.println("Sold Successfully"); //  successfully sold message
                }
                try {
                    /**
                     * update shares in json
                     */
                    FileWriter file = new FileWriter("src/data/stock.json"); //update json
                    file.write(jsonArray.toJSONString());
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found");
        } catch (IOException e) {
            System.out.println("File IO Exception");
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        getInputFromUser(userA);
    }

    /**
     * Creates transaction logs on successfully addition, buy and sell of stocks
     * creates json object of Type of operation,share name, no of shares, total price bought,sold or added with
     * timestamp
     */
    public static void transaction(String operation, String name, long shares, double price) {
        Date currentDate = new Date();
        long time = currentDate.getTime();
        Timestamp ts = new Timestamp(time); //generate time stamp
        String timeString = ts.toString(); // change time to string
        JSONParser jsonParser = new JSONParser(); // create object
        try {
            JSONArray jsonArray = (JSONArray) jsonParser.parse(new FileReader("src/data/logs.json")); // parse logs json
            JSONObject jsonObject = new JSONObject(); // create new log
            jsonObject.put("Operation", operation); // Assiging the accepted value to the JSON Object
            jsonObject.put("Name", name);
            jsonObject.put("Shares", shares);
            jsonObject.put("Price", price);
            jsonObject.put("Time", timeString);
            jsonArray.add(jsonObject); // Adding the JSON Object into the JSON Array.
            try {
                FileWriter file = new FileWriter("src/data/logs.json"); // update json with log
                file.write(jsonArray.toJSONString());
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates transaction logs for new run
     * initialize logs with created date and time of file
     */
    public static void transactionUtility() {
        JSONArray initLogs = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("Created on", (new Date()).toString()); // Assigning the accepted value to the JSON Object
        initLogs.add(jsonObject);
        try {
            FileWriter file = new FileWriter("src/data/logs.json"); //update json for new value
            file.write(initLogs.toJSONString());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}