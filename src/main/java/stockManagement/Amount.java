/**
 * @Author: Vinit Kumar
 * @created: 05-JAN-2022
 * Manges account for user
 */
package stockManagement;

import java.util.Scanner;

public class Amount {
    //variable declaration
    private double amount = 0;
    private String user;
    Scanner sc = new Scanner(System.in);

    //constructor
    Amount(double amount, String user) {
        this.amount = amount;
        this.user = user;
    }

    //getters and setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    /**
     * adds amount to user account
     * takes input and add
     */
    public void amountCredit() {
        System.out.printf("Current balance for %s is %s ", this.user, this.amount);
        System.out.println("Enter amount to be credited: ");
        double amount = sc.nextDouble();
        this.amount = amount + this.amount;
        System.out.println("Your new Balance is:" + this.amount);
    }

    /**
     * deduct amount from user account
     * takes input
     * validates
     * subtracts amount
     */
    public void amountDebit() {
        System.out.printf("Current balance for %s is %s ", this.user, this.amount);
        System.out.println("Enter amount to be debited: ");
        double amount = sc.nextDouble();
        if (amount <= this.amount) {
            this.amount = this.amount - amount;
            System.out.println("Your new Balance is:" + this.amount);
        } else {
            System.out.println("Account does not have sufficient balance. Please credit or input valid amount");
        }
    }

    /**
     * updates amount when user buy
     */
    public void amountBuy(double amount) {
        this.amount = this.amount - amount;
    }

    /**
     * updates amount when user sell
     */
    public void amountSell(double amount) {
        this.amount = this.amount + amount;
    }
}
