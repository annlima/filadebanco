
package javaapplication33;

import java.util.LinkedList;
import java.util.Queue;

public class PoissonSimulation {

    private static final double LAMBDA = 5;
    private static final int NUM_CASHIERS = 4;

    public static void main(String[] args) {
        for (int i = 0; i < 8; i++) {  
            int arrivals = poissonRandom(LAMBDA);
            System.out.println("In hour " + (i + 1) + ", " + arrivals + " customers arrived.");
            serveCustomers(arrivals);
        }
    }

    public static int poissonRandom(double lambda) {
        int k = 0;
        double p = 1.0;
        double expLambda = Math.exp(-lambda);

        do {
            k++;
            p *= Math.random();
        } while (p >= expLambda);

        return k - 1;
    }

    public static void serveCustomers(int numCustomers) {
        Queue<String> customerQueue = new LinkedList<>();
        int[] customersServedByCashier = new int[NUM_CASHIERS];

        for (int i = 0; i < numCustomers; i++) {
            customerQueue.offer("Customer " + (i + 1));
        }

        Cashier[] cashiers = new Cashier[NUM_CASHIERS];
        for (int i = 0; i < NUM_CASHIERS; i++) {
            cashiers[i] = new Cashier("Cashier " + (i + 1));
        }

        while (!customerQueue.isEmpty()) {
            for (int i = 0; i < cashiers.length; i++) {
                Cashier cashier = cashiers[i];
                if (!cashier.isBusy() && !customerQueue.isEmpty()) {
                    String customer = customerQueue.poll();
                    cashier.serveCustomer(customer);
                    customersServedByCashier[i]++;
                }
            }
        }

        for (Cashier cashier : cashiers) {
            cashier.waitForCompletion();
        }

        System.out.println("Customers served: " + numCustomers);
        System.out.println("Customers in queue: " + customerQueue.size());
        for (int i = 0; i < NUM_CASHIERS; i++) {
            System.out.println(cashiers[i].getName() + " served: " + customersServedByCashier[i] + " customers.");
        }
    }
}

class Cashier {
    private String name;
    private boolean busy;

    public Cashier(String name) {
        this.name = name;
        this.busy = false;
    }

    public synchronized boolean isBusy() {
        return busy;
    }

    public synchronized void serveCustomer(String customer) {
        busy = true;
        System.out.println(name + " is serving: " + customer);
        try {
            int serviceTime = (int) (Math.random() * 10 + 1) * 1000;
            Thread.sleep(serviceTime); 
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        busy = false;
        System.out.println(name + " finished serving: " + customer);
        notify();
    }

    public synchronized void waitForCompletion() {
        while (busy) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getName() {
        return name;
    }
}
